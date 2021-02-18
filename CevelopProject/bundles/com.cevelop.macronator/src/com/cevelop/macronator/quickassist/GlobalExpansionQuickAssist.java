package com.cevelop.macronator.quickassist;

import org.eclipse.cdt.internal.ui.editor.ASTProvider;
import org.eclipse.cdt.ui.CDTSharedImages;
import org.eclipse.cdt.ui.text.ICCompletionProposal;
import org.eclipse.cdt.ui.text.IInvocationContext;
import org.eclipse.cdt.ui.text.IProblemLocation;
import org.eclipse.cdt.ui.text.IQuickAssistProcessor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.PlatformUI;

import com.cevelop.macronator.ui.handlers.ExpandMacroHandler;


/**
 * Displays a quickassist to start the global macro expansion refactoring.
 */
@SuppressWarnings("restriction")
public class GlobalExpansionQuickAssist implements IQuickAssistProcessor {

    public static final String ID = "com.cevelop.macronator.plugin.assist.GlobalExpansion";

    @Override
    public boolean hasAssists(final IInvocationContext context) throws CoreException {
        return true;
    }

    @Override
    public ICCompletionProposal[] getAssists(final IInvocationContext context, IProblemLocation[] locations) throws CoreException {
        SelectionASTRunnable runnable = new SelectionASTRunnable(context.getSelectionOffset(), context.getSelectionLength());
        IStatus status = ASTProvider.getASTProvider().runOnAST(context.getTranslationUnit(), ASTProvider.WAIT_ACTIVE_ONLY, new NullProgressMonitor(),
                runnable);
        if (status.isOK()) {

            return new ICCompletionProposal[] { new ICCompletionProposal() {

                @Override
                public void apply(IDocument document) {
                    new ExpandMacroHandler().execute(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor());
                }

                @Override
                public Point getSelection(IDocument document) {
                    return null;
                }

                @Override
                public String getAdditionalProposalInfo() {
                    return null;
                }

                @Override
                public String getDisplayString() {
                    return "Expand globally / remove definition";
                }

                @Override
                public Image getImage() {
                    return CDTSharedImages.getImage(CDTSharedImages.IMG_OBJS_MACRO);
                }

                @Override
                public IContextInformation getContextInformation() {
                    return null;
                }

                @Override
                public int getRelevance() {
                    return 0;
                }

                @Override
                public String getIdString() {
                    return ID;
                }
            } };
        } else {
            return new ICCompletionProposal[0];
        }
    }
}
