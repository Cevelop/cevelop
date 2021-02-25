package com.cevelop.clonewar.transformation.action;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;

import com.cevelop.clonewar.transformation.util.namelookup.ParamNameLookupStrategy;


/**
 * Transform a parameter into a template parameter, for example: <br>
 * <br>
 * <code>void foo(int bar) { ... }</code> <br>
 * <br>
 * is transformed into: <br>
 * <br>
 * <code>template&lt;typename T&gt; void foo(T bar) { ... }</code> <br>
 * <br>
 * Same applies for named types (e.g. a struct or a class).
 *
 * @author ythrier(at)hsr.ch
 */
public class ParamTransformAction extends TransformAction {

    /**
     * Create the param transform action and set the name resolving strategy.
     */
    public ParamTransformAction() {
        super(new ParamNameLookupStrategy());
        setParameterAction(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performTransform() {
        ICPPASTParameterDeclaration param = (ICPPASTParameterDeclaration) getNode().getParent();
        param.setDeclSpecifier(getNodeFactory().newNamedTypeSpecifier(getTemplateName()));
    }
}
