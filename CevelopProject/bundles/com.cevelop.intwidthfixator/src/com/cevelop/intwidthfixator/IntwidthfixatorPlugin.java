package com.cevelop.intwidthfixator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;


public class IntwidthfixatorPlugin extends AbstractUIPlugin {

    private static IntwidthfixatorPlugin plugin;

    public static String PLUGIN_ID;
    public static String PLUGIN_NAME;

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        PLUGIN_ID = getBundle().getSymbolicName();
        PLUGIN_NAME = getBundle().getHeaders().get(Constants.BUNDLE_NAME);
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    public static IntwidthfixatorPlugin getDefault() {
        return plugin;
    }

    /**
     * Logs the specified status with this plug-in's log.
     *
     * @param status
     * The status to be logged
     */
    public static void log(final IStatus status) {
        getDefault().getLog().log(status);
    }

    /**
     * Logs an internal error with the specified throwable
     *
     * @param e
     * The exception to be logged
     */
    public static void log(final Throwable e) {
        log(new Status(IStatus.ERROR, IntwidthfixatorPlugin.PLUGIN_ID, 1, "Internal Error", e));
    }

    /**
     * Logs an error with the specified throwable and message
     *
     * @param e
     * The exception to be logged
     * @param message
     * An additional message
     */
    public static void log(final Throwable e, final String message) {
        log(new Status(IStatus.ERROR, IntwidthfixatorPlugin.PLUGIN_ID, 1, message, e));
    }

    /**
     * Logs an internal error with the specified message.
     *
     * @param message
     * The error message to be logged
     */
    public static void log(final String message) {
        log(new Status(IStatus.ERROR, IntwidthfixatorPlugin.PLUGIN_ID, 1, message, null));
    }
}
