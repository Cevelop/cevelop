package com.cevelop.elevator.ast.analysis.conditions;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTReferenceOperator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;


public class IsReference extends Condition {

    @Override
    public boolean satifies(final IASTNode node) {
        if (node instanceof ICPPASTConstructorChainInitializer) {
            return isReferenceField(((ICPPASTConstructorChainInitializer) node).getMemberInitializerId());
        }
        if (node instanceof IASTDeclarator) {
            return isReferenceField(((IASTDeclarator) node).getName()) || isReferenceDeclaration((IASTDeclarator) node);
        }
        return false;
    }

    private boolean isReferenceField(final IASTName identifier) {
        final IBinding binding = identifier.resolveBinding();
        return binding instanceof IVariable && ((IVariable) binding).getType() instanceof ICPPReferenceType;
    }

    private boolean isReferenceDeclaration(final IASTDeclarator declarator) {
        return (declarator.getPointerOperators().length > 0) && (declarator.getPointerOperators()[0] instanceof ICPPASTReferenceOperator);
    }
}
