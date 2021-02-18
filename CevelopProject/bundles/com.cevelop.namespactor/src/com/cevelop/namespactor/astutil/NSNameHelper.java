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

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPEnumeration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNamespace;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor;
import org.eclipse.cdt.internal.ui.refactoring.utils.NameHelper;


/**
 * @author Ueli Kunz, Jules Weder
 */
@SuppressWarnings("restriction")
public class NSNameHelper extends NameHelper {

    public static final String NAME_RESOLUTION_OPERATOR = "::";

    public static boolean isNodeQualifiedWithName(IASTName node, IName name) {
        if (!(node.getParent() instanceof ICPPASTQualifiedName)) {
            return false;
        }

        ICPPASTNameSpecifier[] names = ((ICPPASTQualifiedName) node.getParent()).getAllSegments();
        for (int i = 1; i < names.length; ++i) {

            if (names[i].equals(node) && names[i - 1].toString().equals(String.valueOf(name.getSimpleID()))) {
                return true;
            }
        }
        return false;
    }

    public static String[] getQualifiedUsingName(IBinding binding) {
        String[] ns = null;
        for (IBinding owner = binding.getOwner(); owner != null; owner = owner.getOwner()) {
            if (owner instanceof ICPPEnumeration && !((ICPPEnumeration) owner).isScoped()) {
                continue;
            }
            String n = owner.getName();
            if (n == null) break;
            if (owner instanceof ICPPFunction) break;
            if (owner instanceof ICPPClassType) continue;
            if (isNamespaceToIgnore(owner, n)) {
                continue;
            }

            ns = ArrayUtil.append(String.class, ns, n);
        }
        ns = ArrayUtil.trim(String.class, ns);
        return reverseArray(ns);
    }

    private static String[] reverseArray(String[] ns) {
        String[] result = new String[ns.length];
        for (int i = ns.length - 1; i >= 0; i--) {
            result[ns.length - i - 1] = ns[i];
        }
        return result;
    }

    public static String[] getQualifiedUDECNameInTypeDecl(IBinding binding) {
        String[] ns = null;
        for (IBinding owner = binding.getOwner(); owner != null; owner = owner.getOwner()) {
            if (owner instanceof ICPPEnumeration && !((ICPPEnumeration) owner).isScoped()) {
                continue;
            }
            String n = owner.getName();
            if (n == null) break;
            if (owner instanceof ICPPFunction) break;
            if (isNamespaceToIgnore(owner, n)) {
                continue;
            }

            ns = ArrayUtil.append(String.class, ns, n);
        }
        ns = ArrayUtil.trim(String.class, ns);
        String[] result = reverseArray(ns);
        return result;
    }

    private static boolean isNamespaceToIgnore(IBinding owner, String n) {
        return owner instanceof ICPPNamespace && (n.length() == 0 || ((ICPPNamespace) owner).isInline() && n.startsWith("__"));
    }

    public static ICPPASTQualifiedName copyQualifers(ICPPASTQualifiedName qName) {
        ICPPASTQualifiedName newNameNode = null;
        //IASTName[] names = qName.getNames();
        ICPPASTNameSpecifier[] names = qName.getQualifier();

        // add all except the last name
        newNameNode = ASTNodeFactory.getDefault().newQualifiedName(null);
        for (ICPPASTNameSpecifier name : names) {
            newNameNode.addNameSpecifier(name.copy());
        }
        //		for (int i = 0; i < names.length - 1; i++) {
        //			IASTName lastName = names[i].getLastName();
        //			newNameNode.addName(lastName.copy());
        //		}
        return newNameNode;
    }

