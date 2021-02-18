package com.cevelop.codeanalysator.core.quickassist;

import org.eclipse.cdt.internal.ui.editor.ASTProvider;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.text.ICCompletionProposal;
import org.eclipse.cdt.ui.text.IInvocationContext;
import org.eclipse.cdt.ui.text.IProblemLocation;
import org.eclipse.cdt.ui.text.IQuickAssistProcessor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorPart;

import com.cevelop.codeanalysator.core.quickassist.proposal.AddOverrideProposal;
import com.cevelop.codeanalysator.core.quickassist.runnable.OverrideProposoalRunnable;


@SuppressWarnings("restriction")
public class OverriderQuickAssist implements IQuickAssistProcessor {

    public static String ID = "com.cevelop.macronator.plugin.assist.Overrider";

    @Override
    public boolean hasAssists(IInvocationContext context) throws CoreException {
        return true;
    }

    @Override
    public ICCompletionProposal[] getAssists(IInvocationContext context, IProblemLocation[] locations) throws CoreException {
        OverrideProposoalRunnable runnable = new OverrideProposoalRunnable(context.getSelectionOffset(), context.getSelectionLength());

        IStatus status = ASTProvider.getASTProvider().runOnAST(context.getTranslationUnit(), ASTProvider.WAIT_ACTIVE_ONLY, new NullProgressMonitor(),
                runnable);
        IEditorPart editor = CUIPlugin.getActivePage().getActiveEditor();
        return (status.isOK() ? new ICCompletionProposal[] { new AddOverrideProposal(runnable, editor) } : new ICCompletionProposal[0]);
    }

}
