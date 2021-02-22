package com.cevelop.elevator.ast.analysis.conditions;

import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTMacroExpansionLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroExpansion;


/**
 * Checks whether an expression is an object macro call of the NULL macro.
 *
 */
public class IsNullMacroExpression extends Condition {

    @Override
    public boolean satifies(IASTNode node) {
        if (node instanceof IASTLiteralExpression) {
            IASTLiteralExpression expression = (IASTLiteralExpression) node;
            char[] value = expression.getValue();
            if (value.length == 1 && value[0] == '0') {
                IASTNodeLocation[] locations = node.getNodeLocations();
                if (locations.length > 0 && locations[0] instanceof IASTMacroExpansionLocation) {
                    IASTMacroExpansionLocation macroLocation = (IASTMacroExpansionLocation) locations[0];
                    IASTPreprocessorMacroExpansion expansion = macroLocation.getExpansion();
                    IASTName reference = expansion.getMacroReference();
                    return reference.toString().equals("NULL");
                }
            }
        }
        return false;
    }
}
