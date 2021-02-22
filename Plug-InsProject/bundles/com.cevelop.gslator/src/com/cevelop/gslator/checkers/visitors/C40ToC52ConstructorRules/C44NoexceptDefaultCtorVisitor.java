package com.cevelop.gslator.checkers.visitors.C40ToC52ConstructorRules;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.BaseNoexceptVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.utils.ASTHelper;


public class C44NoexceptDefaultCtorVisitor extends BaseNoexceptVisitor {

    public C44NoexceptDefaultCtorVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    protected List<IASTDeclaration> getDeclaration(final ICPPASTCompositeTypeSpecifier declSpec) {
        return ASTHelper.getSpecialMemberFunctions(declSpec, ASTHelper.SpecialFunction.DefaultConstructor);
    }

    @Override
    protected void report(final IASTNode node) {
        checker.reportProblem(ProblemId.P_C44, node);
    }
}
