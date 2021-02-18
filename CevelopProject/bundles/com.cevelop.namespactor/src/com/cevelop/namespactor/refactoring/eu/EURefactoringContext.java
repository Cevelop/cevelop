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

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;


/**
 * @author Jules Weder
 */
public class EURefactoringContext {

    public ICPPASTQualifiedName selectedQualifiedName;
    public IASTName             selectedLastName;
    public ICPPASTQualifiedName qualifiedUsingName;
    public IASTNode             firstNameToReplace;
    public IASTName             startingNamespaceName;// may be this needs to become a ICPPASTNameSpecifier
    public IASTName             startingTypeName;     // may be this needs to become a ICPPASTNameSpecifier

    public HashMap<IASTNode, IASTName> nodesToReplace = new HashMap<>();
    public ArrayList<IASTNode>         nodesToRemove  = new ArrayList<>();

}
