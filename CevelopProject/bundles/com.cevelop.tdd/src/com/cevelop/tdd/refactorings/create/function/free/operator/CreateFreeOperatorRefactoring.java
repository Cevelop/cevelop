package com.cevelop.tdd.refactorings.create.function.free.operator;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTOperatorName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTReferenceOperator;
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
import com.cevelop.tdd.infos.FreeOperatorInfo;


public class CreateFreeOperatorRefactoring extends SelectionRefactoring<FreeOperatorInfo> {

    public CreateFreeOperatorRefactoring(final ICElement element, final FreeOperatorInfo info) {
        super(element, info);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return Messages.CreateFreeOperator_name;
    }

    @Override
    protected RefactoringDescriptor getRefactoringDescriptor() {
        String comment = selectedRegion != null ? Messages.CreateFreeOperator_descWithSection : Messages.CreateFreeOperator_descWoSection;
        return new CreateFreeOperatorRefactoringDescriptor(project.getProject().getName(), Messages.CreateFreeOperator_name, comment, info);
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
        ICPPASTFunctionDefinition functionToWrite = getFunctionDefinition(localunit, selectedName, info.operatorName, selection.get());
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
        ICPPASTFunctionDeclarator decl = new CPPASTFunctionDeclarator(new CPPASTOperatorName(("operator" + name).toCharArray()));
        IASTBinaryExpression binex = TddHelper.getAncestorOfType(selectedName, IASTBinaryExpression.class);
        IASTUnaryExpression unex = TddHelper.getAncestorOfType(selectedName, IASTUnaryExpression.class);
        if (binex != null) {
            IASTExpression op1 = binex.getOperand1();
            FunctionCreationHelper.addParameterToOperator(decl, op1);
            IASTExpression op2 = binex.getOperand2();
            FunctionCreationHelper.addParameterToOperator(decl, op2);
        } else if (unex != null) {
            IASTExpression op = unex.getOperand();
            FunctionCreationHelper.addParameterToOperator(decl, op);
        }
        return createFunctionDefinition(localunit, selectedName, selection, decl);
    }

    protected ICPPASTFunctionDefinition createFunctionDefinition(IASTTranslationUnit localunit, IASTNode selectedName, ISelection selection,
            ICPPASTFunctionDeclarator decl) {
        ICPPASTFunctionDefinition fdef = FunctionCreationHelper.createNewFunction(localunit, selection, decl);
        if (!FunctionCreationHelper.isVoid(fdef) && FunctionCreationHelper.isConstOperator(selectedName)) {
            decl.setConst(true);
        }
        if (FunctionCreationHelper.isPostfixOperator(selectedName)) {
            ParameterHelper.addEmptyIntParameter(decl);
        } else if (!decl.isConst() && !FunctionCreationHelper.isVoid(fdef)) {
            decl.addPointerOperator(new CPPASTReferenceOperator(false));
        }
        return fdef;
    }
}
