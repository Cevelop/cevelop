package com.cevelop.codeanalysator.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTAttributeOwner;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCatchHandler;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;


/* BEGIN GSLATOR */
@SuppressWarnings("rawtypes")
public class AttributeOwnerHelper {

    public final static List<Class> invalidAttributeOwners = new ArrayList<>(Arrays.asList(ICPPASTNamedTypeSpecifier.class,
            ICPPASTCatchHandler.class));

    public final static List<Class> unwantedAttributeOwners = new ArrayList<>(Arrays.asList(ICPPASTFunctionDeclarator.class,
            ICPPASTDeclarator.class, IASTDeclarationStatement.class));

    public static boolean valid(Object obj) {
        return obj instanceof IASTAttributeOwner && !instanceOfInvalid(obj);
    }

    public static boolean wanted(Object obj) {
        return valid(obj) && !instanceOfUnwanted(obj);
    }

    public static boolean instanceOfInvalid(Object obj) {
        return instanceOfOne(obj, invalidAttributeOwners);
    }

    public static boolean instanceOfUnwanted(Object obj) {
        return instanceOfOne(obj, unwantedAttributeOwners);
    }

    private static boolean instanceOfOne(Object obj, List<Class> list) {
        for (Class theClass : list) {
            if (theClass.isInstance(obj)) {
                return true;
            }
        }
        return false;
    }

    public static IASTAttributeOwner getWantedAttributeOwner(final IASTNode node) {
        IASTAttributeOwner attrnode = getValidAttributeOwner(node);
        if (attrnode == null) {
            return null;
        }
        if (instanceOfUnwanted(attrnode)) {
            return getWantedAttributeOwner(attrnode.getParent());
        }
        return attrnode;
    }

    public static IASTAttributeOwner getValidAttributeOwner(IASTNode node) {
        while (!valid(node) && !(node instanceof IASTTranslationUnit)) {
            if (node == null) {
                return null;
            }
            node = node.getParent();
        }
        if (node instanceof IASTTranslationUnit) {
            return null;
        }
        return (IASTAttributeOwner) node;
    }
}
/* END GSLATOR */
