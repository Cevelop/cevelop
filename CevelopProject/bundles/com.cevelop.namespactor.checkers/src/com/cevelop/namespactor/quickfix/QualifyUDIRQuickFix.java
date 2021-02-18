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
package com.cevelop.namespactor.quickfix;

import com.cevelop.namespactor.handlers.QUNRefactoringMenuHandler;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.WizardRefactoringStarterMenuHandler;


/**
 * @author kunz@ideadapt.net
 */
public class QualifyUDIRQuickFix extends AbstractMarkerResolution {

    public static final String LABEL = "com.cevelop.namespactor.qun";

    @Override
    public String getLabel() {
        return "Convert typedef to alias";
    }

    @Override
    protected WizardRefactoringStarterMenuHandler<?, ?> getRefactoringHandler() {
        return new QUNRefactoringMenuHandler();
    }
}
