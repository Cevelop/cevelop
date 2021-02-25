package com.cevelop.macronator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


public class MacronatorPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID                  = "com.cevelop.macronator.plugin"; //$NON-NLS-1$
    public static final String SUPPRESSED_MACROS_PREF_KEY = "suppressed_macros";

    // The shared instance
    private static MacronatorPlugin plugin;

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void stop(final BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static MacronatorPlugin getDefault() {
        return plugin;
    }

    /**
     * Logs the specified status with this plug-in's log.
     *
     * @param status
     * status to log
     */
    public static void log(final IStatus status) {
        getDefault().getLog().log(status);
    }

    /**
     * Logs an internal error with the specified throwable
     *
     * @param e
     * the exception to be logged
     */
    public static void log(final Throwable e) {
        log(new Status(IStatus.ERROR, PLUGIN_ID, 1, "Internal Error", e));
    }

    /**
     * Logs an error with the specified throwable and message
     *
     * @param e
     * the exception to be logged
     * @param message
     * additional message
     */
    public static void log(final Throwable e, final String message) {
        log(new Status(IStatus.ERROR, PLUGIN_ID, 1, message, e));
    }

    /**
     * Logs an internal error with the specified message.
     *
     * @param message
     * the error message to log
     */
    public static void logError(final String message) {
        log(new Status(IStatus.ERROR, PLUGIN_ID, 1, message, null));
    }

    public static void logInfo(final String message) {
        log(new Status(IStatus.INFO, PLUGIN_ID, 1, message, null));
    }
}
