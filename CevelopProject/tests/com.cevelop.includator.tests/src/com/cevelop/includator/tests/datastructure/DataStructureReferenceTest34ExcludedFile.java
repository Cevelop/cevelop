/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.preferences.IncludatorPropertyManager;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.ui.helpers.ResourceWrapperStore;


public class DataStructureReferenceTest34ExcludedFile extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {

        final ResourceWrapperStore store = new ResourceWrapperStore();
        store.setResource(getActiveProject().getCProject().getProject());
        store.setValue(IncludatorPropertyManager.EXCLUDE_RESOURCES_PROPERTY_NAME, "main.cpp");

        Assert.assertEquals((Object) 0, (Object) getActiveProject().getAffectedFiles().size());
    }
}
