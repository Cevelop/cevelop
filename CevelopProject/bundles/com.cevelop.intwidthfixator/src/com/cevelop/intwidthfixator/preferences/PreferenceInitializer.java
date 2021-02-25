package com.cevelop.intwidthfixator.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.cevelop.intwidthfixator.helpers.IdHelper;
import com.cevelop.intwidthfixator.helpers.IdHelper.WidthId;


/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeDefaultPreferences() {
        final IPreferenceStore store = PropAndPrefHelper.getInstance().getWorkspacePreferences();
        store.setDefault(IdHelper.P_CHAR_PLATFORM_SIGNED_UNSIGNED, IdHelper.V_CHAR_PLATFORM_SIGNED);
        store.setDefault(IdHelper.P_CHAR_MAPPING, WidthId.WIDTH_8.id);
        store.setDefault(IdHelper.P_SHORT_MAPPING, WidthId.WIDTH_16.id);
        store.setDefault(IdHelper.P_INT_MAPPING, WidthId.WIDTH_32.id);
        store.setDefault(IdHelper.P_LONG_MAPPING, WidthId.WIDTH_32.id);
        store.setDefault(IdHelper.P_LONGLONG_MAPPING, WidthId.WIDTH_64.id);
    }

}
