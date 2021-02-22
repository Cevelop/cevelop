package com.cevelop.gslator.checkers.visitors.C60ToC67CopyMoveRules;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.BaseVisitor;
import com.cevelop.gslator.utils.ASTHelper;


public abstract class C60_00CopyAssignmentSignatureVisitor extends BaseVisitor {

    public C60_00CopyAssignmentSignatureVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarators = true;
    }

    @Override
    public int visit(final IASTDeclarator declarator) {
        if (declarator instanceof ICPPASTFunctionDeclarator) {
            if (nodeHasNoIgnoreAttribute(this, declarator) && castIfNotTypeId(declarator.getParent())) {
                report(declarator);
            }
        }
        return super.visit(declarator);
    }

    private boolean castIfNotTypeId(final IASTNode parent) {
        return parent instanceof IASTDeclaration ? ASTHelper.isDefaultCopyAssignment((IASTDeclaration) parent) : false;
    }

    protected abstract void report(IASTDeclarator declarator);

}
