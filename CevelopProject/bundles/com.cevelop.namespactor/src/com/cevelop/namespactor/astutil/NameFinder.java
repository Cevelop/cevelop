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

import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;


/**
 * @author kunz@ideadapt.net
 */
public class NameFinder extends ASTVisitor {

    private List<IBinding> nameBindings = null;

    private NameFinder(List<IBinding> nameBindings) {
        shouldVisitNames = true;
        this.nameBindings = nameBindings;
    }

    @Override
    public int visit(IASTName name) {
        nameBindings.add(name.resolveBinding());
        shouldVisitNames = false;
        return PROCESS_SKIP;
    }

    public static void findNameBindingOf(IASTDeclaration decl, List<IBinding> nameBindings) {
        IASTNode root = decl;
        if (decl instanceof ICPPASTFunctionDefinition) {
            root = ((ICPPASTFunctionDefinition) decl).getDeclarator();
        } else if (decl instanceof ICPPASTTemplateDeclaration) {
            root = ((ICPPASTTemplateDeclaration) decl).getDeclaration();
        }
        root.accept(new NameFinder(nameBindings));
    }
}
