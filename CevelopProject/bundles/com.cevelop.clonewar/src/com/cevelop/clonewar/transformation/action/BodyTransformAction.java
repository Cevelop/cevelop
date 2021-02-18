package com.cevelop.clonewar.transformation.action;

import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;

import com.cevelop.clonewar.transformation.util.namelookup.BodyNameLookupStrategy;


/**
 * Transform a body type into a template parameter, for example: <br>
 * <br>
 * <code>int foo(int bar)
 * <br>{
 * <br>int body = bar;
 * <br>}
 * </code> <br>
 * <br>
 * is transformed into: <br>
 * <br>
 * <code>template&lt;typename T&gt; int foo(T bar)
 * <br>{
 * <br>T body = bar;
 * <br>}
 * </code> <br>
 * <br>
 * Note that this transformation only applies the body and not the parameter
 * transformation.<br>
 * Same applies for named types (e.g. a struct or a class).
 *
 * @author ythrier(at)hsr.ch
 */
public class BodyTransformAction extends TransformAction {

    /**
     * Create the body transform action and set the name resolving strategy.
     */
    public BodyTransformAction() {
        super(new BodyNameLookupStrategy());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performTransform() {
        IASTSimpleDeclaration body = (IASTSimpleDeclaration) getNode().getParent();
        body.setDeclSpecifier(getNodeFactory().newNamedTypeSpecifier(getTemplateName()));
    }
}
