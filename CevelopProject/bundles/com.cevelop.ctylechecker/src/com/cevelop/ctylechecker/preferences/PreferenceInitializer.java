package com.cevelop.ctylechecker.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;


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
        // TODO use store.setDefault(...) to load default preferences initially
    }

}
