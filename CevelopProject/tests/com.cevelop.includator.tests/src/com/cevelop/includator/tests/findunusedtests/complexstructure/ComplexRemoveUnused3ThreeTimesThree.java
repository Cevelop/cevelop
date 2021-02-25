/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.findunusedtests.complexstructure;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.findunusedincludes.FindUnusedIncludesAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class ComplexRemoveUnused3ThreeTimesThree extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new FindUnusedIncludesAlgorithm());

        Assert.assertEquals((Object) 2, (Object) suggestions.size());
        Assert.assertEquals("The include statement '#include \"I2.h\"' is unneeded. Covered through: I1.h", suggestions.get(0).getDescription());
        Assert.assertEquals("The include statement '#include \"I3.h\"' is unneeded. Covered through: I1.h", suggestions.get(1).getDescription());
    }
}
