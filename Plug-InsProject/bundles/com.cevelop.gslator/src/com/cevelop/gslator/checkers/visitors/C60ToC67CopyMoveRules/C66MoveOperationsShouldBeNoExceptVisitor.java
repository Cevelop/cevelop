package com.cevelop.gslator.checkers.visitors.C60ToC67CopyMoveRules;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.BaseNoexceptVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.utils.ASTHelper;


public class C66MoveOperationsShouldBeNoExceptVisitor extends BaseNoexceptVisitor {

    public C66MoveOperationsShouldBeNoExceptVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclSpecifiers = true;
    }

    @Override
    public int visit(final IASTDeclSpecifier declSpec) {
        return super.visit(declSpec);
    }

    @Override
    protected List<IASTDeclaration> getDeclaration(final ICPPASTCompositeTypeSpecifier declSpec) {
        return ASTHelper.getSpecialMemberFunctions(declSpec, ASTHelper.SpecialFunction.MoveAssignment, ASTHelper.SpecialFunction.MoveConstructor);
    }

    @Override
    protected void report(final IASTNode node) {
        checker.reportProblem(ProblemId.P_C66, node);
    }

}
