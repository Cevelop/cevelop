package com.cevelop.gslator.checkers.visitors.C30ToC37DestructorRules;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.utils.ASTHelper;


public class C31_02DestructorHasNoBodyVisitor extends C31_01NoDestructorVisitor {

    public C31_02DestructorHasNoBodyVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    public int visit(final IASTDeclSpecifier declSpec) {
        if (declSpec instanceof ICPPASTCompositeTypeSpecifier) {
            if (hasGslOwners(declSpec) && !hasNoDestructor() && destructorBodyEmpty() && nodeHasNoIgnoreAttribute(this, destructor)) {
                checker.reportProblem(ProblemId.P_C31_02, ASTHelper.getFunctionDeclaratorFromDeclarationOrDefinition(destructor));
            }
        }
        return super.visit(declSpec);
    }

    protected boolean destructorBodyEmpty() {
        if (destructor instanceof IASTSimpleDeclaration) {
            return getImplFromDeclaration((IASTSimpleDeclaration) destructor).getBody() == null;
        }
        if (destructor instanceof ICPPASTFunctionDefinition) {
            return ((IASTFunctionDefinition) destructor).getBody() == null;
        }
        return false;
    }

}
