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
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.dependency.FullIncludePath;
import com.cevelop.includator.stores.IncludePathStore;
import com.cevelop.includator.tests.base.IncludatorTest;


public class IncludePathStore1DuplicateTest extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> refs = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 6, (Object) refs.size());

        assertDeclarationReference("A", "main.cpp", 46, refs.get(2));
        DeclarationReferenceDependency dependency = getRequiredDependency(refs.get(2));
        List<FullIncludePath> incl = dependency.getIncludePaths();
        IncludePathStore store = IncludatorPlugin.getIncludePathStore();
        ArrayList<FullIncludePath> inclFromStore = store.getIncludePaths(dependency.getDeclarationReference().getFile(), dependency.getDeclaration()
                .getFile());
        Assert.assertTrue(incl == inclFromStore);
    }
}
