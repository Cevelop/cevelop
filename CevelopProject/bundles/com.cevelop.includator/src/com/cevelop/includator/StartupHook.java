/*******************************************************************************
 * Copyright (c) 2017 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator;

import org.eclipse.ui.IStartup;


/**
 * This class is used to ensure Includator is loaded when starting Eclipse,
 * otherwise the menu entries will be disabled.
 *
 * @author Mirko Stocker
 *
 */
public class StartupHook implements IStartup {

    @Override
    public void earlyStartup() {}
}
