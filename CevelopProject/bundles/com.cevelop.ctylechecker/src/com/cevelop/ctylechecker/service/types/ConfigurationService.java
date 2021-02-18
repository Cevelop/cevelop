package com.cevelop.ctylechecker.service.types;

import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.cevelop.ctylechecker.domain.ConfigurationType;
import com.cevelop.ctylechecker.domain.IConfiguration;
import com.cevelop.ctylechecker.domain.IStyleguide;
import com.cevelop.ctylechecker.domain.types.Configuration;
import com.cevelop.ctylechecker.service.IConfigurationService;
import com.cevelop.ctylechecker.service.factory.StyleguideFactory;
import com.cevelop.ctylechecker.service.util.ConfigurationMapper;


public class ConfigurationService implements IConfigurationService {

    @Override
    public IConfiguration loadConfiguration(IEclipsePreferences projectPreferences) {
        String setting = projectPreferences.get(CONFIG_SETTING_KEY, "");
        if (setting.isEmpty()) {
            setting = ConfigurationType.WORKSPACE.toString();
            projectPreferences.put(CONFIG_SETTING_KEY, setting);
        }
        ConfigurationType cSetting = ConfigurationType.valueOf(setting);
        return loadConfiguration(projectPreferences, cSetting);
    }

    @Override
    public IConfiguration loadConfiguration(IEclipsePreferences projectPreferences, ConfigurationType cSetting) {
        if (cSetting != ConfigurationType.PROJECT) {
            IConfiguration cConfig = loadConfiguration(CtylecheckerRuntime.getPreferenceStore());
            if (cSetting == ConfigurationType.REFERENCE) {
                cConfig.setSetting(cSetting);
                String reference = projectPreferences.get(CONFIG_REFERENCE_KEY, "");
                if (reference.isEmpty()) {
                    reference = IStyleguide.GOOGLE;
                    projectPreferences.put(CONFIG_REFERENCE_KEY, reference);
                }
                Optional<IStyleguide> oActiveStyleguide = cConfig.findStyleGuide(reference);
                if (oActiveStyleguide.isPresent()) {
                    cConfig.setActiveStyleguide(oActiveStyleguide.get());
                } else {
                    projectPreferences.put(CONFIG_REFERENCE_KEY, cConfig.getActiveStyleguide().getName());
                }
            } else {
                cConfig.setSetting(cSetting);
            }
            return cConfig;
        }
        String config = projectPreferences.get(CONFIG_KEY, "");
        if (config.isEmpty()) {
            IConfiguration cConfig = loadConfiguration(CtylecheckerRuntime.getPreferenceStore());
            projectPreferences.put(CONFIG_KEY, toJson(cConfig));
            projectPreferences.put(CONFIG_SETTING_KEY, cConfig.getSetting().toString());
            return cConfig;
        }

        IConfiguration cConfig = Optional.of(fromJson(config)).orElse(createEmptyConfiguration());
        cConfig.setSetting(cSetting);
        return cConfig;
    }

    private IConfiguration fromJson(String config) {
        return ConfigurationMapper.fromJson(config, IConfiguration.class);
    }

    private String toJson(IConfiguration cConfig) {
        return ConfigurationMapper.toJson(cConfig);
    }

    @Override
    public IConfiguration loadConfiguration(IPreferenceStore workspacePreferences) {
        String config = workspacePreferences.getString(CONFIG_KEY);
        if (config.isEmpty()) {
            IConfiguration cConfig = createEmptyConfiguration();
            workspacePreferences.putValue(CONFIG_KEY, toJson(cConfig));
            return cConfig;
        }
        return Optional.of(fromJson(config)).orElse(createEmptyConfiguration());
    }

    @Override
    public void saveConfiguration(IEclipsePreferences projectPreferences, IConfiguration config) {
        if (!config.isWorkspaceSetting()) {
            if (config.isReferenceSetting()) {
                projectPreferences.put(CONFIG_REFERENCE_KEY, config.getActiveStyleguide().getName());
            } else {
                projectPreferences.put(CONFIG_KEY, toJson(config));
            }
        }
        projectPreferences.put(CONFIG_SETTING_KEY, config.getSetting().toString());
    }

