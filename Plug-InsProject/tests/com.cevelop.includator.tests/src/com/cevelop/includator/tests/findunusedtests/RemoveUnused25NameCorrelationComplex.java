/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.findunusedtests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.findunusedincludes.FindUnusedIncludesAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class RemoveUnused25NameCorrelationComplex extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new FindUnusedIncludesAlgorithm());
        Assert.assertEquals((Object) 1, (Object) suggestions.size());
        assertSuggestion(suggestions.get(0), "A.cpp", "The include statement '#include \"B.h\"' is unneeded. Covered through: A.h", 15, 14);
    }
}
