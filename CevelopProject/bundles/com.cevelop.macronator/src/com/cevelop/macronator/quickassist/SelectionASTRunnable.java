package com.cevelop.macronator.quickassist;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IMacroBinding;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.internal.core.model.ASTCache.ASTRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


@SuppressWarnings("restriction")
public class SelectionASTRunnable implements ASTRunnable {

    private final int selectionOffset;
    private final int selectionLength;
    private IASTName  selectedName;

    public SelectionASTRunnable(int selectionOffset, int selectionLength) {
        this.selectionOffset = selectionOffset;
        this.selectionLength = selectionLength;
    }

    @Override
    public IStatus runOnAST(ILanguage lang, IASTTranslationUnit ast) throws CoreException {
        selectedName = new SelectionResolver(ast, selectionOffset, selectionLength).getSelectedName();
        return isMacroDefinition() ? Status.OK_STATUS : Status.CANCEL_STATUS;
    }

    public IASTName getResult() {
        return selectedName;
    }

    private boolean isMacroDefinition() {
        return selectedName != null && selectedName.getBinding() instanceof IMacroBinding && selectedName.isDefinition();
    }
}
