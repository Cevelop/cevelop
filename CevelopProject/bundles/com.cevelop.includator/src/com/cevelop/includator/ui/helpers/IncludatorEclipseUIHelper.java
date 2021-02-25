/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.ui.helpers;

import java.net.URI;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.IncludatorException;


public class IncludatorEclipseUIHelper {

    public static IEditorPart getActiveEditor(IWorkbenchWindow window) {
        return window.getActivePage().getActiveEditor();
    }

    public static IEditorInput getActiveEditorInput(IWorkbenchWindow window) {
        IEditorPart activeEditor = getActiveEditor(window);
        return (activeEditor != null) ? activeEditor.getEditorInput() : null;
    }

    public static ITextEditor getEditor(URI fileUri, IWorkbenchWindow window) {
        try {
            IEditorReference[] editors = window.getActivePage().getEditorReferences();

            for (IEditorReference reference : editors) {
                IEditorInput editorInput = reference.getEditorInput();
                if (editorInput instanceof IFileEditorInput) {
                    if (fileUri.equals(((IFileEditorInput) editorInput).getFile().getLocationURI())) {
                        IEditorPart editor = reference.getEditor(false);
                        if ((editor != null) && (editor instanceof ITextEditor)) {
                            return (ITextEditor) editor;
                        }
                    }
                }
            }
        } catch (PartInitException e) {
            throw new IncludatorException(e);
        }
        return null;
    }

    public static boolean syncSaveAllEditors(final boolean shouldAsk) throws InterruptedException {
        final BooleanResultContainer saveEditorResult = new BooleanResultContainer();
        Runnable runnable = () -> saveEditorResult.setResult(IncludatorPlugin.getDefault().getWorkbench().saveAllEditors(shouldAsk));
        PlatformUI.getWorkbench().getDisplay().syncExec(runnable);
        return saveEditorResult.getResult();
    }

    public static void joinIndexer() {
        final boolean joined = CCorePlugin.getIndexManager().joinIndexer(180000, new NullProgressMonitor());
        if (!joined) {
            throw new IncludatorException("Joining indexer timed out.");
        }
    }

    public static boolean syncSaveAllEditors() throws InterruptedException {
        return syncSaveAllEditors(true);
    }

}
