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
package com.cevelop.namespactor.refactoring.eudir;

import org.eclipse.cdt.core.dom.ast.ASTNodeFactoryFactory;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDirective;

import com.cevelop.namespactor.refactoring.eu.EURefactoringContext;
import com.cevelop.namespactor.refactoring.eu.EUReplaceVisitor;
import com.cevelop.namespactor.refactoring.eu.EUTemplateIdFactory;


/**
 * @author Jules Weder
 */
public class EUDirReplaceVisitor extends EUReplaceVisitor {

    public EUDirReplaceVisitor(EURefactoringContext context) {
        this.context = context;
    }

    @Override
    protected void removeUnqualifiedUsingDirective(IASTName name) {
        if (name instanceof IASTName && name.getParent() instanceof ICPPASTUsingDirective) {
            IASTName replacementName = buildReplacementName(name);
            if (replacementName != null && replacementName.getLastName() == null) {
                removeUselessUsingDirective(name);
            }
        }
    }

    @Override
    protected ICPPASTNameSpecifier searchNamesFor(IASTName name, ICPPASTNameSpecifier[] names) {
        if (name == null) {
            return null;
        }
        for (ICPPASTNameSpecifier iastName : names) {
            if (iastName.resolveBinding().equals(name.resolveBinding())) {
                return iastName;
            }
        }
        return null;
    }

    @Override
    protected boolean isReplaceCandidate(ICPPASTNameSpecifier foundName, IASTName name, ICPPASTNameSpecifier[] names) {
        return foundName != null;
    }

    @Override
    protected ICPPASTQualifiedName buildReplacementTemplate(IASTName iastName) {
        ICPPASTQualifiedName replaceName;
        EUTemplateIdFactory templateBuilder = new EUDirTemplateIdFactory((ICPPASTTemplateId) iastName, context);
        IASTName buildTemplate = templateBuilder.buildTemplate();
        if (buildTemplate instanceof ICPPASTQualifiedName) {
            return (ICPPASTQualifiedName) buildTemplate;
        }
        replaceName = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newQualifiedName((ICPPASTName) buildTemplate);
        return replaceName;
    }

    @Override
    protected boolean isNameFound(ICPPASTNameSpecifier foundName, ICPPASTNameSpecifier iastName) {
        return iastName.equals(foundName);
    }
}
