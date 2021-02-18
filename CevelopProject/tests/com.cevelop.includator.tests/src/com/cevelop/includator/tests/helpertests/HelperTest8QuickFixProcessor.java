/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.helpertests;

import java.net.URI;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.IncludatorQuickFix;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.ui.IncludatorResolutionGenerator;
import com.cevelop.includator.ui.actions.FindUnusedIncludesAction;


public class HelperTest8QuickFixProcessor extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        runAction(new FindUnusedIncludesAction(), AlgorithmScope.FILE_SCOPE);

        URI uri = getActiveIncludatorFile().getIFile().getLocationURI();
        List<IncludatorQuickFix> quickFixes = new IncludatorResolutionGenerator().findQuickFixOnOffset(uri, 4);
        Assert.assertEquals((Object) 2, (Object) quickFixes.size());
        Assert.assertEquals((Object) 0, (Object) quickFixes.get(0).getStartOffset());
        Assert.assertEquals("Delete unused include.", quickFixes.get(0).getLabel());
        Assert.assertEquals("This action will remove the include from the current document.", quickFixes.get(0).getDescription());
        Assert.assertEquals("Suppress in future.", quickFixes.get(1).getLabel());
        String expectedSuppressMsg =
                                   "This action will configure Includator to not propose any suggestion of the include under consideration in the current file.";
        Assert.assertEquals(expectedSuppressMsg, quickFixes.get(1).getDescription());
    }
}
