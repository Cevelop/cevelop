package com.cevelop.includator.viewer.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.texteditor.ITextEditor;


final class ViewUpdater implements IPartListener2, ISelectionListener, IPropertyListener {

    private final IncludeView view;
    private volatile boolean  isViewVisible = false;
    private ITextEditor       activeEditor  = null;

    public ViewUpdater(IncludeView modelView) {
        this.view = modelView;
    }

    @Override
    public void partDeactivated(IWorkbenchPartReference partRef) {

        IWorkbenchPart part = partRef.getPart(true);
        if (part instanceof ITextEditor) {

            ITextEditor editor = (ITextEditor) part;
            editor.getEditorSite().getPage().removePostSelectionListener(this);
        }
    }

    @Override
    public void partActivated(IWorkbenchPartReference partRef) {

        IWorkbenchPart part = partRef.getPart(true);
        if (isViewVisible && part instanceof ITextEditor) {
            ITextEditor editor = (ITextEditor) part;
            editor.getEditorSite().getPage().addPostSelectionListener(this);
            if (updateEditor(editor)) {
                view.drawIncludes(editor);
            }
        }
    }

    private boolean updateEditor(final ITextEditor editor) {
        if (activeEditor != editor) {
            if (activeEditor != null) {
                activeEditor.removePropertyListener(this);
            }
            activeEditor = editor;
            activeEditor.addPropertyListener(this);
            return true;
        }
        return false;
    }

    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {

        view.updateHighlighting(activeEditor);

    }

    @Override
    public void partVisible(IWorkbenchPartReference partRef) {
        if (partRef.getPart(false) == view) {
            isViewVisible = true;
            final IEditorPart editor = partRef.getPage().getActiveEditor();
            if (editor instanceof ITextEditor) {
                if (updateEditor((ITextEditor) editor)) {
                    view.drawIncludes(activeEditor);
                }
            }
        }
    }

    @Override
    public void partOpened(IWorkbenchPartReference partRef) {}

    @Override
    public void partInputChanged(IWorkbenchPartReference partRef) {}

    @Override
    public void partHidden(IWorkbenchPartReference partRef) {
        if (partRef.getPart(false) == view) {
            isViewVisible = false;
        }
    }

    @Override
    public void partClosed(IWorkbenchPartReference partRef) {
        if (partRef.getPart(false) == view) {
            isViewVisible = false;
        }
    }

    @Override
    public void partBroughtToTop(IWorkbenchPartReference partRef) {}

    @Override
    public void propertyChanged(Object source, int propId) {
        if (propId == IEditorPart.PROP_DIRTY) {
            if (!activeEditor.isDirty()) {
                view.drawIncludes(activeEditor);
            }
        }
    }
}
