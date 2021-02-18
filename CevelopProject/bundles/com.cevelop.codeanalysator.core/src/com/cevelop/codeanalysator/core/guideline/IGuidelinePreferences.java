package com.cevelop.codeanalysator.core.guideline;

import org.eclipse.core.resources.IProject;

import com.cevelop.codeanalysator.core.helper.CoreIdHelper;


public interface IGuidelinePreferences {

    static final String GUIDELINE_PREFERENCE_NAME         = CoreIdHelper.GUIDELINE_SETTING_ID;
    static final String GUIDELINE_PREFERENCE_ID_SEPARATOR = ", ";

    boolean isGuidelineEnabledForWorkspace(IGuideline guideline);

    boolean isGuidelineEnabledForProject(IGuideline guideline, IProject project);

    int getGuidelinePriorityForWorkspace(IGuideline guideline);

    int getGuidelinePriorityForProject(IGuideline guideline, IProject project);

    void addPreferencesChangeListener(IPreferencesChangeListener listener);

    @FunctionalInterface
    public interface IPreferencesChangeListener {

        void guidelinePreferencesChange();
    }
}
