package com.cevelop.namespactor.quickfix;

import java.util.Optional;

import org.eclipse.cdt.codan.ui.AbstractCodanCMarkerResolution;
import org.eclipse.cdt.core.model.IWorkingCopy;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.ICEditor;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IWorkbenchWindow;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.WizardRefactoringStarterMenuHandler;


abstract public class AbstractMarkerResolution extends AbstractCodanCMarkerResolution {

    @Override
    public void apply(IMarker marker, IDocument document) {

        int nodeOffset = marker.getAttribute(IMarker.CHAR_START, -1);
        int length = marker.getAttribute(IMarker.CHAR_END, -1) - nodeOffset;

        IWorkbenchWindow window = CUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
        boolean isEditor = window.getActivePage().getActivePart() instanceof ICEditor;
        ICEditor fEditor = (isEditor) ? (ICEditor) window.getActivePage().getActivePart() : null;

        IWorkingCopy wc = CUIPlugin.getDefault().getWorkingCopyManager().getWorkingCopy(fEditor.getEditorInput());

        getRefactoringHandler().execute(fEditor.getSite().getShell(), wc, Optional.of(new TextSelection(nodeOffset, length)));
    }

    protected abstract WizardRefactoringStarterMenuHandler<?, ?> getRefactoringHandler();
}
