package com.cevelop.templator.plugin.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;


public class GetActiveEditorUIJob extends UIJob {

    private IEditorPart activeEditor;

    public GetActiveEditorUIJob(String name) {
        super(name);
    }

    @Override
    public IStatus runInUIThread(IProgressMonitor monitor) {
        activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        return Status.OK_STATUS;
    }

    public IEditorPart getActiveEditor() {
        return activeEditor;
    }

}
