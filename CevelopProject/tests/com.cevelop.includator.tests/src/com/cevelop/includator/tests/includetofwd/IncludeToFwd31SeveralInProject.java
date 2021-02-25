/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.includetofwd;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.includestofwd.ReplaceIncludesWithFwdAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class IncludeToFwd31SeveralInProject extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(wrapAsProjectAlgorithm(new ReplaceIncludesWithFwdAlgorithm()));

        Assert.assertEquals((Object) 2, (Object) suggestions.size());
        assertSuggestion(suggestions.get(0), "X.cpp", "#include \"A.h\" can be replaced with forward declaration of 'class A'.", 0, 14);
        assertSuggestion(suggestions.get(1), "main.cpp", "#include \"A.h\" can be replaced with forward declaration of 'class A'.", 0, 14);
    }
}
