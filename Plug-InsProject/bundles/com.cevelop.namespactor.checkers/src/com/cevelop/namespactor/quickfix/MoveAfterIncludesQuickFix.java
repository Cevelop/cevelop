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

import org.eclipse.cdt.codan.ui.AbstractAstRewriteQuickFix;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;

import com.cevelop.namespactor.astutil.NSNodeHelper;


/**
 * @author kunz@ideadapt.net
 */
public class MoveAfterIncludesQuickFix extends AbstractAstRewriteQuickFix {

    @Override
    public String getLabel() {
        return "Move using after last #include";
    }

    @Override
    public void modifyAST(IIndex index, IMarker marker) {
        IASTTranslationUnit ast;
        try {
            ITranslationUnit tu = getTranslationUnitViaEditor(marker);
            ast = tu.getAST(index, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);
        } catch (CoreException e) {
            return;
        }

        applyMove(marker, createMoveChange(marker, ast));
    }

    private void applyMove(IMarker marker, Change moveChange) {
        try {
            moveChange.perform(new NullProgressMonitor());
        } catch (CoreException e) {
            return;
        }
        try {
            marker.delete();
        } catch (CoreException e) {}
    }

    private Change createMoveChange(IMarker marker, IASTTranslationUnit ast) {

        int nodeOffset = marker.getAttribute(IMarker.CHAR_START, -1);
        int length = marker.getAttribute(IMarker.CHAR_END, -1) - nodeOffset;
        IASTNode markedNode = ast.getNodeSelector(null).findNode(nodeOffset, length);
        ASTRewrite r = ASTRewrite.create(ast);

        IASTPreprocessorIncludeStatement[] includes = ast.getIncludeDirectives();
        if (includes.length > 0) {
            IASTNode lastInclude = includes[includes.length - 1];
            r.remove(markedNode, null);
            r.insertBefore(lastInclude.getParent(), NSNodeHelper.getASTSiblingOf(lastInclude, ast), markedNode.copy(), null);
        }

        return r.rewriteAST();
    }

}
