package com.cevelop.conanator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "conan.core";

    // The shared instance
    private static Activator plugin;

    boolean started;

    /**
     * The constructor
     */
    public Activator() {}

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        started = true;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        started = false;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path
     *
     * @param path
     * the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
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

    public static void logError(final String message) {
        log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message));
    }

    public static void logInfo(final String message) {
        log(new Status(IStatus.INFO, Activator.PLUGIN_ID, message));
    }

    public static void logWarning(final String message) {
        log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, message));
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
}
