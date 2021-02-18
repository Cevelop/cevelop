package com.cevelop.gslator.checkers.visitors.C30ToC37DestructorRules;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.utils.ASTHelper;


public class C31_01NoDestructorVisitor extends C31_00DeleteOwnersInDestructorVisitor {

    protected ICPPASTCompositeTypeSpecifier struct;
    protected IASTDeclaration               destructor;

    public C31_01NoDestructorVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclSpecifiers = true;
    }

    @Override
    public int visit(final IASTDeclSpecifier declSpec) {
        if (declSpec instanceof ICPPASTCompositeTypeSpecifier && nodeHasNoIgnoreAttribute(this, declSpec)) {

            if (hasGslOwners(declSpec) && hasNoDestructor()) {
                checker.reportProblem(ProblemId.P_C31_01, struct.getName());
            }
        }
        return super.visit(declSpec);
    }

    protected boolean hasNoDestructor() {
        destructor = ASTHelper.getFirstSpecialMemberFunction(struct, ASTHelper.SpecialFunction.DefaultDestructor);
        if (destructor instanceof IASTSimpleDeclaration) {
            return getImplFromDeclaration((IASTSimpleDeclaration) destructor) == null;
        }

        return !(destructor instanceof ICPPASTFunctionDefinition);
    }

    protected boolean hasGslOwners(final IASTDeclSpecifier declSpec) {
        struct = (ICPPASTCompositeTypeSpecifier) declSpec;
        final List<IASTSimpleDeclaration> gslOwners = ASTHelper.collectGslOwners(ASTHelper.collectMemberVariables(struct));

        return gslOwners.size() > 0;
    }
}
