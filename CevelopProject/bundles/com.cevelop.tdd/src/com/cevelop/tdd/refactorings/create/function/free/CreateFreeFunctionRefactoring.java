package com.cevelop.tdd.refactorings.create.function.free;

import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.SelectionRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;

import com.cevelop.tdd.helpers.FunctionCreationHelper;
import com.cevelop.tdd.helpers.ParameterHelper;
import com.cevelop.tdd.helpers.TddHelper;
import com.cevelop.tdd.infos.FreeFunctionInfo;


public class CreateFreeFunctionRefactoring extends SelectionRefactoring<FreeFunctionInfo> {

    public CreateFreeFunctionRefactoring(final ICElement element, final FreeFunctionInfo info) {
        super(element, info);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return Messages.CreateFreeFunction_name;
    }

    @Override
    protected RefactoringDescriptor getRefactoringDescriptor() {
        String comment = selectedRegion != null ? Messages.CreateFreeFunction_descWithSection : Messages.CreateFreeFunction_descWoSection;
        return new CreateFreeFunctionRefactoringDescriptor(project.getProject().getName(), Messages.CreateFreeFunction_name, comment, info);
    }

    @Override
    protected void collectModifications(IProgressMonitor pm, ModificationCollector collector) throws CoreException, OperationCanceledException {
        if (!selection.isPresent()) {
            return;
        }
        IASTTranslationUnit localunit = refactoringContext.getAST(tu, pm);
        IASTName selectedName = FunctionCreationHelper.getMostCloseSelectedNodeName(localunit, selection.get());
        if (selectedName == null) {
            return;
        }
        ICPPASTFunctionDefinition functionToWrite = getFunctionDefinition(localunit, selectedName, info.functionName, selection.get());
        if (functionToWrite == null) {
            return;
        }
        ((ICPPASTFunctionDeclarator) functionToWrite.getDeclarator()).setConst(false);
        IASTFunctionDefinition outerFunction = TddHelper.getOuterFunctionDeclaration(localunit, selection.get());
        if (outerFunction == null) {
            return;
        }
        ASTRewrite rewrite = collector.rewriterForTranslationUnit(localunit);
        rewrite.insertBefore(outerFunction.getParent(), outerFunction, functionToWrite, null);
    }

    private ICPPASTFunctionDefinition getFunctionDefinition(IASTTranslationUnit localunit, IASTNode selectedName, String name, ISelection selection) {
        ICPPASTFunctionDeclarator dec = new CPPASTFunctionDeclarator(new CPPASTName(name.toCharArray()));
        IASTFunctionCallExpression caller = TddHelper.getAncestorOfType(selectedName, IASTFunctionCallExpression.class);
        if (caller != null) {
            ParameterHelper.addTo(caller, dec);
        }
        ICPPASTFunctionDefinition newFunctionDefinition = FunctionCreationHelper.createNewFunction(localunit, selection, dec);
        if (!FunctionCreationHelper.isVoid(newFunctionDefinition)) {
            dec.setConst(true);
        }
        return newFunctionDefinition;
    }
}
