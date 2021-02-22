package com.cevelop.clonewar.transformation.util.namelookup;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;


/**
 * Lookup strategy to find the name of a body node.
 *
 * @author ythrier(at)hsr.ch
 */
public class BodyNameLookupStrategy implements NameLookupStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public String lookupName(IASTNode node) {
        IASTSimpleDeclaration body = (IASTSimpleDeclaration) node.getParent();
        return body.getDeclarators()[0].getName().toString();
    }
}
