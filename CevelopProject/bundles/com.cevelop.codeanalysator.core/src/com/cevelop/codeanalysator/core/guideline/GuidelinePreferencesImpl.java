package com.cevelop.codeanalysator.core.guideline;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.util.SafeRunnable;

import ch.hsr.ifs.iltis.core.core.preferences.IPropertyAndPreferenceHelper;
import ch.hsr.ifs.iltis.core.core.preferences.PropertyAndPreferenceHelper;


public class GuidelinePreferencesImpl implements IGuidelinePreferences {

    private final ListenerList<IPreferencesChangeListener> changeListeners = new ListenerList<>();
    private IPropertyAndPreferenceHelper                   propAndPerfHelper;
    private GuidelineProjectPreferences                    guidelineWorkspacePreferences;
    private Map<IProject, GuidelineProjectPreferences>     guidelinePreferenceByProject;

    public GuidelinePreferencesImpl() {
        guidelineWorkspacePreferences = new GuidelineProjectPreferences(null);
        guidelinePreferenceByProject = new HashMap<>();
    }

    public void startListening(IPropertyAndPreferenceHelper propAndPerfHelper) {
        if (propAndPerfHelper == null) throw new IllegalArgumentException("propAndPerfHelper must not be null.");
        if (this.propAndPerfHelper != null) throw new IllegalStateException("cannot listen to more than one PropertyAndPreferenceHelper.");

        this.propAndPerfHelper = propAndPerfHelper;
        guidelineWorkspacePreferences.startListening();
        guidelinePreferenceByProject.values().stream() //
                .forEach(GuidelineProjectPreferences::startListening);
    }

    @Override
    public boolean isGuidelineEnabledForWorkspace(IGuideline guideline) {
        if (guideline == null) throw new IllegalArgumentException("guideline must not be null.");

        return guidelineWorkspacePreferences.isGuidelineEnabled(guideline);
    }

    @Override
    public boolean isGuidelineEnabledForProject(IGuideline guideline, IProject project) {
        if (guideline == null) throw new IllegalArgumentException("guideline must not be null.");
        if (project == null) throw new IllegalArgumentException("project must not be null.");

        return getGuidelinePreferencesForProject(project).isGuidelineEnabled(guideline);
    }

    @Override
    public int getGuidelinePriorityForWorkspace(IGuideline guideline) {
        if (guideline == null) throw new IllegalArgumentException("guideline must not be null.");

        return guidelineWorkspacePreferences.getGuidelinePriority(guideline);
    }

    @Override
    public int getGuidelinePriorityForProject(IGuideline guideline, IProject project) {
        if (guideline == null) throw new IllegalArgumentException("guideline must not be null.");
        if (project == null) throw new IllegalArgumentException("project must not be null.");

        return getGuidelinePreferencesForProject(project).getGuidelinePriority(guideline);
    }

    @Override
    public void addPreferencesChangeListener(IPreferencesChangeListener listener) {
        changeListeners.add(listener);
    }

    public void stopListening() {
        guidelineWorkspacePreferences.stopListening();
        guidelinePreferenceByProject.values().stream() //
                .forEach(GuidelineProjectPreferences::stopListening);
    }

    private GuidelineProjectPreferences getGuidelinePreferencesForProject(IProject project) {
        GuidelineProjectPreferences guidelineProjectPreferences = guidelinePreferenceByProject.get(project);
        if (guidelineProjectPreferences == null) {
            // replace guidelinePreferenceByProject with new instance to assure thread safety
            // and to prevent concurrent accesses from reading a partial map
            Map<IProject, GuidelineProjectPreferences> newGuidelinePreferencesByProject = new HashMap<>(guidelinePreferenceByProject);
            guidelineProjectPreferences = new GuidelineProjectPreferences(project);
            newGuidelinePreferencesByProject.put(project, guidelineProjectPreferences);
            guidelinePreferenceByProject = newGuidelinePreferencesByProject;
            if (propAndPerfHelper != null) {
                guidelineProjectPreferences.startListening();
            }
        }
        return guidelineProjectPreferences;
    }

    private void firePreferencesChangeEvent() {
        for (IPreferencesChangeListener listener : changeListeners) {
            SafeRunnable.run(() -> listener.guidelinePreferencesChange());
        }
    }

    private class GuidelineProjectPreferences implements IPropertyChangeListener {

        private final IProject       project;
        private IPreferenceStore     workspacePreferenceStore;
        private IPreferenceStore     projectPreferenceStore;
        private Map<String, Integer> priorityByGuidelineId;

        private GuidelineProjectPreferences(IProject project) {
            this.project = project;
            priorityByGuidelineId = new HashMap<>();
        }

        private void startListening() {
            workspacePreferenceStore = propAndPerfHelper.getWorkspacePreferences();
            workspacePreferenceStore.addPropertyChangeListener(this);
            if (project != null) {
                projectPreferenceStore = propAndPerfHelper.getProjectPreferences(project);
                projectPreferenceStore.addPropertyChangeListener(this);
            }
            readPreferenceList();
        }

        private void readPreferenceList() {
            // replace priorityByGuidelineId with new instance to assure thread safety
            // and to prevent concurrent accesses from reading a partial map
            String preferenceList = propAndPerfHelper.getString(GUIDELINE_PREFERENCE_NAME, project);
            String[] guidelineIds = preferenceList.split(GUIDELINE_PREFERENCE_ID_SEPARATOR);
            Map<String, Integer> newPriorityByGuidelineId = new HashMap<>();
            for (String guidelineId : guidelineIds) {
                newPriorityByGuidelineId.put(guidelineId, newPriorityByGuidelineId.size());
            }
            replacePriorityByGuidelineId(newPriorityByGuidelineId);
        }

        private void replacePriorityByGuidelineId(Map<String, Integer> newPriorityByGuidelineId) {
            priorityByGuidelineId = newPriorityByGuidelineId;

            firePreferencesChangeEvent();
        }

        private boolean isGuidelineEnabled(IGuideline guideline) {
            return priorityByGuidelineId.containsKey(guideline.getId());
        }

        private int getGuidelinePriority(IGuideline guideline) {
            return priorityByGuidelineId.getOrDefault(guideline.getId(), -1);
        }

        private void stopListening() {
            workspacePreferenceStore.removePropertyChangeListener(this);
            if (projectPreferenceStore != null) {
                projectPreferenceStore.removePropertyChangeListener(this);
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            String property = event.getProperty();
            if (property.equals(GUIDELINE_PREFERENCE_NAME) || property.equals(PropertyAndPreferenceHelper.P_USE_PROJECT_PREFERENCES)) {
                readPreferenceList();
            }
        }
    }
}
