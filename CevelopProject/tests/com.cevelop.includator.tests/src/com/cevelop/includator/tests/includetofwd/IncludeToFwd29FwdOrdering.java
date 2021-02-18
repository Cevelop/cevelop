/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.includetofwd;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.includestofwd.ReplaceIncludesWithFwdAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class IncludeToFwd29FwdOrdering extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        fail("Not implemented"); //TODO: impl
        List<Suggestion<?>> suggestions = runAlgorithm(new ReplaceIncludesWithFwdAlgorithm());

        Assert.assertEquals((Object) 1, (Object) suggestions.size());
        Suggestion<?> suggestion = suggestions.get(0);
        assertSuggestion(suggestion, "main.cpp", "#include \"foo.h\" can be replaced with forward declarations of 'function foo', 'function foo'.", 0,
                16);
    }
}
