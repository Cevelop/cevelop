/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.checkers.visitors;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;


public abstract class AbstractResolutionProblemVisitor extends ASTVisitor {

    {
        shouldVisitNames = true;
    }

    @Override
    public int visit(IASTName name) {
        IBinding binding = name.resolveBinding();
        if (binding instanceof IProblemBinding) {
            reactOnProblemBinding((IProblemBinding) binding, name);
        }
        return PROCESS_CONTINUE;
    }

    protected abstract void reactOnProblemBinding(IProblemBinding problemBinding, IASTName name);

}
