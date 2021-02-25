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
package com.cevelop.namespactor.refactoring.eudec;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBinding;

import com.cevelop.namespactor.astutil.NSNameHelper;
import com.cevelop.namespactor.refactoring.eu.EURefactoringContext;
import com.cevelop.namespactor.refactoring.eu.EUReplaceVisitor;
import com.cevelop.namespactor.refactoring.eu.EUTemplateIdFactory;


/**
 * @author Jules Weder
 */
public class EUDecReplaceVisitor extends EUReplaceVisitor {

    public EUDecReplaceVisitor(EURefactoringContext context) {
        this.context = context;
    }

    @Override
    protected ICPPASTNameSpecifier searchNamesFor(IASTName name, ICPPASTNameSpecifier[] names) {
        if (name == null) {
            return null;
        }
        for (ICPPASTNameSpecifier iastName : names) {
            if (name instanceof ICPPASTTemplateId && iastName instanceof ICPPASTTemplateId) {
                if (NSNameHelper.isSameNameInTemplateId((ICPPASTTemplateId) name, (ICPPASTTemplateId) iastName)) {
                    return iastName;
                }
            }
            if (iastName.resolveBinding().equals(name.resolveBinding())) {
                return iastName;
            }
        }
        return null;
    }

    @Override
    protected boolean isReplaceCandidate(ICPPASTNameSpecifier foundName, IASTName name, ICPPASTNameSpecifier[] names) {
        if (context.firstNameToReplace != null) {
            IBinding targetBinding = ((IASTName) context.firstNameToReplace).resolveBinding();
            IBinding nameBinding = name.resolveBinding();
            if (targetBinding instanceof ICPPBinding && nameBinding instanceof ICPPBinding) {
                try {
                    String[] targetQualifiedNames = ((ICPPBinding) targetBinding).getQualifiedName();
                    String[] nameQualifiedNames = ((ICPPBinding) nameBinding).getQualifiedName();
                    if (nameQualifiedNames.length >= targetQualifiedNames.length) {
                        for (int i = 0; i < targetQualifiedNames.length; i++) {
                            if (!targetQualifiedNames[i].equals(nameQualifiedNames[i])) {
                                return false;
                            }
                        }
                        return true;
                    }
                } catch (DOMException e) {
                    e.printStackTrace();
                }
            }
        }
        return foundName != null && isSameName(name.getLastName(), names);
    }

    private boolean isSameName(IASTName name, ICPPASTNameSpecifier[] names) {
        if (context.startingTypeName != null) {
            for (ICPPASTNameSpecifier iastName : names) {
                if (context.startingTypeName.resolveBinding().equals(iastName.resolveBinding())) {
                    return true;
                }
                if (name instanceof ICPPASTTemplateId && iastName instanceof ICPPASTTemplateId) {
                    return NSNameHelper.isSameNameInTemplateId((ICPPASTTemplateId) name, (ICPPASTTemplateId) iastName);
                }
            }
            return false;
        }
        IBinding nameBinding = name.resolveBinding();
        IBinding selectionBinding = context.selectedLastName.resolveBinding();

        return nameBinding.getOwner().equals(selectionBinding.getOwner()) && nameBinding.getName().equals(selectionBinding.getName());
    }

    @Override
    protected ICPPASTQualifiedName buildReplacementTemplate(IASTName iastName) {
        ICPPASTQualifiedName replaceName;
        EUTemplateIdFactory templateBuilder = new EUDecTemplateIdFactory((ICPPASTTemplateId) iastName, context);
        replaceName = templateBuilder.buildTemplate();
        return replaceName;
    }

    @Override
    protected boolean isNameFound(ICPPASTNameSpecifier foundName, ICPPASTNameSpecifier iastName) {
        if (foundName instanceof ICPPASTTemplateId && iastName instanceof ICPPASTTemplateId) {
            return NSNameHelper.isSameNameInTemplateId((ICPPASTTemplateId) foundName, (ICPPASTTemplateId) iastName);
        } else {
            return iastName.equals(foundName);
        }
    }

    @Override
    protected void buildFullyQualifiedReplaceName(ICPPASTQualifiedName replaceName, IASTName[] names) {
        for (IASTName iastName : names) {
            replaceName.addName(iastName.copy());
        }
    }
}
