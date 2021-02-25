package com.cevelop.charwars.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;


public class ShowSettingsActionDelegate extends AbstractHandler implements IWorkbenchWindowActionDelegate {

    @Override
    public void run(IAction action) {
        Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        SettingsDialog dialog = new SettingsDialog(activeShell);
        dialog.open();
        //		final ISelection selection = CUIPlugin.getActivePage().getSelection();
        //		final IEditorInput editorInput = CUIPlugin.getActivePage().getActiveEditor().getEditorInput();
        //		final IWorkingCopy workingCopy = CUIPlugin.getDefault().getWorkingCopyManager().getWorkingCopy(editorInput);
        //		new GeneratePipelineRefactoringRunner(workingCopy, selection, CUIPlugin.getActiveWorkbenchWindow(), workingCopy.getCProject()).run();
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {}

    @Override
    public void init(IWorkbenchWindow window) {}

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        run(null);
        return null;
    }
}
