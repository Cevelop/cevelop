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


public class RemoveUnused5Complex extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new FindUnusedIncludesAlgorithm());

        Assert.assertEquals((Object) 5, (Object) suggestions.size());
        Assert.assertEquals("The include statement '#include \"B.h\"' is unneeded. No reference requires include.", suggestions.get(0)
                .getDescription());
        Assert.assertEquals("The include statement '#include \"D.h\"' is unneeded. No reference requires include.", suggestions.get(1)
                .getDescription());
        Assert.assertEquals("The include statement '#include \"F.h\"' is unneeded. Covered through: E.h", suggestions.get(2).getDescription());
        Assert.assertEquals("The include statement '#include \"G.h\"' is unneeded. Covered through: E.h", suggestions.get(3).getDescription());
        Assert.assertEquals("The include statement '#include \"H.h\"' is unneeded. Covered through: E.h", suggestions.get(4).getDescription());
    }
}
