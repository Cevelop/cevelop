package com.cevelop.constificator.resolution;

import org.eclipse.cdt.codan.ui.AbstractAstRewriteQuickFix;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.ui.IMarkerResolution;

import com.cevelop.constificator.Activator;
import com.cevelop.constificator.core.util.ast.ASTRewriteCache;
import com.cevelop.constificator.core.util.structural.Relation;
import com.cevelop.constificator.refactorings.MultiChangeRefactoring;
import com.cevelop.constificator.refactorings.MultiChangeRefactoringWizard;


public class ConstificatorQuickFix extends AbstractAstRewriteQuickFix {

    private boolean fHasMultipleChanges = false;
    private boolean fIsTesting          = false;

    @Override
    protected IIndex getIndexFromMarker(IMarker marker) throws CoreException {
        CCorePlugin.getIndexManager().joinIndexer(1000, new NullProgressMonitor());
        return super.getIndexFromMarker(marker);
    }

    @Override
    public String getLabel() {
        return "Add const-qualification";
    }

    private void handle(IASTNode node, IIndex index, ASTRewriteCache cache) {
        try {
            if (Relation.getAncestorOf(ICPPASTParameterDeclaration.class, node) != null) {
                ICPPASTParameterDeclaration ancestor = Relation.getAncestorOf(ICPPASTParameterDeclaration.class, node);
                new FunctionParameterResolution(this).handle(node, index, cache, ancestor);
            } else if (node instanceof ICPPASTFunctionDeclarator) {
                ICPPASTFunctionDefinition ancestor = Relation.getAncestorOf(ICPPASTFunctionDefinition.class, node);
                new MemberFunctionResolution(this).handle(node, index, cache, ancestor);
            } else if (Relation.getAncestorOf(IASTSimpleDeclaration.class, node) != null) {
                IASTSimpleDeclaration ancestor = Relation.getAncestorOf(IASTSimpleDeclaration.class, node);
                new LocalVariableResolution().handle(node, index, cache, ancestor);
            }
        } catch (Throwable exception) {
            Activator.getDefault().logException("Failed to run quickfix ", exception);
        }

    }

    @Override
    public void modifyAST(final IIndex index, final IMarker marker) {

        try {
            ASTRewriteCache cache = new ASTRewriteCache(index);
            final ITranslationUnit tu = getTranslationUnitViaEditor(marker);
            final IASTTranslationUnit ast = cache.getASTTranslationUnit(tu);

            int start = marker.getAttribute(IMarker.CHAR_START, -1);
            int end = marker.getAttribute(IMarker.CHAR_END, -1);
            final IASTNode node = ast.getNodeSelector(null).findNode(start, end - start);
            handle(node, index, cache);

            if (fHasMultipleChanges && !fIsTesting) {
                final MultiChangeRefactoring refactoring = new MultiChangeRefactoring(cache);
                final MultiChangeRefactoringWizard wizard = new MultiChangeRefactoringWizard(refactoring);
                final RefactoringWizardOpenOperation operation = new RefactoringWizardOpenOperation(wizard);
                int status = operation.run(null, "Add missing const qualifications");
                if (status != IDialogConstants.CANCEL_ID) {
                    marker.delete();
                }
            } else {
                cache.getChange().perform(new NullProgressMonitor());
                marker.delete();
            }

        } catch (CoreException | InterruptedException e) {
            Activator.getDefault().logException("AST modification failed", e);
        }

    }

    public void setHasMutipleChanges(boolean hasMultiple) {
        fHasMultipleChanges = hasMultiple;
    }

    public IMarkerResolution setTesting(boolean isTesting) {
        fIsTesting = isTesting;
        return this;
    }

}