    public static String buildQualifiedName(String[] qualifiedName) {
        // @see http://eclipsesourcecode.appspot.com/jsrcs/org.eclipse.cdt-core/org.eclipse.cdt.core/parser/org/eclipse/cdt/internal/core/dom/parser/cpp/semantics/CPPVisitor.java.html
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < qualifiedName.length; i++) {
            result.append(qualifiedName[i] + (i + 1 < qualifiedName.length ? NAME_RESOLUTION_OPERATOR : ""));
        }
        return result.toString();
    }

    public static ICPPASTQualifiedName prefixNameWith(IASTName prefixName, IASTName nameToPrefix) {
        ICPPASTQualifiedName newQName = ASTNodeFactory.getDefault().newQualifiedName(null);

        if (nameToPrefix.getParent() instanceof ICPPASTQualifiedName && !(nameToPrefix instanceof ICPPASTTemplateId)) {

            ICPPASTNameSpecifier[] namespecifiers = ((ICPPASTQualifiedName) nameToPrefix.getParent()).getAllSegments();
            for (ICPPASTNameSpecifier existingName : namespecifiers) {

                if (existingName.equals(nameToPrefix)) {
                    addPrefix(prefixName, newQName);
                }
                if (existingName instanceof IASTName)
                    newQName.addName((IASTName) existingName.copy());
                else newQName.addNameSpecifier(existingName.copy());
            }

        } else {
            addPrefix(prefixName, newQName);
            newQName.addName(nameToPrefix.copy());
        }

        return newQName;
    }

    private static void addPrefix(IASTName prefixName, ICPPASTQualifiedName newQName) {
        if (prefixName instanceof ICPPASTQualifiedName) {
            for (ICPPASTNameSpecifier prefix : ((ICPPASTQualifiedName) prefixName).getQualifier()) {
                newQName.addNameSpecifier(prefix.copy());
            }
            IASTName lastName = prefixName.getLastName();
            if (lastName != null) newQName.addName(lastName.copy());
        } else {
            newQName.addName(prefixName.copy());
        }
    }

    public static IBinding findOutermostClassTypeOfName(IBinding nameBinding) {
        return NSNameHelper.findOutermostClassTypeOfName(nameBinding, null);
    }

    private static IBinding findOutermostClassTypeOfName(IBinding binding, IBinding outermostType) {
        if (binding instanceof ICPPClassType) {
            outermostType = binding;
        }
        if (binding == null) {
            return outermostType;
        }
        return findOutermostClassTypeOfName(binding.getOwner(), outermostType);
    }

    public static boolean isSelectionInUnnamedScope(ICPPASTQualifiedName selectedQualifiedName) {
        return CPPVisitor.getContainingScope(selectedQualifiedName).getScopeName() == null;
    }

    public static boolean haveSameScope(IASTNode insertNode, IASTName selectedQualifiedName) {
        return CPPVisitor.getContainingScope(insertNode).equals(CPPVisitor.getContainingScope(selectedQualifiedName));
    }

    public static boolean isSameNameInTemplateId(ICPPASTTemplateId name, ICPPASTTemplateId iastName) {
        return name.getRawSignature().equals(iastName.getRawSignature());
    }

    public static String[] getQualifiedName(IBinding binding) { // stolen from CPPVisitor and modified
        String[] ns = null;
        for (IBinding owner = binding.getOwner(); owner != null; owner = owner.getOwner()) {
            if (owner instanceof ICPPEnumeration && !((ICPPEnumeration) owner).isScoped()) {
                continue;
            }
            String n = owner.getName();
            if (n == null) break;
            if (owner instanceof ICPPFunction) break;
            if (isNamespaceToIgnore(owner, n)) {
                // ignore anonymous and inline namespaces internal to the implementation, such as __1 from libstdc++
                continue;
            }

            ns = ArrayUtil.append(String.class, ns, n);
        }
        ns = ArrayUtil.trim(String.class, ns);
        String[] result = new String[ns.length + 1];
        for (int i = ns.length - 1; i >= 0; i--) {
            result[ns.length - i - 1] = ns[i];
        }
        result[ns.length] = binding.getName();
        return result;
    }

    public static void addNameOrNameSpecifier(ICPPASTQualifiedName newQName, ICPPASTNameSpecifier n) {
        CopyStyle style = CopyStyle.withoutLocations;
        addNameOrNameSpecifierWithStyle(newQName, n, style);
    }

    public static void addNameOrNameSpecifierWithStyle(ICPPASTQualifiedName newQName, ICPPASTNameSpecifier n, CopyStyle style) {
        if (n instanceof IASTName)
            newQName.addName((IASTName) n.copy(style));
        else newQName.addNameSpecifier(n.copy(style));
    }
}
