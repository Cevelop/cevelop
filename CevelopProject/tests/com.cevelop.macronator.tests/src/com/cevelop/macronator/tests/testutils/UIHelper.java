
package com.cevelop.macronator.tests.testutils;

import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.helpers.UIThreadSyncRunnable;


public class UIHelper {

    private static final String INTROVIEW_ID        = "org.eclipse.ui.internal.introview";
    private static boolean      welcomeScreenClosed = false;

    public static void closeWelcomeScreen() throws Exception {
        new UIThreadSyncRunnable() {

            @Override
            protected void runSave() throws Exception {
                if (!welcomeScreenClosed) {
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    IViewReference viewRef = page.findViewReference(INTROVIEW_ID);
                    page.hideView(viewRef);

                    welcomeScreenClosed = true;
                }
            }
        }.runSyncOnUIThread();
    }
}
