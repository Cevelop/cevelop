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

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNamespace;

import com.cevelop.namespactor.astutil.ASTNodeFactory;
import com.cevelop.namespactor.astutil.NSNameHelper;
import com.cevelop.namespactor.refactoring.TemplateIdFactory;


/**
 * @author Jules Weder
 */
@SuppressWarnings("restriction")
public abstract class EUTemplateIdFactory extends TemplateIdFactory {

    protected IASTName selectedName;
    protected IASTName selectedType;

    public EUTemplateIdFactory(ICPPASTTemplateId templateId, EURefactoringContext context) {
        super(templateId);
        this.selectedName = context.startingNamespaceName;
        this.selectedType = context.startingTypeName;
    }

    @Override
    public ICPPASTQualifiedName buildTemplate() {
        IASTName newTemplateId = templateId.copy(CopyStyle.withLocations);
        newTemplateId.accept(new ASTVisitor() {

            {
                shouldVisitTypeIds = true;
            }

            @Override
            public int visit(IASTTypeId typeId) {
                IASTDeclSpecifier vDeclSpecifier = typeId.getDeclSpecifier();
                IASTDeclSpecifier newDeclSpec = null;

                if (vDeclSpecifier instanceof ICPPASTNamedTypeSpecifier) {
                    newDeclSpec = createNamedDeclSpec(vDeclSpecifier);
                } else if (vDeclSpecifier instanceof ICPPASTSimpleDeclSpecifier) {
                    newDeclSpec = createSimpleDeclSpec(vDeclSpecifier);
                }
                if (newDeclSpec != null) {
                    typeId.setDeclSpecifier(newDeclSpec);
                }
                return super.visit(typeId);
            }

        });
        if (!(newTemplateId instanceof ICPPASTQualifiedName)) {
            ICPPASTQualifiedName wrapper = ASTNodeFactory.getDefault().newQualifiedName(null);
            wrapper.addName(newTemplateId);
            newTemplateId = wrapper;
        }
        return (ICPPASTQualifiedName) newTemplateId;
    }

    protected void buildReplaceName(ICPPASTQualifiedName replaceName, ICPPASTNameSpecifier[] names) {
        boolean start = false;
        for (ICPPASTNameSpecifier iastName : names) {
            IBinding binding = ((IASTName) iastName.getOriginalNode()).resolveBinding();
            if (start) {
                if (binding instanceof ICPPNamespace || binding instanceof ICPPClassType) {
                    NSNameHelper.addNameOrNameSpecifierWithStyle(replaceName, iastName, CopyStyle.withLocations);
                }
            }
            if (binding.equals(selectedName.resolveBinding())) {
                start = true;
            }
        }
    }

    protected void precedeWithQualifiers(ICPPASTQualifiedName replaceName, ICPPASTNameSpecifier[] names, IASTName templateName) {

    }

    @Override
    protected ICPPASTNamedTypeSpecifier createNamedDeclSpec(IASTDeclSpecifier vDeclSpecifier) {
        ICPPASTNamedTypeSpecifier newDeclSpec = (ICPPASTNamedTypeSpecifier) vDeclSpecifier;
        IASTName specName = newDeclSpec.getName();
        ICPPASTQualifiedName replaceName = ASTNodeFactory.getDefault().newQualifiedName(null);

        if (specName instanceof ICPPASTQualifiedName) {
            ICPPASTNameSpecifier[] names = ((ICPPASTQualifiedName) specName).getAllSegments();
            precedeWithQualifiers(replaceName, names, specName.getLastName());
            buildReplaceName(replaceName, names);
            newDeclSpec.setName(replaceName);
        } else {
            newDeclSpec.setName(specName);
        }

        return newDeclSpec;
    }

    @Override
    protected ICPPASTQualifiedName modifyTemplateId(ICPPASTTemplateId vTemplId) {
        ICPPASTQualifiedName replaceName = ASTNodeFactory.getDefault().newQualifiedName(null);

        ICPPASTNameSpecifier[] names = null;
        if (vTemplId.getOriginalNode().getParent() instanceof ICPPASTQualifiedName) {
            names = ((ICPPASTQualifiedName) vTemplId.getOriginalNode().getParent()).getAllSegments();
        } else {
            names = new ICPPASTNameSpecifier[] { (ICPPASTNameSpecifier) ((IASTName) vTemplId.getOriginalNode().getParent()).getLastName() };
        }
        precedeWithQualifiers(replaceName, names, vTemplId.getTemplateName());
        buildReplaceName(replaceName, names);
        replaceName.setLastName((ICPPASTName) vTemplId.getTemplateName());
        return replaceName;
    }

}
