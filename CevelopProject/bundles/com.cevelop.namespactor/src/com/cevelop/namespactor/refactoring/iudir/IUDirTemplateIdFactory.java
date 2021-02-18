/******************************************************************************
 * Copyright (c) 2012-2015 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ueli Kunz <kunz@ideadapt.net>, Jules Weder <julesweder@gmail.com> - initial API and implementation
 * Peter Sommerlad <peter.sommerlad@hsr.ch> - modernization
 ******************************************************************************/
package com.cevelop.namespactor.refactoring.iudir;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNamespace;
import org.eclipse.cdt.core.index.IIndex;

import com.cevelop.namespactor.astutil.NSNameHelper;
import com.cevelop.namespactor.refactoring.TemplateIdFactory;
import com.cevelop.namespactor.refactoring.iu.InlineRefactoringContext;
import com.cevelop.namespactor.refactoring.iu.NamespaceInlineContext;


/**
 * @author kunz@ideadapt.net
 */
@SuppressWarnings("restriction")
public class IUDirTemplateIdFactory extends TemplateIdFactory {

    private final NamespaceInlineContext enclosingNSContext;
    //private Set<ICPPASTTemplateId> templateIdsToIgnore = null;

    public IUDirTemplateIdFactory(ICPPASTTemplateId templateId, InlineRefactoringContext context) {
        super(templateId);
        this.enclosingNSContext = context.enclosingNSContext;
        //		this.templateIdsToIgnore = context.templateIdsToIgnore;
    }

    @Override
    public int visit(IASTName name) {
        //		if (name instanceof ICPPASTTemplateId) {
        //			templateIdsToIgnore.add((ICPPASTTemplateId) name);
        //		}
        return super.visit(name);
    }

    @Override
    protected ICPPASTNamedTypeSpecifier createNamedDeclSpec(IASTDeclSpecifier vDeclSpecifier) {
        ICPPASTNamedTypeSpecifier newDeclSpec = (ICPPASTNamedTypeSpecifier) vDeclSpecifier;
        IASTName specName = newDeclSpec.getName();

        // qualify the name of the specifier if it has nothing to do with a template id
        IASTName originalNode = (IASTName) specName.getOriginalNode();
        IBinding owner = originalNode.resolveBinding().getOwner();
        if (!isOrContainsTemplateId(specName) && requiresQualification(originalNode, owner)) {//(!isOrContainsTemplateId(specName)) {
            IASTName qnameNode = specName;
            if (!NSNameHelper.isNodeQualifiedWithName(specName.getLastName(), enclosingNSContext.usingName.getLastName())) {
                qnameNode = NSNameHelper.prefixNameWith(enclosingNSContext.usingName, specName);
            }
            newDeclSpec.setName(qnameNode);//.copy());
        }
        return newDeclSpec;
    }

    @Override
    protected ICPPASTQualifiedName modifyTemplateId(ICPPASTTemplateId vTemplId) {
        ICPPASTQualifiedName qnameNode;
        ICPPASTTemplateId originalTemplId = (ICPPASTTemplateId) vTemplId.getOriginalNode();
        IASTName templateName = originalTemplId.getTemplateName();
        IBinding owner = templateName.resolveBinding().getOwner();

        if (requiresQualification(originalTemplId, owner)) {
            qnameNode = NSNameHelper.prefixNameWith(enclosingNSContext.usingName, templateName);
            qnameNode = NSNameHelper.copyQualifers(qnameNode);
            //} else if (vTemplId.getOriginalNode().getParent() instanceof ICPPASTQualifiedName) {
            //qnameNode = NSNameHelper.copyQualifers((ICPPASTQualifiedName) vTemplId.getOriginalNode().getParent());
        } else {
            qnameNode = factory.newQualifiedName(null);
        }
        qnameNode.addName(vTemplId.getTemplateName());

        return qnameNode;
    }

    private boolean requiresQualification(IASTName name, IBinding owner) {
        if (owner instanceof ICPPNamespace) {
            IIndex index = name.getTranslationUnit().getIndex();
            boolean isChildOfEnclosingNamespace = index.adaptBinding(owner).equals(index.adaptBinding(enclosingNSContext.namespaceDefBinding)); // since this should works with nested using directives
            while (!isChildOfEnclosingNamespace && (owner != null && owner instanceof ICPPNamespace) && ((ICPPNamespace) owner).isInline()) {
                owner = owner.getOwner();
                isChildOfEnclosingNamespace = index.adaptBinding(owner).equals(index.adaptBinding(enclosingNSContext.namespaceDefBinding));
            }
            if (isChildOfEnclosingNamespace) {
                IASTNode parent = name.getParent();
                if (parent instanceof ICPPASTQualifiedName) {
                    // doesn't work with template member function definitions
                    return ((ICPPASTQualifiedName) parent).getQualifier()[0] == name;
                }
                return true;

            }
        }

        return false;
    }
}
