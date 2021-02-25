package com.cevelop.constificator.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;


public class Activator extends Plugin {

    private static Activator     activator;
    private static BundleContext context;
    public static final String   PLUGIN_ID = "com.cevelop.constificator";

    static BundleContext getContext() {
        return context;
    }

    public static Activator getDefault() {
        return activator;
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

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        Activator.context = bundleContext;
        activator = this;
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        Activator.context = null;

    }
}