    @Override
    public void saveConfiguration(IPreferenceStore workspacePreferences, IConfiguration config) {
        workspacePreferences.putValue(CONFIG_KEY, toJson(config));
    }

    @Override
    public IStyleguide loadStyleguide(IProject project) {
        return loadConfiguration(project).getActiveStyleguide();
    }

    @Override
    public IConfiguration loadConfiguration(IProject project) {
        Optional<IEclipsePreferences> oPrefs = getProjectPreferences(project);
        IConfiguration config = null;
        if (oPrefs.isPresent()) {
            config = loadConfiguration(oPrefs.get());
        } else {
            config = createEmptyConfiguration();
        }
        return config;
    }

    @Override
    public IConfiguration getDefaultConfiguration() {
        IConfiguration defaultConfig = createEmptyConfiguration();
        IStyleguide googleStyleguide = StyleguideFactory.createGoogleStyleguide();
        defaultConfig.addStyleguide(googleStyleguide);
        defaultConfig.addStyleguide(StyleguideFactory.createCanonicalStyleguide());
        defaultConfig.addStyleguide(StyleguideFactory.createGeosoftStyleguide());
        defaultConfig.setActiveStyleguide(googleStyleguide);
        return defaultConfig;
    }

    @Override
    public void restoreDefaults(IConfiguration pConfig) {
        updateConfiguration(pConfig, getDefaultConfiguration());
    }

    private void updateConfiguration(IConfiguration pSource, IConfiguration pTarget) {
        pSource.setActiveStyleguide(pTarget.getActiveStyleguide());
        pSource.setSetting(pTarget.getSetting());
        pSource.setStyleguides(pTarget.getStyleguides());
        pSource.isEnabled(pTarget.isEnabled());
    }

    @Override
    public Optional<IEclipsePreferences> getProjectPreferencesFromAdaptable(IAdaptable resource) {
        Optional<IProject> oProject = getProjectFromAdaptable(resource);
        if (oProject.isPresent()) {
            IScopeContext projectScope = new ProjectScope(oProject.get());
            return Optional.of(projectScope.getNode(CtylecheckerRuntime.getPluginId()));
        }
        return Optional.empty();
    }

    private Optional<IEclipsePreferences> getProjectPreferences(IProject project) {
        if (project != null) {
            IScopeContext projectScope = new ProjectScope(project);
            return Optional.of(projectScope.getNode(CtylecheckerRuntime.getPluginId()));
        }
        return Optional.empty();
    }

    private Optional<IProject> getProjectFromAdaptable(IAdaptable resource) {
        if (resource != null) {
            IProject project = (IProject) resource.getAdapter(IProject.class);
            if (project != null) {
                return Optional.of(project);
            }
        }
        return getActiveProject();
    }

    @Override
    public Optional<IProject> getActiveProject() {
        Optional<IFile> oFile = getActiveFile();
        if (oFile.isPresent()) {
            return Optional.of(oFile.get().getProject());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<IFile> getActiveFile() {
        IWorkbench iWorkbench = PlatformUI.getWorkbench();
        if (iWorkbench == null) return Optional.empty();
        IWorkbenchWindow iWorkbenchWindow = iWorkbench.getActiveWorkbenchWindow();
        if (iWorkbenchWindow == null) return Optional.empty();
        IWorkbenchPage iWorkbenchPage = iWorkbenchWindow.getActivePage();
        if (iWorkbenchPage == null) return Optional.empty();
        IEditorPart editorPart = iWorkbenchPage.getActiveEditor();
        if (editorPart == null) return Optional.empty();
        IFileEditorInput input = (IFileEditorInput) editorPart.getEditorInput();
        if (input == null) return Optional.empty();
        IFile file = input.getFile();
        return Optional.ofNullable(file);
    }

    public IConfiguration createEmptyConfiguration() {
        return new Configuration();
    }
}
