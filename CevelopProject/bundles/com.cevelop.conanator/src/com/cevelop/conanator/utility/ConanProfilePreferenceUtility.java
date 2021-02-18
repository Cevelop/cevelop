package com.cevelop.conanator.utility;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.cevelop.conanator.Activator;
import com.cevelop.conanator.preferences.PreferenceConstants;


public class ConanProfilePreferenceUtility {

    public static String getActiveProfile(IProject project) {
        IEclipsePreferences preferenceNode = null;
        String profile = "";

        if (project != null) preferenceNode = new ProjectScope(project).getNode(Activator.PLUGIN_ID);

        if (preferenceNode != null && preferenceNode.getBoolean(PreferenceConstants.P_ENABLE_PROJECT_SETTINGS, false)) {
            profile = preferenceNode.get(PreferenceConstants.P_PROJECT_PROFILE, "");
        } else {
            profile = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_WORKSPACE_PROFILE);
        }

        return profile;
    }
}
