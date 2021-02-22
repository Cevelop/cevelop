/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.includestoretests;

import org.eclipse.cdt.core.index.IIndexInclude;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.stores.IndexIncludeStore;
import com.cevelop.includator.tests.base.IncludatorTest;


public class IndexIncludeStore3InactiveIncludeTest extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        IndexIncludeStore store = IncludatorPlugin.getIndexIncludeStore();
        IncludatorFile file = getActiveIncludatorFile();
        IIndexInclude[] includes = store.getIncludes(FileHelper.getIndexFile(file), getActiveProject().getIndex());

        Assert.assertEquals((Object) 1, (Object) includes.length);
        Assert.assertEquals("A.h", includes[0].getName());
        // file B.h is not included due to inactive code block in A.h.
    }
}
