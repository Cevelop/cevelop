package com.cevelop.includator.tests.helpers;

import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;

import com.cevelop.includator.IncludatorPlugin;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.helpers.UIThreadSyncRunnable;


public class UIHelper {

    private static final String INTROVIEW_ID        = "org.eclipse.ui.internal.introview";
    private static boolean      welcomeScreenClosed = false;

    public static void closeWelcomeScreen() throws Exception {
        if (!welcomeScreenClosed) {
            new UIThreadSyncRunnable() {

                @Override
                protected void runSave() throws Exception {
                    IWorkbenchPage page = IncludatorPlugin.getActiveWorkbenchWindow().getActivePage();
                    IViewReference viewRef = page.findViewReference(INTROVIEW_ID);
                    page.hideView(viewRef);
                    hideIncludeView(page);

                    welcomeScreenClosed = true;
                }
            }.runSyncOnUIThread();
        }
    }

    private static void hideIncludeView(IWorkbenchPage page) {
        IViewReference includeViewRef = page.findViewReference("com.cevelop.includator.viewer.views.IncludeView");
        page.hideView(includeViewRef);
    }
}
