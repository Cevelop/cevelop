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
 * Peter Sommerlad (peter.sommerlad@hsr.ch) - modernization and generalization
 ******************************************************************************/
package com.cevelop.namespactor.refactoring.eudec;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNamespace;

import com.cevelop.namespactor.astutil.NSNameHelper;
import com.cevelop.namespactor.refactoring.eu.EURefactoringContext;
import com.cevelop.namespactor.refactoring.eu.EUTemplateIdFactory;


/**
 * @author Jules Weder and Peter Sommerlad
 */
public class EUDecTemplateIdFactory extends EUTemplateIdFactory {

    public EUDecTemplateIdFactory(ICPPASTTemplateId templateId, EURefactoringContext context) {
        super(templateId, context);
    }

    @Override
    protected void buildReplaceName(ICPPASTQualifiedName replaceName, ICPPASTNameSpecifier[] names) {
        boolean start = false;
        for (ICPPASTNameSpecifier iastName : names) {
            IBinding binding = ((ICPPASTNameSpecifier) iastName.getOriginalNode()).resolveBinding();
            if (start) {
                if (binding instanceof ICPPNamespace || binding instanceof ICPPClassType) {
                    NSNameHelper.addNameOrNameSpecifierWithStyle(replaceName, iastName, CopyStyle.withLocations);
                }
                if (iastName instanceof ICPPASTTemplateId) {
                    replaceName.setLastName(((ICPPASTTemplateId) iastName).copy(CopyStyle.withLocations));
                    break;
                }
            }
            if (binding.equals(selectedName.resolveBinding())) {
                start = true;
            }
        }
    }

    @Override
    protected void precedeWithQualifiers(ICPPASTQualifiedName replaceName, ICPPASTNameSpecifier[] names, IASTName templateName) {
        IASTName type = selectedType;
        if (selectedType instanceof ICPPASTTemplateId) {
            type = ((ICPPASTTemplateId) selectedType).getTemplateName();
            if (templateName instanceof ICPPASTTemplateId) {
                templateName = ((ICPPASTTemplateId) templateName).getTemplateName();
            }
        }
        if (!((IASTName) type.getOriginalNode()).resolveBinding().equals(((IASTName) templateName.getOriginalNode()).resolveBinding())) {
            for (ICPPASTNameSpecifier iastName : names) {
                if (iastName instanceof ICPPASTTemplateId) {
                    iastName = (ICPPASTNameSpecifier) ((ICPPASTTemplateId) iastName).getTemplateName();
                }
                if (((IASTName) iastName.getOriginalNode()).resolveBinding().equals(((IASTName) templateName.getOriginalNode()).resolveBinding())) {
                    break;
                }
                NSNameHelper.addNameOrNameSpecifierWithStyle(replaceName, iastName, CopyStyle.withLocations);
            }
        }
    }
}
