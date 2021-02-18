/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.checkers;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.tdd.checkers.visitors.MissingLocalVariableProblemVisitor;

import ch.hsr.ifs.iltis.cpp.core.wrappers.AbstractIndexAstChecker;


public class MissingLocalVariableChecker extends AbstractIndexAstChecker {

    @Override
    public void processAst(IASTTranslationUnit ast) {
        ast.accept(new MissingLocalVariableProblemVisitor(this::reportProblem, false));
    }
}
