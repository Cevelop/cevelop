package com.cevelop.intwidthfixator.visitors.intvisitors;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;


public class SimpleDeclSpecVisitor extends ASTVisitor {

    public interface ICPPASTSimpleDeclSpecifierAction {

        void conditionalReport(ICPPASTSimpleDeclSpecifier simpleDeclSpec);

    }

    private final ICPPASTSimpleDeclSpecifierAction action;
    {
        shouldVisitDeclSpecifiers = true;
    }

    public SimpleDeclSpecVisitor(final ICPPASTSimpleDeclSpecifierAction action) {
        this.action = action;
    }

    @Override
    public int visit(final IASTDeclSpecifier declSpec) {
        if (declSpec instanceof ICPPASTSimpleDeclSpecifier) {
            action.conditionalReport((ICPPASTSimpleDeclSpecifier) declSpec);
        }
        return PROCESS_CONTINUE;
    }
}
