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

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.WizardRefactoringStarterMenuHandler;

import com.cevelop.namespactor.handlers.IURefactoringMenuHandler;


/**
 * @author kunz@ideadapt.net
 */
public class InlineUsingQuickFix extends AbstractMarkerResolution {

    @Override
    public String getLabel() {
        return "Inline using";
    }

    @Override
    protected WizardRefactoringStarterMenuHandler<?, ?> getRefactoringHandler() {
        return new IURefactoringMenuHandler();
    }
}
