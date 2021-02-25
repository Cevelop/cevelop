package com.cevelop.elevator.ast.analysis.conditions;

import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNewExpression;

import com.cevelop.elevator.ast.analysis.NodeProperties;


public class IsElevatedNewExpression extends Condition {

    @Override
    public boolean satifies(final IASTNode node) {
        final NodeProperties properties = new NodeProperties(node);
        return properties.hasAncestor(ICPPASTNewExpression.class) && properties.getAncestor(ICPPASTNewExpression.class)
                .getInitializer() instanceof IASTInitializerList;
    }

}
