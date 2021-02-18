/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.ui.tests.quickfixes;

import java.util.function.Supplier;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.tdd.helpers.IdHelper.ProblemId;
import com.cevelop.tdd.quickfixes.create.variable.local.CreateLocalVariableQuickfix;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;
import ch.hsr.ifs.iltis.cpp.core.codan.marker.InfoProblemMarkerResolutionGenerator;


public class CreateLocalVariableTest extends AbstractQuickFixTest<InfoProblemMarkerResolutionGenerator> {

    @Override
    protected IProblemId<?> getProblemId() {
        return ProblemId.MISSING_LOCAL_VARIABLE;
    }

    @Override
    protected boolean solutionSelectionPredicate(IMarkerResolution quickfix) {
        return quickfix instanceof CreateLocalVariableQuickfix;
    }

    @Override
    protected Supplier<InfoProblemMarkerResolutionGenerator> getMarkerResolutionGeneratorConstructor() {
        return InfoProblemMarkerResolutionGenerator::new;
    }
}
