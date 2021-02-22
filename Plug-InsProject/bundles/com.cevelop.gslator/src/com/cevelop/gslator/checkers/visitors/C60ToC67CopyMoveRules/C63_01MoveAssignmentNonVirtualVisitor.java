package com.cevelop.gslator.checkers.visitors.C60ToC67CopyMoveRules;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.ids.IdHelper.ProblemId;


public class C63_01MoveAssignmentNonVirtualVisitor extends C63_00MoveAssignmentSignatureVisitor {

    public C63_01MoveAssignmentNonVirtualVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void report(final IASTDeclarator declarator, final IASTDeclSpecifier declSpec) {
        if (((ICPPASTDeclSpecifier) declSpec).isVirtual()) {
            checker.reportProblem(ProblemId.P_C63_01, declSpec);
        }
    }

}
