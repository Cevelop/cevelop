package com.cevelop.gslator.checkers.visitors;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.utils.ASTHelper;


public abstract class BaseNoexceptVisitor extends BaseVisitor {

    protected BaseNoexceptVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclSpecifiers = true;
    }

    @Override
    public int visit(final IASTDeclSpecifier declSpec) {
        if (declSpec instanceof ICPPASTCompositeTypeSpecifier) {
            final List<IASTDeclaration> decls = getDeclaration((ICPPASTCompositeTypeSpecifier) declSpec);
            if (decls != null) {
                for (IASTDeclaration decl : decls) {
                    final ICPPASTFunctionDeclarator declarator = ASTHelper.getFunctionDeclaratorFromDeclarationOrDefinition(decl);

                    if (declarator != null && nodeHasNoIgnoreAttribute(this, declarator) && hasNoNoexcept(declarator)) {
                        report(declarator);
                    }
                }
            }
        }
        return super.visit(declSpec);
    }

    protected abstract List<IASTDeclaration> getDeclaration(final ICPPASTCompositeTypeSpecifier declSpec);

    protected abstract void report(IASTNode node);

}
