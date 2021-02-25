package com.cevelop.clonewar.transformation.action;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;

import com.cevelop.clonewar.transformation.util.namelookup.ReturnNameLookupStrategy;


/**
 * Transform a return type into a template parameter, for example: <br>
 * <br>
 * <code>int foo(int bar) { ... }</code> <br>
 * <br>
 * is transformed into: <br>
 * <br>
 * <code>template&lt;typename T&gt; T foo(int bar) { ... }</code> <br>
 * <br>
 * Same applies for named types (e.g. a struct or a class).
 *
 * @author ythrier(at)hsr.ch
 */
public class ReturnTransformAction extends TransformAction {

    /**
     * Create the return transform action and set the name resolving strategy.
     */
    public ReturnTransformAction() {
        super(new ReturnNameLookupStrategy());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performTransform() {
        ICPPASTFunctionDefinition function = (ICPPASTFunctionDefinition) getNode().getParent();
        function.setDeclSpecifier(getNodeFactory().newNamedTypeSpecifier(getTemplateName()));
    }
}
