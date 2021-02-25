package com.cevelop.templator.plugin;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.cevelop.templator.plugin.preferences.TemplatorPreference;
import com.cevelop.templator.plugin.util.ColorPalette;
import com.cevelop.templator.plugin.util.CursorCache;


/**
 * The activator class controls the plug-in life cycle
 */
public class TemplatorPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "com.cevelop.templator.plugin"; //$NON-NLS-1$

    // The shared instance
    private static TemplatorPlugin plugin;

    // default preference values
    private static final int DEFAULT_HOVER_COLOR     = SWT.COLOR_GRAY;
    private static final int DEFAULT_RECTANGLE_COLOR = SWT.COLOR_BLACK;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        ColorPalette.disposePalette();
        CursorCache.disposeCursors();
        plugin = null;
        super.stop(context);
    }

    public static TemplatorPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path
     *
     * @param path
     * the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    @Override
    protected void initializeDefaultPreferences(IPreferenceStore store) {
        Color hoverColor = Display.getDefault().getSystemColor(DEFAULT_HOVER_COLOR);
        PreferenceConverter.setDefault(store, TemplatorPreference.HOVER_COLOR.getKey(), hoverColor.getRGB());

        Color rectangleColor = Display.getDefault().getSystemColor(DEFAULT_RECTANGLE_COLOR);
        PreferenceConverter.setDefault(store, TemplatorPreference.RECTANGLE_COLOR.getKey(), rectangleColor.getRGB());
    }
}
