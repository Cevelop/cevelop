package com.cevelop.templator.plugin.util;

import org.eclipse.jface.preference.IPreferenceStore;

import com.cevelop.templator.plugin.TemplatorPlugin;
import com.cevelop.templator.plugin.preferences.TemplatorPreference;


public final class SettingsCache {

    private static boolean alwaysShowRectangles;
    private static boolean disableTracingNormalFunctions;
    private static boolean disableTracingNormalClasses;
    private static boolean disableTracingAutoSpecifier;
    private static boolean hasMaxWidth;
    private static boolean hasMaxHeight;

    static {
        cachePreferenceValues();
        addPropertyChangeListener();
    }

    public static boolean isTracingNormalFunctions() {
        return !disableTracingNormalFunctions;
    }

    public static boolean isTracingNormalClasses() {
        return !disableTracingNormalClasses;
    }

    public static boolean isTracingAuto() {
        return !disableTracingAutoSpecifier;
    }

    public static boolean areAllRectanglesVisible() {
        return alwaysShowRectangles;
    }

    public static boolean hasMaxWidth() {
        return hasMaxWidth;
    }

    public static boolean hasMaxHeight() {
        return hasMaxHeight;
    }

    private static void cachePreferenceValues() {
        IPreferenceStore store = TemplatorPlugin.getDefault().getPreferenceStore();
        alwaysShowRectangles = store.getBoolean(TemplatorPreference.RECTANGLE_ALWAYS_VISIBLE.getKey());
        disableTracingNormalFunctions = store.getBoolean(TemplatorPreference.DISABLE_NORMAL_FUNCTIONS.getKey());
        disableTracingNormalClasses = store.getBoolean(TemplatorPreference.DISABLE_NORMAL_CLASSES.getKey());
    }

    private static void addPropertyChangeListener() {
        TemplatorPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(event -> {
            String property = event.getProperty();
            IPreferenceStore store = TemplatorPlugin.getDefault().getPreferenceStore();
            if (property == TemplatorPreference.RECTANGLE_ALWAYS_VISIBLE.getKey()) {
                alwaysShowRectangles = store.getBoolean(TemplatorPreference.RECTANGLE_ALWAYS_VISIBLE.getKey());
            } else if (property == TemplatorPreference.DISABLE_NORMAL_FUNCTIONS.getKey()) {
                disableTracingNormalFunctions = store.getBoolean(TemplatorPreference.DISABLE_NORMAL_FUNCTIONS.getKey());
            } else if (property == TemplatorPreference.DISABLE_NORMAL_CLASSES.getKey()) {
                disableTracingNormalClasses = store.getBoolean(TemplatorPreference.DISABLE_NORMAL_CLASSES.getKey());
            } else if (property == TemplatorPreference.DISABLE_AUTO_SPECIFIER.getKey()) {
                disableTracingAutoSpecifier = store.getBoolean(TemplatorPreference.DISABLE_AUTO_SPECIFIER.getKey());
            } else if (property == TemplatorPreference.HAS_MAX_WIDTH.getKey()) {
                hasMaxWidth = store.getBoolean(TemplatorPreference.HAS_MAX_WIDTH.getKey());
            } else if (property == TemplatorPreference.HAS_MAX_HEIGHT.getKey()) {
                hasMaxHeight = store.getBoolean(TemplatorPreference.HAS_MAX_HEIGHT.getKey());
            }
        });
    }
}
