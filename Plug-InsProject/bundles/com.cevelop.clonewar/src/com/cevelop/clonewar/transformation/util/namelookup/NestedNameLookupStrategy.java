package com.cevelop.clonewar.transformation.util.namelookup;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCastExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTemplateId;


/**
 * Lookup strategy to find the name of a nested node.
 *
 * @author ythrier(at)hsr.ch
 */

public class NestedNameLookupStrategy implements NameLookupStrategy {

    private static final String                                       UNRESOLVABLE_NAME   = "<unresolvable>";
    private static Map<Class<? extends IASTNode>, NameLookupStrategy> nameLookupRegistry_ = new HashMap<>();

    /**
     * Register strategies for nested node name lookup.
     */
    static {
        nameLookupRegistry_.put(CPPASTTemplateId.class, new TemplateNameLookupStrategy());
        nameLookupRegistry_.put(CPPASTCastExpression.class, new CastNameLookupStrategy());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String lookupName(IASTNode node) {
        IASTNode lookupNode = node.getParent().getParent();
        if (!nameLookupRegistry_.containsKey(lookupNode.getClass())) return UNRESOLVABLE_NAME;
        String name = nameLookupRegistry_.get(lookupNode.getClass()).lookupName(node);
        if (name == null) return UNRESOLVABLE_NAME;
        return name;
    }
}
