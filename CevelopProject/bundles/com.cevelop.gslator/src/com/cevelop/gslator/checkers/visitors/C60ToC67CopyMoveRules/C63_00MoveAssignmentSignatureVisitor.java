package com.cevelop.gslator.checkers.visitors.C60ToC67CopyMoveRules;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.BaseVisitor;
import com.cevelop.gslator.utils.ASTHelper;


public abstract class C63_00MoveAssignmentSignatureVisitor extends BaseVisitor {

    public C63_00MoveAssignmentSignatureVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarators = true;
    }

    @Override
    public int visit(final IASTDeclarator declarator) {
        if (declarator instanceof ICPPASTFunctionDeclarator) {
            final IASTNode parent = declarator.getParent();
            if (castIfNotTypeId(parent)) {
                final IASTDeclaration decl = (IASTDeclaration) parent;
                if (nodeHasNoIgnoreAttribute(this, decl) && ASTHelper.isMoveAssignment(decl)) {
                    final IASTDeclSpecifier declSpec = ASTHelper.getDeclSpecifierFromDeclaration(decl);

                    if (declSpec != null && declSpec instanceof ICPPASTDeclSpecifier) {
                        report(declarator, declSpec);
                    }

                }
            }
        }
        return super.visit(declarator);
    }

    private boolean castIfNotTypeId(final IASTNode parent) {
        if (parent instanceof IASTDeclaration) {
            final IASTDeclaration decl = (IASTDeclaration) parent;
            return ASTHelper.isMoveAssignment(decl) || ASTHelper.isMoveConstructor(decl);
        }
        return false;
    }

    protected abstract void report(final IASTDeclarator declarator, IASTDeclSpecifier declSpec);

}
