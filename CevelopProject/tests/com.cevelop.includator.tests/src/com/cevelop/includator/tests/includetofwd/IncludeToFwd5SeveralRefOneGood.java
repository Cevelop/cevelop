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


public class IncludeToFwd5SeveralRefOneGood extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new ReplaceIncludesWithFwdAlgorithm());

        Assert.assertEquals((Object) 0, (Object) suggestions.size());
    }
}
