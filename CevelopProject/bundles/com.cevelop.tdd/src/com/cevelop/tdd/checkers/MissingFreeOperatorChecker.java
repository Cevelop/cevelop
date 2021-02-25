/*******************************************************************************
 * Copyright (c) 2011-2015, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.checkers;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import ch.hsr.ifs.iltis.cpp.core.wrappers.AbstractIndexAstChecker;

import com.cevelop.tdd.checkers.visitors.MissingFreeOperatorProblemVisitor;


public class MissingFreeOperatorChecker extends AbstractIndexAstChecker {

    @Override
    public void processAst(IASTTranslationUnit ast) {
        ast.accept(new MissingFreeOperatorProblemVisitor(this::reportProblem));
    }
}
