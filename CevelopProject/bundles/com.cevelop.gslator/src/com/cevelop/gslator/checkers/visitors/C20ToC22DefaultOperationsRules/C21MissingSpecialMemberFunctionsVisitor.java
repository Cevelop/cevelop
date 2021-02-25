package com.cevelop.gslator.checkers.visitors.C20ToC22DefaultOperationsRules;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.BaseVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.utils.ASTHelper;


public class C21MissingSpecialMemberFunctionsVisitor extends BaseVisitor {

    public C21MissingSpecialMemberFunctionsVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclSpecifiers = true;
    }

    @Override
    public int visit(final IASTDeclSpecifier declSpec) {
        if (declSpec instanceof ICPPASTCompositeTypeSpecifier && nodeHasNoIgnoreAttribute(this, declSpec)) {
            IASTNode function = null;
            int numOfSpecialFunctionsFound = 0;

            for (final IASTDeclaration decl : ((ICPPASTCompositeTypeSpecifier) declSpec).getMembers()) {
                if (decl instanceof ICPPASTFunctionDefinition || decl instanceof IASTSimpleDeclaration) {
                    function = decl;

                    if (isSpecialFunction(decl)) {
                        numOfSpecialFunctionsFound++;
                    }
                }
            }

            if (!(numOfSpecialFunctionsFound == 0 || numOfSpecialFunctionsFound == 6)) {
                checker.reportProblem(ProblemId.P_C21, ASTHelper.getCompositeTypeSpecifier(function).getName());
            }
        }
        return super.visit(declSpec);
    }

    public boolean isSpecialFunction(final IASTDeclaration declaration) {
        return ASTHelper.isDefaultConstructor(declaration) || ASTHelper.isDefaultCopyAssignment(declaration) || ASTHelper.isDefaultCopyConstructor(
                declaration) || ASTHelper.isDefaultDestructor(declaration) || ASTHelper.isMoveConstructor(declaration) || ASTHelper.isMoveAssignment(
                        declaration);
    }

}
