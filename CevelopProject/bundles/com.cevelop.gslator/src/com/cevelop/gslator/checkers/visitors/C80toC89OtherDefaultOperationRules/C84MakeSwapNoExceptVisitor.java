package com.cevelop.gslator.checkers.visitors.C80toC89OtherDefaultOperationRules;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.BaseNoexceptVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.utils.ASTHelper;


public class C84MakeSwapNoExceptVisitor extends BaseNoexceptVisitor {

    public C84MakeSwapNoExceptVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    protected List<IASTDeclaration> getDeclaration(final ICPPASTCompositeTypeSpecifier declSpec) {
        return ASTHelper.getSpecialMemberFunctions(declSpec, ASTHelper.SpecialFunction.SwapFunction);
    }

    @Override
    public int visit(final IASTDeclSpecifier declSpec) {
        if (declSpec instanceof IASTSimpleDeclSpecifier) {
            IASTNode parent = declSpec.getParent();
            if (parent instanceof IASTFunctionDefinition || parent instanceof IASTSimpleDeclaration) {

                ICPPASTFunctionDeclarator declarator = ASTHelper.getFunctionDeclaratorFromDeclarationOrDefinition(declSpec.getParent());
                if (ASTHelper.getSwapFunctionType((IASTDeclaration) parent, null) == ASTHelper.AnalyseSwapFunction.IsNamespaceFunction) {
                    if (declarator != null && nodeHasNoIgnoreAttribute(this, declarator) && hasNoNoexcept(declarator)) {
                        report(declarator);
                    }
                }

            }
        }
        return super.visit(declSpec);
    }

    @Override
    protected void report(final IASTNode node) {
        checker.reportProblem(ProblemId.P_C84, node);
    }

}
