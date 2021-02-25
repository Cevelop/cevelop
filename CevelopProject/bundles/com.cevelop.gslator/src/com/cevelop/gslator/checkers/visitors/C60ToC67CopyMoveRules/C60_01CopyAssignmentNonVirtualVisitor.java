package com.cevelop.gslator.checkers.visitors.C60ToC67CopyMoveRules;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.utils.ASTHelper;


public class C60_01CopyAssignmentNonVirtualVisitor extends C60_00CopyAssignmentSignatureVisitor {

    public C60_01CopyAssignmentNonVirtualVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void report(final IASTDeclarator declarator) {
        final IASTDeclSpecifier declSpec = ASTHelper.getDeclSpecifierFromDeclaration((IASTDeclaration) declarator.getParent());
        if (declSpec != null && declSpec instanceof ICPPASTDeclSpecifier) {
            report(declarator, declSpec);
        }
    }

    protected void report(final IASTDeclarator declarator, final IASTDeclSpecifier declSpec) {
        if (((ICPPASTDeclSpecifier) declSpec).isVirtual()) {
            checker.reportProblem(ProblemId.P_C60_01, declSpec);
        }
    }

}
