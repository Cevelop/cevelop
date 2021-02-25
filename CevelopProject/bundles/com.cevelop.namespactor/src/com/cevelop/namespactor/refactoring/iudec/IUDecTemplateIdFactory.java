/******************************************************************************
 * Copyright (c) 2013-2015 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ueli Kunz <kunz@ideadapt.net>, Jules Weder <julesweder@gmail.com> - initial API and implementation
 * peter.sommerlad@hsr.ch - adaption of hybrid approach, modernization
 ******************************************************************************/
package com.cevelop.namespactor.refactoring.iudec;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTName;
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
 * @author peter.sommerlad@hsr.ch
 */
public class IUDecTemplateIdFactory extends TemplateIdFactory {

    private final NamespaceInlineContext enclosingNSContext;
    //	private Set<ICPPASTTemplateId> templateIdsToIgnore = null;

    public IUDecTemplateIdFactory(ICPPASTTemplateId templateId, InlineRefactoringContext context) {
        super(templateId);
        this.enclosingNSContext = context.enclosingNSContext;
        //		this.templateIdsToIgnore = context.templateIdsToIgnore;
    }

    @Override
    public int visit(IASTName name) {
        if (name instanceof ICPPASTTemplateId) {
            //			templateIdsToIgnore.add((ICPPASTTemplateId) name);
        }
        return super.visit(name);
    }

    @Override
    protected ICPPASTNamedTypeSpecifier createNamedDeclSpec(IASTDeclSpecifier vDeclSpecifier) {
        ICPPASTNamedTypeSpecifier newDeclSpec = (ICPPASTNamedTypeSpecifier) vDeclSpecifier;
        IASTName specName = newDeclSpec.getName();

        // qualify the name of the specifier if it has nothing to do with a template id
        if (!isOrContainsTemplateId(specName)) {
            IASTName qnameNode = specName;
            if (!NSNameHelper.isNodeQualifiedWithName(specName.getLastName(), enclosingNSContext.namespaceDefNode.getName())) {
                qnameNode = NSNameHelper.prefixNameWith(enclosingNSContext.usingName, specName);
            }
            newDeclSpec.setName(qnameNode);
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
        } else {
            qnameNode = factory.newQualifiedName(null);
        }
        qnameNode.addName(vTemplId.getTemplateName());
        return qnameNode;
    }

    private boolean requiresQualification(ICPPASTTemplateId templId) {
        templId = (ICPPASTTemplateId) templId.getOriginalNode();
        IBinding templateNameBinding = templId.getTemplateName().resolveBinding();
        IBinding owner = templateNameBinding.getOwner();
        if (owner instanceof ICPPNamespace) {
            IIndex index = templId.getTranslationUnit().getIndex();
            boolean isChildOfEnclosingNamespace = index.adaptBinding(owner).equals(index.adaptBinding(enclosingNSContext.namespaceDefBinding)); // enclosingContext muss abgef���llt sein
            boolean isNotQualified = !(templId.getParent() instanceof ICPPASTQualifiedName);

            return isChildOfEnclosingNamespace && isNotQualified;
        }

        return false;
    }

}
