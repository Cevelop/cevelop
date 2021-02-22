/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.dependency;

import org.eclipse.cdt.core.index.IIndexInclude;


public interface IncludePath {

    public IIndexInclude getLastInclude();

    public IIndexInclude getFirstInclude();

    public void clear();
}
