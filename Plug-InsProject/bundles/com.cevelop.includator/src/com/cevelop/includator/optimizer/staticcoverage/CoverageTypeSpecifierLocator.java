package com.cevelop.includator.optimizer.staticcoverage;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;


public class CoverageTypeSpecifierLocator extends ASTVisitor {

    public CoverageTypeSpecifierLocator() {
        super(true);
    }

    @Override
    public int visit(IASTDeclSpecifier declSpec) {
        if (declSpec instanceof ICPPASTNamedTypeSpecifier) {
            ICPPASTNamedTypeSpecifier namedTypeSpecifier = (ICPPASTNamedTypeSpecifier) declSpec;
            IBinding typeBinding = namedTypeSpecifier.getName().resolveBinding();
            if (typeBinding instanceof ICPPClassType && hasDeclarationValueDeclarator(declSpec)) {
                processCandidate((ICPPClassType) typeBinding);
            }
        }
        return super.visit(declSpec);
    }

    private void processCandidate(ICPPClassType classType) {
        ICPPMethod[] methods = classType.getAllDeclaredMethods();
        methods[0].isDestructor();
    }

    private boolean hasDeclarationValueDeclarator(IASTDeclSpecifier declSpec) {
        if (declSpec.getParent() instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration simpleDecl = (IASTSimpleDeclaration) declSpec.getParent();
            for (IASTDeclarator curDeclarator : simpleDecl.getDeclarators()) {
                if (curDeclarator.getPointerOperators().length == 0) {
                    return true;
                }
            }
        }
        if (declSpec.getParent() instanceof IASTParameterDeclaration) {
            IASTParameterDeclaration paramDeclaration = (IASTParameterDeclaration) declSpec.getParent();
            return paramDeclaration.getDeclarator() == null || paramDeclaration.getDeclarator().getPointerOperators().length == 0;
        }
        return false;
    }

}
