package com.cevelop.intwidthfixator.visitors.cstdintvisitors;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;


public class NamedTypeSpecVisitor extends ASTVisitor {

    public interface ICPPASTNamedTypeSpecifierAction {

        void conditionalReport(ICPPASTNamedTypeSpecifier namedTypeSpec);
    }

    private final ICPPASTNamedTypeSpecifierAction action;
    {
        shouldVisitDeclSpecifiers = true;
    }

    public NamedTypeSpecVisitor(final ICPPASTNamedTypeSpecifierAction action) {
        this.action = action;
    }

    @Override
    public int visit(final IASTDeclSpecifier declSpec) {
        if (declSpec instanceof ICPPASTNamedTypeSpecifier) {
            action.conditionalReport((ICPPASTNamedTypeSpecifier) declSpec);
        }
        return PROCESS_CONTINUE;
    }
}
