package com.cevelop.ctylechecker.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

import ch.hsr.ifs.iltis.core.preferences.PropertyAndPreferenceHelper;

import com.cevelop.ctylechecker.Activator;
import com.cevelop.ctylechecker.ids.IdHelper;


public class PropAndPrefHelper extends PropertyAndPreferenceHelper {

    private static PropAndPrefHelper helper = new PropAndPrefHelper();

    public static PropAndPrefHelper getInstance() {
        return helper;
    }

    private static final IPreferenceStore workspacePreferences = Activator.getDefault().getPreferenceStore();

    @Override
    public IPreferenceStore getWorkspacePreferences() {
        return workspacePreferences;
    }

    @Override
    public String getPreferenceIdQualifier() {
        return IdHelper.DEFAULT_QUALIFIER;
    }
}
