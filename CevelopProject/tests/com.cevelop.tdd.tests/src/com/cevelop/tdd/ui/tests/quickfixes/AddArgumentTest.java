/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.ui.tests.quickfixes;

import java.util.Properties;
import java.util.function.Supplier;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.tdd.helpers.IdHelper.ProblemId;
import com.cevelop.tdd.quickfixes.argument.ArgumentMismatchQuickfix;
import com.cevelop.tdd.quickfixes.argument.ArgumentMismatchQuickfixGenerator;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public class AddArgumentTest extends AbstractQuickFixTest<ArgumentMismatchQuickfixGenerator> {

    protected int candidate;

    @Override
    protected void configureTest(Properties properties) {
        candidate = Integer.parseInt(properties.getProperty("candidate", "0"));
        super.configureTest(properties);
    }

    @Override
    protected IProblemId<?> getProblemId() {
        return ProblemId.ARGUMENT_MISMATCH;
    }

    @Override
    protected boolean solutionSelectionPredicate(IMarkerResolution quickfix) {
        return quickfix instanceof ArgumentMismatchQuickfix && ((ArgumentMismatchQuickfix) quickfix).getInfo().candidateNr == candidate;
    }

    @Override
    protected Supplier<ArgumentMismatchQuickfixGenerator> getMarkerResolutionGeneratorConstructor() {
        return ArgumentMismatchQuickfixGenerator::new;
    }
}
