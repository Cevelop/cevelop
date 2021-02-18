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
package com.cevelop.namespactor.refactoring.eu;

import org.eclipse.cdt.codan.core.cxx.CxxAstUtils;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDirective;

import com.cevelop.namespactor.astutil.ASTNodeFactory;
import com.cevelop.namespactor.astutil.NSNameHelper;
import com.cevelop.namespactor.astutil.NSNodeHelper;
import com.cevelop.namespactor.astutil.NSSelectionHelper;


/**
 * @author Jules Weder
 */
@SuppressWarnings("restriction")
public abstract class EUReplaceVisitor extends ASTVisitor {

    protected EURefactoringContext context;

    {
        shouldVisitNames = true;
    }

    @Override
    public int visit(IASTName name) {
        if (name instanceof ICPPASTQualifiedName) {
            IASTName replacementName = buildReplacementName(name);
            if (replacementName != null) {
                if (((ICPPASTQualifiedName) replacementName).getQualifier().length < 1) {
                    replacementName = replacementName.getLastName();
                }
                if (replacementName == null && name.getParent() instanceof ICPPASTUsingDirective) {
                    removeUselessUsingDirective(name);
                    return PROCESS_SKIP;
                }
                replace(name, replacementName);
            }
        }
        removeUnqualifiedUsingDirective(name);
        return PROCESS_CONTINUE;

    }

    private void replace(IASTName name, IASTName replacementName) {
        context.nodesToReplace.put(name, replacementName);
        prepareInsertionPoint(name);
    }

    protected void removeUselessUsingDirective(IASTName name) {
        IASTNode parent = name.getParent();
        context.nodesToRemove.add(parent);
        prepareInsertionPoint(parent);
    }

    protected void prepareInsertionPoint(IASTNode node) {
        if (context.firstNameToReplace == null) {
            context.firstNameToReplace = node;
        }
    }

    protected IASTName buildReplacementName(IASTName name) { // TODO: debug and fix with new template mechanism
        if (isExtractCandidate(name)) {
            ICPPASTQualifiedName replaceName = ASTNodeFactory.getDefault().newQualifiedName(null);
            ICPPASTNameSpecifier[] names = getNamesOf(name);
            ICPPASTNameSpecifier foundName = searchNamesFor(context.startingNamespaceName, names);
            if (foundName != null && CxxAstUtils.isInMacro(foundName)) return null;
            if (isReplaceCandidate(foundName, name, names)) {
                boolean start = false;
                for (ICPPASTNameSpecifier iastName : names) {
                    if (isTemplateReplaceCandidate(foundName, iastName)) {
                        replaceName = buildReplacementTemplate((IASTName) iastName);
                        continue;
                    }
                    if (start) {
                        NSNameHelper.addNameOrNameSpecifier(replaceName, iastName);
                    }
                    if (isNameFound(foundName, iastName)) {
                        start = true;
                    }
                }
                if (isUnqualifiedDefinition(name, replaceName)) {
                    return null;
                }
                return replaceName;
            } else {
                // Bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=381032 // PS: fixed by Thomas in 2013, still required?
                // if (context.selectedQualifiedName.isFullyQualified()) {
                // buildFullyQualifiedReplaceName(replaceName, names);
                // return replaceName;
                // }
            }
        }
        return null;
    }

    protected void removeUnqualifiedUsingDirective(IASTName name) {}

    private static ICPPASTNameSpecifier[] getNamesOf(IASTName name) {
        ICPPASTNameSpecifier[] names = null;
        if (name instanceof ICPPASTQualifiedName) {
            names = ((ICPPASTQualifiedName) name).getAllSegments(); // this requires more work, because of decltype()
        } else {
            names = new ICPPASTNameSpecifier[] { (ICPPASTNameSpecifier) name.getLastName() };
        }
        return names;
    }

    protected static boolean isExtractCandidate(IASTName name) {
        return NSSelectionHelper.isSelectionCandidate(name) || NSNodeHelper.hasAncestor(name, ICPPASTUsingDirective.class);
    }

    protected abstract ICPPASTNameSpecifier searchNamesFor(IASTName name, ICPPASTNameSpecifier[] names);

    protected abstract boolean isReplaceCandidate(ICPPASTNameSpecifier foundName, IASTName name, ICPPASTNameSpecifier[] names);

    protected boolean isTemplateReplaceCandidate(ICPPASTNameSpecifier foundName, ICPPASTNameSpecifier iastName) {
        return iastName instanceof ICPPASTTemplateId && !(foundName instanceof ICPPASTTemplateId);
    }

    protected abstract ICPPASTQualifiedName buildReplacementTemplate(IASTName iastName);

    protected abstract boolean isNameFound(ICPPASTNameSpecifier foundName, ICPPASTNameSpecifier iastName);

    protected boolean isUnqualifiedDefinition(IASTName name, ICPPASTQualifiedName replaceName) {
        if (isFunctionDefinition(name) || name.getParent() instanceof ICPPASTCompositeTypeSpecifier) {
            return (replaceName.getQualifier().length <= 0);
        }
        return false;
    }

    protected static boolean isFunctionDefinition(IASTName name) {
        return name.getParent().getParent() instanceof ICPPASTFunctionDefinition;
    }

    protected void buildFullyQualifiedReplaceName(ICPPASTQualifiedName replaceName, IASTName[] names) {}

}
