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
import com.cevelop.includator.preferences.IncludatorPropertyManager;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.ui.helpers.ResourceWrapperStore;


public class RemoveUnused21ExcludedClassReference extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        ResourceWrapperStore store = new ResourceWrapperStore();
        store.setResource(getActiveProject().getCProject().getProject());
        store.setValue(IncludatorPropertyManager.P_EXCLUDE_SYMBOL_IN_WORKSPACE_PREFERENCE, "A,~A");
        List<Suggestion<?>> suggestions = runAlgorithm(new FindUnusedIncludesAlgorithm());

        Assert.assertEquals((Object) 1, (Object) suggestions.size());
        Assert.assertEquals("The include statement '#include \"A.h\"' is unneeded. No reference requires include.", suggestions.get(0)
                .getDescription());

        store.setValue(IncludatorPropertyManager.P_EXCLUDE_SYMBOL_IN_WORKSPACE_PREFERENCE, "");
    }
}
