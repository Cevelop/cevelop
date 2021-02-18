/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.syntaxerrortests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.findunusedincludes.FindUnusedIncludesAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class SyntaxError3UnresolvableBaseClass extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(wrapAsProjectAlgorithm(new FindUnusedIncludesAlgorithm()));

        Assert.assertEquals((Object) 1, (Object) suggestions.size());
        Assert.assertEquals("The include statement '#include \"A.h\"' is unneeded. No reference requires include.", suggestions.get(0)
                .getDescription());

        assertStatus("No declaration found for \"A\" in file B.h[2:18,2:19].");
    }
}
