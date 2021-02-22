package com.cevelop.namespactor.refactoring.iu;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.namespactor.astutil.NSSelectionHelper;
import com.cevelop.namespactor.refactoring.TemplateIdFactory;
import com.cevelop.namespactor.refactoring.iudec.IUDecRefactoring;
import com.cevelop.namespactor.refactoring.iudir.IUDirRefactoring;
import com.cevelop.namespactor.refactoring.rewrite.ASTRewriteStore;
import com.cevelop.namespactor.resources.Labels;


public class IURefactoring extends InlineRefactoringBase {

    private InlineRefactoringBase    delegate = null;
    private ICElement                element  = null;
    private Optional<ITextSelection> selection;

    public IURefactoring(ICElement element, Optional<ITextSelection> selection) {
        super(element, selection);
        this.selection = selection;
        this.element = element;
    }

    @Override
    protected void collectModifications(ASTRewriteStore store) {
        delegate.collectModifications(store);
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        Region region = selectedRegion;

        IASTTranslationUnit ast = this.refactoringContext.getAST(this.tu, pm);// warnings...
        if (NSSelectionHelper.getSelectedUsingDirective(region, ast) != null) {
            delegate = new IUDirRefactoring(element, selection);
        } else if (NSSelectionHelper.getSelectedUsingDeclaration(region, ast) != null) {
            delegate = new IUDecRefactoring(element, selection);
        } else {
            initStatus.addFatalError(Labels.IUDIR_NoUDIRSelected);
            return initStatus;
        }
        delegate.setContext(this.refactoringContext);

        return delegate.checkInitialConditions(pm);
    }

    @Override
    protected TemplateIdFactory getTemplateIdFactory(ICPPASTTemplateId templateId, InlineRefactoringContext ctx) {
        return delegate.getTemplateIdFactory(templateId, ctx);
    }

}
