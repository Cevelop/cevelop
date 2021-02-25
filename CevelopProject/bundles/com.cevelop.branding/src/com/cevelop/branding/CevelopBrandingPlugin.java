package com.cevelop.branding;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * CevelopBrandingPlugin (Activator) is required so plug-in gets automatically
 * started (and also stays started). This is required by Includator to correctly
 * evaluate that Cevelop is the active product and that it is also signed.
 *
 */
public class CevelopBrandingPlugin extends AbstractUIPlugin {

    public static String PLUGIN_ID = "com.cevelop.branding";

    private BundleContext                bundleContext;
    private static CevelopBrandingPlugin plugin;

    @Override
    public void start(BundleContext context) throws Exception {
        bundleContext = context;
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    public static CevelopBrandingPlugin getDefault() {
        return plugin;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }
}
