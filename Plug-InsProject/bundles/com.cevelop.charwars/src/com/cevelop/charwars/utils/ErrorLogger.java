package com.cevelop.charwars.utils;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.cevelop.charwars.CharWarsPlugin;


public final class ErrorLogger {

    private ErrorLogger() {}

    public static void log(String message, Throwable exception) {
        ILog logger = CharWarsPlugin.getDefault().getLog();
        Status status = new Status(IStatus.ERROR, CharWarsPlugin.PLUGIN_ID, IStatus.OK, message, exception);
        logger.log(status);
    }
}
