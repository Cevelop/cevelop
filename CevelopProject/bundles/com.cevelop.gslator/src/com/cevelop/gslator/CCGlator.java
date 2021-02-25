package com.cevelop.gslator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class CCGlator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "com.cevelop.gslator"; //$NON-NLS-1$
    // The shared instance
    private static CCGlator plugin;

    /**
     * The constructor
     */
    public CCGlator() {}

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
     * BundleContext)
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
     * BundleContext)
     */
    @Override
    public void stop(final BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    private void log(int severity, String message) {
        getLog().log(new Status(severity, PLUGIN_ID, message));
    }

    private void log(int severity, String message, Throwable exception) {
        getLog().log(new Status(severity, PLUGIN_ID, message, exception));
    }

    public void logException(String message, Throwable exception) {
        log(IStatus.ERROR, message, exception);
    }

    public void logException(Throwable exception) {
        logException("Internal Error", exception);
    }

    public void logInfo(String message) {
        log(IStatus.INFO, message);
    }

    public void logWarning(String message) {
        log(IStatus.WARNING, message);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static CCGlator getDefault() {
        return plugin;
    }

}
