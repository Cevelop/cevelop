package com.cevelop.gslator.charwarsstub.stubadapter;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;


public class CharWarsPlugin extends AbstractUIPlugin {

    private static CharWarsPlugin plugin;

    public static AbstractUIPlugin getDefault() {
        if (plugin == null) {
            plugin = new CharWarsPlugin();
        }
        return plugin;
    }

    @Override
    public IPreferenceStore getPreferenceStore() {
        return new CharWarsPreferenceStoreAdapter();
    }
}
