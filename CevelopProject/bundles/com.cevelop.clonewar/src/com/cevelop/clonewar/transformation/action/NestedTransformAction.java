package com.cevelop.clonewar.transformation.action;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeId;

import com.cevelop.clonewar.transformation.util.namelookup.NestedNameLookupStrategy;


/**
 * Transform a nested type into a template parameter, for example: <br>
 * <br>
 * <code>void foo(std::vector&lt;int&gt; bar) { ... }</code> <br>
 * <br>
 * is transformed into: <br>
 * <br>
 * <code>template&lt;typename T&gt; void foo(std::vector&lt;T&gt; bar) { ... }</code>
 * <br>
 * <br>
 * Same applies for named types (e.g. a struct or a class).
 *
 * @author ythrier(at)hsr.ch
 */
public class NestedTransformAction extends TransformAction {

    /**
     * Create the nested transform action and set the name resolving strategy.
     */
    public NestedTransformAction() {
        super(new NestedNameLookupStrategy());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performTransform() {
        ICPPASTTypeId nested = (ICPPASTTypeId) getNode().getParent();
        nested.setDeclSpecifier(getNodeFactory().newNamedTypeSpecifier(getTemplateName()));
    }
}
