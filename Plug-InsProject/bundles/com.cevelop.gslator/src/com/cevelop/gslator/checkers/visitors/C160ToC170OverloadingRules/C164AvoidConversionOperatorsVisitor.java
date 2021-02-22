package com.cevelop.gslator.checkers.visitors.C160ToC170OverloadingRules;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConversionName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.BaseVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.utils.ASTHelper;


public class C164AvoidConversionOperatorsVisitor extends BaseVisitor {

    public C164AvoidConversionOperatorsVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarators = true;
    }

    @Override
    public int visit(final IASTDeclarator declarator) {
        if (declarator instanceof ICPPASTFunctionDeclarator && !ASTHelper.isExplicit(declarator) && nodeHasNoIgnoreAttribute(this, declarator)) {
            ICPPASTFunctionDeclarator funcDecl = (ICPPASTFunctionDeclarator) declarator;
            IASTName name = funcDecl.getName();
            if (name instanceof ICPPASTConversionName) {
                checker.reportProblem(ProblemId.P_C164, funcDecl);
            }
        }
        return super.visit(declarator);
    }
}
