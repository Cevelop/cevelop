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
package com.cevelop.namespactor.astutil;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamedTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPNodeFactory;
import org.eclipse.core.runtime.Assert;


/**
 * @author Ueli Kunz, Jules Weder
 */
@SuppressWarnings("restriction")
public class ASTNodeFactory extends CPPNodeFactory {

    private static final ASTNodeFactory DEFAULT_INSTANCE = new ASTNodeFactory();

    public static ASTNodeFactory getDefault() {
        return DEFAULT_INSTANCE;
    }

    public ICPPASTQualifiedName newQualifiedNameNode(String[] names) {
        Assert.isTrue(names.length > 0);
        ICPPASTQualifiedName qname = newQualifiedName(null);
        for (String name : names) {
            qname.addName(newName(name.toCharArray()));
        }
        return qname;
    }

    @Override
    public ICPPASTNamedTypeSpecifier newNamedTypeSpecifier(IASTName name) {
        return new CPPASTNamedTypeSpecifier(name);
    }
}
