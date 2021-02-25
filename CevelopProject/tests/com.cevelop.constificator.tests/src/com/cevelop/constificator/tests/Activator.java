package com.cevelop.constificator.tests;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


public class Activator extends AbstractUIPlugin {

    private static Activator plugin;

    public static final String PLUGIN_ID = "com.cevelop.constificator.tests"; //$NON-NLS-1$

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
