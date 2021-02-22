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


public class ComplexRemoveUnused5AdditionalCollectingHeader extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new FindUnusedIncludesAlgorithm());

        Assert.assertEquals((Object) 4, (Object) suggestions.size());
        Assert.assertEquals("The include statement '#include \"A.h\"' is unneeded. Covered through: AtoD.h", suggestions.get(0).getDescription());
        Assert.assertEquals("The include statement '#include \"B.h\"' is unneeded. Covered through: AtoD.h", suggestions.get(1).getDescription());
        Assert.assertEquals("The include statement '#include \"C.h\"' is unneeded. Covered through: AtoD.h", suggestions.get(2).getDescription());
        Assert.assertEquals("The include statement '#include \"D.h\"' is unneeded. Covered through: AtoD.h", suggestions.get(3).getDescription());
    }
}
