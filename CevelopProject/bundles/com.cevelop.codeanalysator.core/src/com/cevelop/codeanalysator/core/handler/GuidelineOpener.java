package com.cevelop.codeanalysator.core.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.handlers.HandlerUtil;


public class GuidelineOpener extends AbstractHandler {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {

        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

        String prefPageID = "com.cevelop.codeanalysator.core.preference.guideline.page";

        PreferencesUtil.createPreferenceDialogOn(window.getShell(), prefPageID, new String[] { prefPageID }, null).open();

        return null;
    }

}
