/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.preferences.IncludatorPropertyManager;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.ui.helpers.ResourceWrapperStore;


public class DataStructureReferenceTest49ExcludedSymbolReference extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        final ResourceWrapperStore store = new ResourceWrapperStore();
        store.setResource(getActiveProject().getCProject().getProject());
        store.setValue(IncludatorPropertyManager.P_EXCLUDE_SYMBOL_IN_WORKSPACE_PREFERENCE, "EXCLUDED_SYMBOL");

        List<DeclarationReference> references = getActiveFileDeclarationReferences();
        Assert.assertEquals((Object) 1, (Object) references.size());

        store.setValue(IncludatorPropertyManager.P_EXCLUDE_SYMBOL_IN_WORKSPACE_PREFERENCE, "");

    }
}
