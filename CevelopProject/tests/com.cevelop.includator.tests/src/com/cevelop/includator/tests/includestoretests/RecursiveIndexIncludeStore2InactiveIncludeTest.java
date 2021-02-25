/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.includestoretests;

import java.util.ArrayList;

import org.eclipse.cdt.core.index.IIndexInclude;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.stores.RecursiveIndexIncludeStore;
import com.cevelop.includator.tests.base.IncludatorTest;


public class RecursiveIndexIncludeStore2InactiveIncludeTest extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        RecursiveIndexIncludeStore store = IncludatorPlugin.getRecursiveIndexIncludeStore();
        ArrayList<IIndexInclude> recursiveIncludeRelations = store.getRecursiveIncludeRelations(getActiveIncludatorFile());

        Assert.assertEquals((Object) 1, (Object) recursiveIncludeRelations.size());
        Assert.assertEquals("A.h", recursiveIncludeRelations.get(0).getName());
        // file B.h is not included due to inactive code block in A.h.
    }
}
