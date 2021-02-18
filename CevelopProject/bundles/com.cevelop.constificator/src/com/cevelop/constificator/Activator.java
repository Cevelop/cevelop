package com.cevelop.constificator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


public class Activator extends AbstractUIPlugin {

    private static Activator plugin;

    public static final String PLUGIN_ID = "com.cevelop.constificator"; //$NON-NLS-1$

    public static Activator getDefault() {
        return plugin;
    }

    public Activator() {}

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
