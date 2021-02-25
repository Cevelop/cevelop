package com.cevelop.templator.plugin.view.components;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IViewSite;

import com.cevelop.templator.plugin.util.ImageCache;
import com.cevelop.templator.plugin.util.ImageCache.ImageID;
import com.cevelop.templator.plugin.view.interfaces.ITreeViewController;


public class GlobalToolBar {

    public GlobalToolBar(final ITreeViewController controller, IViewSite viewSite) {

        ImageDescriptor minimizeAllImg = ImageDescriptor.createFromImage(ImageCache.get(ImageID.MINIMIZE_ALL));
        ImageDescriptor maximizeAllImg = ImageDescriptor.createFromImage(ImageCache.get(ImageID.MAXIMIZE_ALL));
        ImageDescriptor closeAllImg = ImageDescriptor.createFromImage(ImageCache.get(ImageID.CLOSE_ALL));
        ImageDescriptor refreshImg = ImageDescriptor.createFromImage(ImageCache.get(ImageID.REFRESH));

        IToolBarManager toolBarManager = viewSite.getActionBars().getToolBarManager();

        toolBarManager.add(new Action("Minimize All Entries", minimizeAllImg) {

            @Override
            public void run() {
                controller.minimizeAll();
            }
        });
        toolBarManager.add(new Action("Maximize All Entries", maximizeAllImg) {

            @Override
            public void run() {
                controller.maximizeAll();
            }
        });
        toolBarManager.add(new Action("Close All Entries", closeAllImg) {

            @Override
            public void run() {
                controller.clear();
            }
        });

        toolBarManager.add(new Separator());

        toolBarManager.add(new Action("Refresh View from Editor", refreshImg) {

            @Override
            public void run() {
                controller.refreshFromEditor();
            }
        });
    }
}
