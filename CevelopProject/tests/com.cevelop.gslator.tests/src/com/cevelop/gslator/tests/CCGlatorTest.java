package com.cevelop.gslator.tests;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class CCGlatorTest extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "com.cevelop.gslator.test"; //$NON-NLS-1$

    // The shared instance
    private static CCGlatorTest plugin;

    /**
     * The constructor
     */
    public CCGlatorTest() {}

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static CCGlatorTest getDefault() {
        return plugin;
    }

}
