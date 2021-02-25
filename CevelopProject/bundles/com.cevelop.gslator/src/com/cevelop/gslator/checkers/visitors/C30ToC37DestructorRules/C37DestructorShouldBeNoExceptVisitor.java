package com.cevelop.gslator.checkers.visitors.C30ToC37DestructorRules;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.BaseNoexceptVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.utils.ASTHelper;


public class C37DestructorShouldBeNoExceptVisitor extends BaseNoexceptVisitor {

    public C37DestructorShouldBeNoExceptVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    protected List<IASTDeclaration> getDeclaration(final ICPPASTCompositeTypeSpecifier declSpec) {
        return ASTHelper.getSpecialMemberFunctions(declSpec, ASTHelper.SpecialFunction.DefaultDestructor);
    }

    @Override
    protected void report(final IASTNode node) {
        checker.reportProblem(ProblemId.P_C37, node);
    }
}
