/******************************************************************************
 * Copyright (c) 2012 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ueli Kunz <kunz@ideadapt.net>, Jules Weder <julesweder@gmail.com> - initial API and implementation
 ******************************************************************************/
package com.cevelop.namespactor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


public class Activator extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "com.cevelop.namespactor";

    private static Activator plugin;

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

    public static void log(IStatus status) {
        getDefault().getLog().log(status);
    }

    public static void log(String message) {
        log(message, null);
    }

    public static void log(String message, Throwable t) {
        log(new Status(IStatus.ERROR, PLUGIN_ID, 1, message, t));
    }

    public static Activator getDefault() {
        return plugin;
    }

    public static void log(Throwable t) {
        log("Error: ", t);
    }
}
