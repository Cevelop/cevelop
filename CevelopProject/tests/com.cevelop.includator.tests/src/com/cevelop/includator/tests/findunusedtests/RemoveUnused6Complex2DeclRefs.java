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


public class RemoveUnused6Complex2DeclRefs extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new FindUnusedIncludesAlgorithm());

        Assert.assertEquals((Object) 3, (Object) suggestions.size());
        Assert.assertEquals("The include statement '#include \"E.h\"' is unneeded. Covered through: H.h", suggestions.get(0).getDescription());
        Assert.assertEquals("The include statement '#include \"F.h\"' is unneeded. Covered through: H.h", suggestions.get(1).getDescription());
        Assert.assertEquals("The include statement '#include \"G.h\"' is unneeded. Covered through: H.h", suggestions.get(2).getDescription());
    }
}
