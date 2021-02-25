package com.cevelop.ctylechecker.service;

import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.IPreferenceStore;

import com.cevelop.ctylechecker.domain.ConfigurationType;
import com.cevelop.ctylechecker.domain.IConfiguration;
import com.cevelop.ctylechecker.domain.IStyleguide;


public interface IConfigurationService {

    String CONFIG_SETTING_KEY   = "ctylechecker.config.setting";
    String CONFIG_REFERENCE_KEY = "ctylechecker.config.reference";
    String CONFIG_KEY           = "ctylechecker.config";

    IConfiguration loadConfiguration(IEclipsePreferences projectPreferences);

    IConfiguration loadConfiguration(IEclipsePreferences projectPreferences, ConfigurationType cSetting);

    IConfiguration loadConfiguration(IPreferenceStore workspacePreferences);

    void saveConfiguration(IEclipsePreferences projectPreferences, IConfiguration config);

    void saveConfiguration(IPreferenceStore workspacePreferences, IConfiguration config);

    IStyleguide loadStyleguide(IProject project);

    IConfiguration loadConfiguration(IProject project);

    IConfiguration getDefaultConfiguration();

    void restoreDefaults(IConfiguration pConfig);

    Optional<IEclipsePreferences> getProjectPreferencesFromAdaptable(IAdaptable resource);

    /**
     * This method is necessary when one tries to access the property preferences
     * page via a File, i.e. Alt + Enter shortcut
     *
     * @return project of the current active editor
     */
    Optional<IProject> getActiveProject();

    /**
     * This method is necessary to manually kick off Codan Checkers
     * after a save operation.
     *
     * @return file of the current active editor
     */
    Optional<IFile> getActiveFile();
}
