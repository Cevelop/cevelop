/******************************************************************************
 * Copyright (c) 2013 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Peter Sommerlad - adaption and fixes for open cases/rewrites
 * Ueli Kunz <kunz@ideadapt.net>, Jules Weder <julesweder@gmail.com> - initial API and implementation
 ******************************************************************************/
package com.cevelop.namespactor.refactoring.qun;

import java.util.Set;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNamespace;

import com.cevelop.namespactor.Activator;
import com.cevelop.namespactor.astutil.NSNameHelper;
import com.cevelop.namespactor.refactoring.TemplateIdFactory;
import com.cevelop.namespactor.refactoring.iu.InlineRefactoringContext;
import com.cevelop.namespactor.refactoring.iu.NamespaceInlineContext;


public class QUNTemplateIdFactory extends TemplateIdFactory {

    private final NamespaceInlineContext enclosingNSContext;
    private Set<ICPPASTTemplateId>       templateIdsToIgnore = null;

    public QUNTemplateIdFactory(ICPPASTTemplateId templateId, InlineRefactoringContext context) {
        super(templateId);
        this.enclosingNSContext = context.enclosingNSContext;
        //		this.templateIdsToIgnore = context.templateIdsToIgnore;
    }

    @Override
    public int visit(IASTName name) {
        if (name instanceof ICPPASTTemplateId) {
            templateIdsToIgnore.add((ICPPASTTemplateId) name);
        }
        return super.visit(name);
    }

    @Override
    protected ICPPASTNamedTypeSpecifier createNamedDeclSpec(IASTDeclSpecifier vDeclSpecifier) {
        ICPPASTNamedTypeSpecifier newDeclSpec = factory.newNamedTypeSpecifier(null);
        IASTName specName = ((ICPPASTNamedTypeSpecifier) vDeclSpecifier).getName();

        // qualify the name of the specifier if it has nothing to do with a template id
        if (!isOrContainsTemplateId(specName)) {
            IASTName qnameNode = specName;
            if (!NSNameHelper.isNodeQualifiedWithName(specName.getLastName(), enclosingNSContext.namespaceDefName)) {
                qnameNode = NSNameHelper.prefixNameWith(enclosingNSContext.usingName, specName);
            }
            newDeclSpec.setName(qnameNode.copy());
        }
        return newDeclSpec;
    }

    @SuppressWarnings("restriction")
    @Override
    protected ICPPASTQualifiedName modifyTemplateId(ICPPASTTemplateId vTemplId) {
        ICPPASTQualifiedName qnameNode;
        if (requiresQualification(vTemplId)) {
            qnameNode = NSNameHelper.prefixNameWith(enclosingNSContext.usingName, vTemplId.getTemplateName());
            qnameNode = NSNameHelper.copyQualifers(qnameNode);
        } else if (vTemplId.getParent() instanceof ICPPASTQualifiedName) {
            qnameNode = NSNameHelper.copyQualifers((ICPPASTQualifiedName) vTemplId.getParent());
        } else {
            qnameNode = factory.newQualifiedName(null);
        }
        return qnameNode;
    }

    private boolean requiresQualification(ICPPASTTemplateId templId) {
        IBinding templateNameBinding = templId.getTemplateName().resolveBinding();
        String qname = "";

        if (templateNameBinding.getOwner() instanceof ICPPNamespace) {
            try {
                qname = NSNameHelper.buildQualifiedName(((ICPPNamespace) templateNameBinding.getOwner()).getQualifiedName());

                boolean isChildOfEnclosingNamespace = qname.equals(enclosingNSContext.namespaceDefBinding.toString());
                boolean isNotQualified = !(templId.getParent() instanceof ICPPASTQualifiedName);

                return isChildOfEnclosingNamespace && isNotQualified;

            } catch (DOMException e) {
                Activator.log("Exception in requiresQualification().", e);
            }
        }

        return false;
    }
}
