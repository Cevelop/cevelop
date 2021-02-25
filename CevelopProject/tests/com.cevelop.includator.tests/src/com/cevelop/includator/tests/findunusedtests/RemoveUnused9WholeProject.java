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


public class RemoveUnused9WholeProject extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(wrapAsProjectAlgorithm(new FindUnusedIncludesAlgorithm()));

        Assert.assertEquals((Object) 2, (Object) suggestions.size());
        Assert.assertEquals("A.cpp", suggestions.get(0).getProjectRelativePath());
        Assert.assertEquals("The include statement '#include \"Unused.h\"' is unneeded. No reference requires include.", suggestions.get(0)
                .getDescription());
        Assert.assertEquals("B.cpp", suggestions.get(1).getProjectRelativePath());
        Assert.assertEquals("The include statement '#include \"Unused.h\"' is unneeded. No reference requires include.", suggestions.get(1)
                .getDescription());
    }
}
