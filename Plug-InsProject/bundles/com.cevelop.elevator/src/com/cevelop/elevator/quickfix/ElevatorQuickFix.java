/******************************************************************************
 * Copyright (c) 2015 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Thomas Corbat - initial API and implementation
 ******************************************************************************/
package com.cevelop.elevator.quickfix;

import org.eclipse.cdt.codan.ui.AbstractAstRewriteQuickFix;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;


public abstract class ElevatorQuickFix extends AbstractAstRewriteQuickFix {

    public ElevatorQuickFix() {
        super();
    }

    protected void performChange(IASTNode target, IASTNode replacement, IASTTranslationUnit ast) throws CoreException {
        ASTRewrite rewrite = ASTRewrite.create(ast);
        rewrite.replace(target, replacement, null);
        Change change = rewrite.rewriteAST();
        change.perform(new NullProgressMonitor());
    }

}
