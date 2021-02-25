/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.helpers.visitors;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;
import org.eclipse.cdt.internal.ui.refactoring.NodeContainer;


public class VisibilityLabelFinder extends ASTVisitor {

    private final NodeContainer container;
    private final int           visibility;
    {
        shouldVisitDeclarations = true;
    }

    public VisibilityLabelFinder(int visibility) {
        this.container = new NodeContainer();
        this.visibility = visibility;
    }

    @Override
    public int visit(IASTDeclaration declaration) {
        if (declaration instanceof ICPPASTVisibilityLabel) {
            ICPPASTVisibilityLabel label = (ICPPASTVisibilityLabel) declaration;
            if (label.getVisibility() == visibility) {
                container.add(label);
                return PROCESS_ABORT;
            }
        }
        return PROCESS_CONTINUE;
    }

    public ICPPASTVisibilityLabel getFoundLabel() {
        if (container.size() > 0) {
            return (ICPPASTVisibilityLabel) container.getNodesToWrite().get(0);
        }
        return null;
    }
}
