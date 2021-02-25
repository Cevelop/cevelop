package com.cevelop.conanator.tests;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;


public class Activator extends Plugin {

    private static Activator plugin;

    public static final String PLUGIN_ID = "com.cevelop.conanator.tests"; //$NON-NLS-1$

    public static Activator getDefault() {
        return plugin;
    }

    public Activator() {}

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

}
