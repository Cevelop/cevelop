package com.cevelop.macronator.quickassist;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroExpansion;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.internal.core.model.ASTCache.ASTRunnable;
import org.eclipse.cdt.internal.ui.editor.ASTProvider;
import org.eclipse.cdt.ui.text.ICCompletionProposal;
import org.eclipse.cdt.ui.text.IInvocationContext;
import org.eclipse.cdt.ui.text.IProblemLocation;
import org.eclipse.cdt.ui.text.IQuickAssistProcessor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;

import com.cevelop.macronator.refactoring.ExpandMacroProposal;


/**
 * Displays a quickassist to start the local macro expansion refactoring.
 *
 */
@SuppressWarnings("restriction")
public class LocalExpansionQuickAssist implements IQuickAssistProcessor {

    private ExpandMacroProposal result;

    @Override
    public boolean hasAssists(IInvocationContext context) throws CoreException {
        return true;
    }

    @Override
    public ICCompletionProposal[] getAssists(final IInvocationContext context, IProblemLocation[] locations) throws CoreException {
        IStatus status = ASTProvider.getASTProvider().runOnAST(context.getTranslationUnit(), ASTProvider.WAIT_ACTIVE_ONLY, new NullProgressMonitor(),
                new CompletionProposoalAstRunnable(context.getSelectionOffset(), context.getSelectionLength()));
        return (status.isOK()) ? new ICCompletionProposal[] { result } : new ICCompletionProposal[0];
    }

    private class CompletionProposoalAstRunnable implements ASTRunnable {

        private final int selectionOffset;
        private final int selectionLength;

        public CompletionProposoalAstRunnable(int selectionOffset, int selectionLength) {
            this.selectionOffset = selectionOffset;
            this.selectionLength = selectionLength;
        }

        @Override
        public IStatus runOnAST(ILanguage lang, IASTTranslationUnit ast) throws CoreException {
            IASTPreprocessorMacroExpansion macroExpansion = ast.getNodeSelector(null).findEnclosingMacroExpansion(selectionOffset, selectionLength);
            if (macroExpansion != null) {
                result = new ExpandMacroProposal(macroExpansion);
                return Status.OK_STATUS;
            }
            return Status.CANCEL_STATUS;
        }
    }
}
