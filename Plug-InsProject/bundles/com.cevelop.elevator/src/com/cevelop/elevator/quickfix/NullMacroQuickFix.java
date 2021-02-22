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

import org.eclipse.cdt.codan.core.cxx.Activator;
import org.eclipse.cdt.core.dom.ast.ASTNodeFactoryFactory;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;


/**
 * Elevates NULL macro calls with nullptr keyword.
 */
public class NullMacroQuickFix extends ElevatorQuickFix {

    private static final char[] NULLPTR_KEYWORD = "nullptr".toCharArray();

    private static final int AST_STYLE = ITranslationUnit.AST_SKIP_INDEXED_HEADERS | ITranslationUnit.AST_PARSE_INACTIVE_CODE;

    private IASTTranslationUnit ast;
    private IMarker             marker;

    @Override
    public String getLabel() {
        return "Replace with nullptr keyword";
    }

    @Override
    public void modifyAST(final IIndex index, final IMarker marker) {
        try {
            this.marker = marker;
            this.ast = getTranslationUnitViaWorkspace(marker).getAST(index, AST_STYLE);
            transformNullMacro();
        } catch (CoreException e) {
            Activator.log(e);
        }
    }

    private void transformNullMacro() throws CoreException {
        ICPPNodeFactory nodeFactory = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();
        IASTNode nullMacro = getASTNodeInMacroOfMarker(marker);
        IASTName nullptrNode = nodeFactory.newName(NULLPTR_KEYWORD);
        performChange(nullMacro, nullptrNode, ast);
        marker.delete();
    }

    private IASTNode getASTNodeInMacroOfMarker(IMarker marker) {
        int markerOffset = marker.getAttribute(IMarker.CHAR_START, -1);
        int markerLength = marker.getAttribute(IMarker.CHAR_END, -1) - markerOffset;
        return ast.getNodeSelector(null).findFirstContainedNodeInExpansion(markerOffset, markerLength);
    }
}
