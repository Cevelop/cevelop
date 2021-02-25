package com.cevelop.namespactor.quickfix;

import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeSelector;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPTypedef;
import org.eclipse.cdt.internal.core.model.ASTCache.ASTRunnable;
import org.eclipse.cdt.internal.ui.editor.ASTProvider;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.ICEditor;
import org.eclipse.cdt.ui.text.ICCompletionProposal;
import org.eclipse.cdt.ui.text.IInvocationContext;
import org.eclipse.cdt.ui.text.IProblemLocation;
import org.eclipse.cdt.ui.text.IQuickAssistProcessor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorPart;

import com.cevelop.namespactor.astutil.NSNodeHelper;


@SuppressWarnings("restriction")
public class NamespactorQuickAssistProcessor implements IQuickAssistProcessor {

    @Override
    public boolean hasAssists(final IInvocationContext context) throws CoreException {
        IStatus status = ASTProvider.getASTProvider().runOnAST(context.getTranslationUnit(), ASTProvider.WAIT_ACTIVE_ONLY, new NullProgressMonitor(),
                (lang, astRoot) -> {
                    IASTNodeSelector selector = astRoot.getNodeSelector(null);
                    IASTNode node = selector.findEnclosingNode(context.getSelectionOffset(), context.getSelectionLength());

                    // Activate the proposal only if a typedef is selected.
                    if ((node = NSNodeHelper.nodeIsInTypedefSimpleDeclaration(node)) != null) {
                        return Status.OK_STATUS;
                    } else {
                        return Status.OK_STATUS;
                    }
                    //					return Status.CANCEL_STATUS;
                });

        return status.isOK();

    }

    @Override
    public ICCompletionProposal[] getAssists(final IInvocationContext context, final IProblemLocation[] locations) throws CoreException {

        final ArrayList<ICCompletionProposal> proposals = new ArrayList<>();

        ASTProvider.getASTProvider().runOnAST(context.getTranslationUnit(), ASTProvider.WAIT_ACTIVE_ONLY, new NullProgressMonitor(),
                new ASTRunnable() {

                    @Override
                    public IStatus runOnAST(ILanguage lang, IASTTranslationUnit astRoot) throws CoreException {
                        IASTNodeSelector selector = astRoot.getNodeSelector(null);
                        IASTName name = selector.findEnclosingName(context.getSelectionOffset(), context.getSelectionLength());
                        IASTNode node = selector.findEnclosingNode(context.getSelectionOffset(), context.getSelectionLength());

                        // Activate the proposal only if a typedef is selected. duplication to effort in underlying refactoring, but...
                        if ((NSNodeHelper.nodeIsInTypedefSimpleDeclaration(node)) != null) {
                            boolean lowerPriortyIfThereAreErrors = errorsAtLocation(locations);

                            // Quick assists that show up also if there is an error/warning
                            getTD2ARefactoringProposal(context, locations, lowerPriortyIfThereAreErrors, proposals);
                        }
                        if (name != null || node instanceof ICPPASTName) {
                            if (name == null) {
                                name = (IASTName) node;
                            }
                            IBinding thebinding = name.resolveBinding();

                            if (thebinding instanceof CPPTypedef && !isWithinOwnTypedef(name, (CPPTypedef) thebinding)) {
                                getITDARefactoringProposal(context, locations, false, proposals);

                            }
                        }
                        if (node instanceof ICPPASTSimpleDeclSpecifier) {

                            //				 if (((ICPPASTSimpleDeclSpecifier) node).getType() == ICPPASTSimpleDeclSpecifier.t_auto)
                            //					System.out.println("auto detected");

                        }
                        return Status.OK_STATUS;
                    }

                    private boolean isWithinOwnTypedef(IASTName name, CPPTypedef thebinding) {
                        IASTNode definition = thebinding.getDefinition();
                        return name.equals(definition);
                    }

                });
        return proposals.isEmpty() ? null : proposals.toArray(new ICCompletionProposal[proposals.size()]);

    }

    protected void getITDARefactoringProposal(IInvocationContext context, IProblemLocation[] locations, boolean b,
            ArrayList<ICCompletionProposal> proposals) {
        final IEditorPart editor = CUIPlugin.getActivePage().getActiveEditor();
        if (editor instanceof ICEditor && proposals != null) {
            ITDACompletionProposal proposal = new ITDACompletionProposal(editor);
            if (b) {
                proposal.setRelevance(2); // lower relevance if errors are there
            }

            proposals.add(proposal);
        }
    }

    private boolean errorsAtLocation(IProblemLocation[] locations) {
        if (locations != null) {
            for (IProblemLocation location : locations) {
                if (location.isError()) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void getTD2ARefactoringProposal(IInvocationContext context, IProblemLocation[] locations, boolean errorsAtLocation,
            ArrayList<ICCompletionProposal> proposals) {
        final IEditorPart editor = CUIPlugin.getActivePage().getActiveEditor();
        if (editor instanceof ICEditor && proposals != null) {

            TD2ACompletionProposal proposal = new TD2ACompletionProposal(editor);
            if (errorsAtLocation) {
                proposal.setRelevance(2); // lower relevance if errors are there
            }

            proposals.add(proposal);
        }
    }

}
