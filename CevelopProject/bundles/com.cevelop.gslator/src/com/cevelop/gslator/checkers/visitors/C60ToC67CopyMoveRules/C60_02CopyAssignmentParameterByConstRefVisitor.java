package com.cevelop.gslator.checkers.visitors.C60ToC67CopyMoveRules;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTReferenceOperator;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.ids.IdHelper.ProblemId;


public class C60_02CopyAssignmentParameterByConstRefVisitor extends C60_00CopyAssignmentSignatureVisitor {

    public C60_02CopyAssignmentParameterByConstRefVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void report(final IASTDeclarator declarator) {
        final ICPPASTParameterDeclaration[] parameters = ((ICPPASTFunctionDeclarator) declarator).getParameters();

        if (parameters.length == 1) {
            final IASTPointerOperator[] poops = parameters[0].getDeclarator().getPointerOperators();
            if (poops.length != 1 || !parameters[0].getDeclSpecifier().isConst() || !(poops[0] instanceof ICPPASTReferenceOperator)) {
                checker.reportProblem(ProblemId.P_C60_02, parameters[0]);
            }
        }
    }

}
