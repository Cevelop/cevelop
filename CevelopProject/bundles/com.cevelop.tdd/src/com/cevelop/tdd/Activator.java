/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.cevelop.tdd.helpers.IdHelper;


public class Activator extends AbstractUIPlugin {

    private static final IPath ICONS_PATH = new Path("$nl$/icons");

    private static Activator plugin;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
    }

    public static Activator getDefault() {
        if (plugin == null) {
            throw new RuntimeException(Messages.ActivatorException);
        }
        return plugin;
    }

    public static ImageDescriptor getImageDescriptor(String relativePath) {
        IPath path = ICONS_PATH.append(relativePath);
        return createImageDescriptor(getDefault().getBundle(), path);
    }

    private static ImageDescriptor createImageDescriptor(Bundle bundle, IPath path) {
        URL url = FileLocator.find(bundle, path, null);
        return ImageDescriptor.createFromURL(url);
    }

    public ITextSelection getEditorSelection() {
        IEditorSite site = getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorSite();
        return (ITextSelection) site.getSelectionProvider().getSelection();
    }

    public static void log(IStatus status) {
        getDefault().getLog().log(status);
    }

    public static void log(String message) {
        log(message, null);
    }

    public static void log(String message, Throwable t) {
        log(new Status(IStatus.ERROR, IdHelper.PLUGIN_ID, 1, message, t));
    }
}
