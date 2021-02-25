package com.cevelop.clonewar.transformation.util.namelookup;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamedTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTParameterDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;


/**
 * Lookup strategy to find the name of a template node.
 *
 * @author ythrier(at)hsr.ch
 */

public class TemplateNameLookupStrategy implements NameLookupStrategy {

    private static final String TEMPLATE_SPECIFIER = "Template type of: ";

    /**
     * {@inheritDoc}
     */
    @Override
    public String lookupName(IASTNode node) {
        if (isParameter(node)) return TEMPLATE_SPECIFIER + lookupFromParameter(node);
        if (isBody(node)) return TEMPLATE_SPECIFIER + lookupFromBody(node);
        if (isReturn(node)) return TEMPLATE_SPECIFIER + ReturnNameLookupStrategy.RETURN_SPECIFIER;
        return null;
    }

    /**
     * Lookup the name from a body type.
     *
     * @param node
     * Node.
     * @return Name.
     */
    private String lookupFromBody(IASTNode node) {
        CPPASTSimpleDeclaration body = findBodyNode(node);
        String name = "";
        for (IASTDeclarator decl : body.getDeclarators())
            name += decl.getName() + ", ";
        if (!name.isEmpty()) name = name.substring(0, name.length() - 2);
        return name;
    }

    /**
     * Check if the node name have to be resolved from a body type.
     *
     * @param node
     * Node.
     * @return True if the node name should be resolved from a body type,
     * otherwise false.
     */
    private boolean isBody(IASTNode node) {
        if (findBodyNode(node) == null) return false;
        return true;
    }

    /**
     * Find the body node.
     *
     * @param node
     * Node.
     * @return The body node or null.
     */
    private CPPASTSimpleDeclaration findBodyNode(IASTNode node) {
        while (node != null && !(node instanceof CPPASTSimpleDeclaration))
            node = node.getParent();
        return (CPPASTSimpleDeclaration) node;
    }

    /**
     * Check if the node name have to be resolved from a return type.
     *
     * @param node
     * Node.
     * @return True if the node name should be resolved from a return type,
     * otherwise false.
     */
    private boolean isReturn(IASTNode node) {
        while (node != null && !(node instanceof CPPASTNamedTypeSpecifier))
            node = node.getParent();
        if (node == null) return false;
        return true;
    }

    /**
     * Lookup the name from parameter.
     *
     * @param node
     * Node.
     * @return Name.
     */
    private String lookupFromParameter(IASTNode node) {
        CPPASTParameterDeclaration param = findParameterNode(node);
        return param.getDeclarator().getName().toString();
    }

    /**
     * Check if the node name have to be resolved from a parameter.
     *
     * @param node
     * Node.
     * @return True if the node name should be resolved from a parameter,
     * otherwise false.
     */
    private boolean isParameter(IASTNode node) {
        if (findParameterNode(node) == null) return false;
        return true;
    }

    /**
     * Find the parameter node.
     *
     * @param node
     * Node.
     * @return The parameter node or null.
     */
    private CPPASTParameterDeclaration findParameterNode(IASTNode node) {
        while (node != null && !(node instanceof CPPASTParameterDeclaration))
            node = node.getParent();
        return (CPPASTParameterDeclaration) node;
    }
}
