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

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;

import com.cevelop.namespactor.refactoring.eu.EURefactoringContext;
import com.cevelop.namespactor.refactoring.eu.EUTemplateIdFactory;


/**
 * @author Jules Weder
 */
public class EUDirTemplateIdFactory extends EUTemplateIdFactory {

    public EUDirTemplateIdFactory(ICPPASTTemplateId templateId, EURefactoringContext context) {
        super(templateId, context);
    }
}
