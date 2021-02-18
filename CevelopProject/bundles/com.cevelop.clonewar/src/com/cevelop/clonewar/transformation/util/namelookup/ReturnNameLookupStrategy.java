package com.cevelop.clonewar.transformation.util.namelookup;

import org.eclipse.cdt.core.dom.ast.IASTNode;


/**
 * Lookup strategy to find the name of a return node. Since return does not have
 * a name, a constant string value <b>return</b> is used.
 *
 * @author ythrier(at)hsr.ch
 */
public class ReturnNameLookupStrategy implements NameLookupStrategy {

    public static final String RETURN_SPECIFIER = "return";

    @Override
    public String lookupName(IASTNode node) {
        return RETURN_SPECIFIER;
    }
}
