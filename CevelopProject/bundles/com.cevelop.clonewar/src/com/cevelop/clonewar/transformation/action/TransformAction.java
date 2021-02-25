package com.cevelop.clonewar.transformation.action;

import org.eclipse.cdt.core.dom.ast.IASTNode;

import com.cevelop.clonewar.transformation.util.TypeInformation;
import com.cevelop.clonewar.transformation.util.namelookup.NameLookupStrategy;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ExtendedNodeFactory;


/**
 * Base class for different kinds of transformation that are applicable to
 * types:
 * <ul>
 * <li>Named type in body</li>
 * <li>Named type parameter</li>
 * <li>Named type return</li>
 * <li>Simple type in body</li>
 * <li>Simple type parameter</li>
 * <li>Simple type return</li>
 * <li>Nested type of a template, cast, or similar</li>
 * </ul>
 * The subclasses take care of the according transformation process. Since
 * changes are only allowed on a non-frozen AST only a copy of the transformed
 * node is hold in this class. Note that when using multiple transform actions
 * the same copy has to be used, otherwise replacing the original AST with the
 * copied AST will not work as expected.
 *
 * @author ythrier(at)hsr.ch
 */
public abstract class TransformAction {

    private ExtendedNodeFactory nodeFactory_ = new ExtendedNodeFactory();
    private boolean             paramAction_ = false;
    private boolean             perform_     = true;
    private NameLookupStrategy  nameLookup_;
    private TypeInformation     typeInfo_;
    private IASTNode            node_;

    /**
     * Create the action with the name lookup strategy.
     *
     * @param nameLookup
     * Name lookup strategy.
     */
    protected TransformAction(NameLookupStrategy nameLookup) {
        this.nameLookup_ = nameLookup;
    }

    /**
     * Set the type information of this action.
     *
     * @param typeInfo
     * Type info.
     */
    public void setTypeInformation(TypeInformation typeInfo) {
        this.typeInfo_ = typeInfo;
    }

    /**
     * Return whether this action is performed on a parameter or on another
     * type.
     *
     * @return True if this action transform a parameter, otherwise false.
     */
    public boolean isParameterAction() {
        return paramAction_;
    }

    /**
     * Enable/disable this action to be a parameter action.
     *
     * @param on
     * True to enable, false to disable.
     */
    protected void setParameterAction(boolean on) {
        this.paramAction_ = on;
    }

    /**
     * Return the node of this action.
     *
     * @return Node.
     */
    public IASTNode getNode() {
        return node_;
    }

    /**
     * Set the node of this action.
     *
     * @param node
     * Node.
     */
    public void setNode(IASTNode node) {
        this.node_ = node;
    }

    /**
     * Return whether this action should be performed.
     *
     * @return True if the action should be performed, otherwise false.
     */
    public boolean shouldPerform() {
        return perform_;
    }

    /**
     * Set whether this action should be performed.
     *
     * @param on
     * True if the action should be performed, otherwise false.
     */
    public void setPerform(boolean on) {
        this.perform_ = on;
    }

    /**
     * Return the template type name which is used in this action.
     *
     * @return Template type name.
     */
    protected String getTemplateName() {
        return typeInfo_.getTemplateName();
    }

    /**
     * Return the node factory to create new nodes needed in the transformation
     * process.
     *
     * @return Node factory.
     */
    protected ExtendedNodeFactory getNodeFactory() {
        return nodeFactory_;
    }

    /**
     * Lookup the name of the variable which is connected to the node to
     * transform.
     *
     * @return Name of the variable of the node.
     */
    public String getVariableName() {
        return nameLookup_.lookupName(getNode());
    }

    /**
     * Perform the transformation process.
     */
    public abstract void performTransform();
}
