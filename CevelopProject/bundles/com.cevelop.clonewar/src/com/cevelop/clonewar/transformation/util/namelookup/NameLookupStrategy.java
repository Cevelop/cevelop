package com.cevelop.clonewar.transformation.util.namelookup;

import org.eclipse.cdt.core.dom.ast.IASTNode;


/**
 * An interface to allow the lookup the name of a variable based on the type
 * node.
 *
 * @author ythrier(at)hsr.ch
 */
public interface NameLookupStrategy {

    /**
     * Lookup the name of the variable.
     *
     * @param node
     * Type node.
     * @return Name of the variable.
     */
    public String lookupName(IASTNode node);
}
