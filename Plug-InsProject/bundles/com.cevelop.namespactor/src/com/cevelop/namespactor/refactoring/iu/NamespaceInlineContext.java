/******************************************************************************
 * Copyright (c) 2012-2013 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ueli Kunz <kunz@ideadapt.net>, Jules Weder <julesweder@gmail.com> - initial API and implementation
 * Peter Sommerlad - adapted for hybrid AST-based approach for IUDECRefactoring
 ******************************************************************************/
package com.cevelop.namespactor.refactoring.iu;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.index.IIndexName;


/**
 * @author kunz@ideadapt.net
 */
public class NamespaceInlineContext {

    public IASTName                      usingName           = null;
    public ICPPASTNamespaceDefinition    namespaceDefNode    = null;
    public IBinding                      namespaceDefBinding = null;
    public IIndexName                    namespaceDefName    = null;
    public ICPPASTCompositeTypeSpecifier classDefNode        = null;
}
