package com.cevelop.clonewar.transformation.util.namelookup;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCastExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTLiteralExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTUnaryExpression;


/**
 * Lookup strategy to find the name of a cast node.
 *
 * @author ythrier(at)hsr.ch
 */

public class CastNameLookupStrategy implements NameLookupStrategy {

    private static final String CAST_SPECIFIER = "Cast type of: ";

    /**
     * {@inheritDoc}
     */
    @Override
    public String lookupName(IASTNode node) {
        CPPASTCastExpression cast = (CPPASTCastExpression) node.getParent().getParent();
        IASTNode operand = cast.getOperand();
        while (!(operand instanceof CPPASTIdExpression)) {
            if (operand instanceof CPPASTUnaryExpression) {
                operand = ((CPPASTUnaryExpression) operand).getOperand();
                continue;
            }
            if (operand instanceof CPPASTLiteralExpression) break;
        }
        return CAST_SPECIFIER + operand.toString();
    }
}
