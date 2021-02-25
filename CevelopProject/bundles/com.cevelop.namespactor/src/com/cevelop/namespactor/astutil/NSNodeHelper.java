/******************************************************************************
 * Copyright (c) 2012 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ueli Kunz <kunz@ideadapt.net>, Jules Weder <julesweder@gmail.com> - initial API and implementation
 ******************************************************************************/
package com.cevelop.namespactor.astutil;

import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.internal.ui.refactoring.utils.NodeHelper;


/**
 * @author kunz@ideadapt.net, Jules Weder
 */
@SuppressWarnings("restriction")
public class NSNodeHelper extends NodeHelper {

    public static boolean isNodeEnclosedBy(IASTNode enclosingCompound, IASTNode descendantNode) {
        boolean compoundFound = false;
        IASTNode currNode = descendantNode;
        IASTCompoundStatement currCompound = null;

        while (currNode != null) {
            currCompound = NSNodeHelper.findCompoundStatementInAncestors(currNode);

            if (currCompound == null) {
                break;
            } else {
                if (currCompound.equals(enclosingCompound)) {
                    compoundFound = true;
                    break;
                }

                currNode = currCompound.getParent();
            }
        }

        return compoundFound;
    }

    public static boolean isNodeEnclosedByScopeDefinedBy(IASTNode descenantNode, IASTNode nsDef) {
        IASTNode currNode = descenantNode.getParent();

        while (currNode != null) {
            if (currNode.equals(nsDef)) {
                return true;
            }
            currNode = currNode.getParent();
        }
        return false;
    }

    public static boolean isBindingDefinedWithin(IBinding descendant, IBinding potentialowner) {
        IBinding search = descendant;

        while (search != null) {
            if (search.equals(potentialowner)) {
                return true;
            }
            search = search.getOwner();
        }
        return false;
    }

    public static IASTNode getRoot(IASTNode node) {
        while (node != null && node.getParent() != null) {
            node = node.getParent();
        }
        return node;
    }

    @SuppressWarnings("unchecked")
    public static <T> T findAncestorOf(IASTNode node, Class<T> ancestorClass) {
        IASTNode searchNode = node.getParent();
        while (searchNode != null && !isInstanceOf(ancestorClass, searchNode)) {
            searchNode = searchNode.getParent();
        }
        return (T) searchNode;
    }

    public static <T> boolean isInstanceOf(Class<T> clazz, Object obj) {
        if (obj == null) {
            return false;
        }
        return clazz.isAssignableFrom(obj.getClass());
    }

    public static <T extends IASTNode> T findOuterMost(Class<T> clazz, IASTNode descendantNode) {
        T outerMost = findAncestorOf(descendantNode, clazz);
        T currOuter = outerMost;
        while (currOuter != null) {
            currOuter = findAncestorOf(currOuter, clazz);
            if (currOuter != null) {
                outerMost = currOuter;
            }
        }
        return outerMost;
    }

    public static <T> boolean hasAncestor(IASTNode node, Class<T> clazz) {
        return findAncestorOf(node, clazz) != null;
    }

    public static boolean isInOrIsCompositeDeclaration(IASTNode node) {
        if (node == null) {
            return false;
        }
        if (node instanceof ICPPASTCompositeTypeSpecifier) {
            return true;
        }
        return isInOrIsCompositeDeclaration(node.getParent());
    }

    @SuppressWarnings("unchecked")
    public static <T> T findAncestorOrInstanceOf(IASTNode node, Class<T> clazz) {
        IASTNode searchNode = node;
        while (searchNode != null && !isInstanceOf(clazz, searchNode)) {
            searchNode = searchNode.getParent();
        }
        return (T) searchNode;
    }

    public static IASTNode getASTSiblingOf(IASTNode node, IASTTranslationUnit ast) {
        int nodeOffset = node.getFileLocation().getNodeOffset();
        int nodeLength = node.getFileLocation().getNodeLength();
        int tuLength = ast.getFileLocation().getNodeLength();

        IASTNode sibling = ast.getNodeSelector(null).findFirstContainedNode(nodeOffset + nodeLength, tuLength);

        while (!sibling.getParent().equals(node.getParent())) {
            sibling = sibling.getParent();
        }

        return sibling;
    }

    public static IASTCompoundStatement findCompoundStatementInAncestors(IASTNode node) {
        while (node != null) {
            if (node instanceof IASTCompoundStatement) {
                return (IASTCompoundStatement) node;
            }
            node = node.getParent();
        }
        return null;
    }

    static public IASTSimpleDeclaration nodeIsInTypedefSimpleDeclaration(IASTNode node) {
        while (node != null) {
            //			if (node instanceof ICPPASTParameterDeclaration){
            //				// we are within a parameter name, so do not activate
            //				//return null;
            //			}else
            if (node instanceof IASTSimpleDeclaration) {
                IASTSimpleDeclaration decl = (IASTSimpleDeclaration) node;
                IASTDeclSpecifier declspecifier = decl.getDeclSpecifier();
                if (declspecifier.getStorageClass() == IASTDeclSpecifier.sc_typedef) {
                    return decl;
                } else {
                    return null;
                }
            }
            node = node.getParent();
        }
        return null;
    }
}
