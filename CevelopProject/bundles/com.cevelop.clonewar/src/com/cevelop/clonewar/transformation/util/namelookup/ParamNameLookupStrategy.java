package com.cevelop.clonewar.transformation.util.namelookup;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;


/**
 * Lookup strategy to find the name of a parameter node.
 *
 * @author ythrier(at)hsr.ch
 */
public class ParamNameLookupStrategy implements NameLookupStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    public String lookupName(IASTNode node) {
        ICPPASTParameterDeclaration param = (ICPPASTParameterDeclaration) node.getParent();
        return param.getDeclarator().getName().toString();
    }
}
