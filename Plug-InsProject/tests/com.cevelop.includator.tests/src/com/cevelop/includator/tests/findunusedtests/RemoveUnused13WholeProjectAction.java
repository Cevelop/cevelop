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

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.ui.actions.FindUnusedIncludesAction;


public class RemoveUnused13WholeProjectAction extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        runAction(new FindUnusedIncludesAction(), AlgorithmScope.PROJECT_SCOPE);

        List<Suggestion<?>> suggestions = IncludatorPlugin.getSuggestionStore().getAllSuggestions();

        Assert.assertEquals((Object) 3, (Object) suggestions.size());
        Assert.assertEquals("The include statement '#include \"F.h\"' is unneeded. No reference requires include.", suggestions.get(0)
                .getDescription());
        Assert.assertEquals("A.cpp", suggestions.get(0).getProjectRelativePath());
        Assert.assertEquals("The include statement '#include \"C.h\"' is unneeded. No reference requires include.", suggestions.get(1)
                .getDescription());
        Assert.assertEquals("B.h", suggestions.get(1).getProjectRelativePath());
        Assert.assertEquals("The include statement '#include \"B.h\"' is unneeded. No reference requires include.", suggestions.get(2)
                .getDescription());
        Assert.assertEquals("D.cpp", suggestions.get(2).getProjectRelativePath());
    }
}
