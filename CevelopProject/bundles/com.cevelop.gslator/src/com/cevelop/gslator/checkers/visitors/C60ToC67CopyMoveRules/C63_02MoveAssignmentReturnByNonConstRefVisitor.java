package com.cevelop.gslator.checkers.visitors.C60ToC67CopyMoveRules;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTReferenceOperator;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.ids.IdHelper.ProblemId;


public class C63_02MoveAssignmentReturnByNonConstRefVisitor extends C63_00MoveAssignmentSignatureVisitor {

    public C63_02MoveAssignmentReturnByNonConstRefVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void report(final IASTDeclarator declarator, final IASTDeclSpecifier declSpec) {
        final IASTPointerOperator[] poops = declarator.getPointerOperators();
        if (poops.length != 1 || ((ICPPASTDeclSpecifier) declSpec).isConst() || !(poops[0] instanceof ICPPASTReferenceOperator)) {
            checker.reportProblem(ProblemId.P_C63_02, declSpec);
        }
    }

}
