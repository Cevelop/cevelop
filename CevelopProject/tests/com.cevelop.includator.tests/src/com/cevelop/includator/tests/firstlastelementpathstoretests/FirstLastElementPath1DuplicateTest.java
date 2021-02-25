/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.firstlastelementpathstoretests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.dependency.FirstLastElementIncludePath;
import com.cevelop.includator.stores.FirstLastElementIncludePathStore;
import com.cevelop.includator.tests.base.IncludatorTest;


public class FirstLastElementPath1DuplicateTest extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> refs = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 6, (Object) refs.size());

        assertDeclarationReference("A", "A.cpp", 46, refs.get(2));
        DeclarationReferenceDependency dependency = getRequiredDependency(refs.get(2));
        List<FirstLastElementIncludePath> incl = dependency.getFirstLastElementIncludePaths();
        FirstLastElementIncludePathStore store = IncludatorPlugin.getFirstLastElementIncludePathStore();
        List<FirstLastElementIncludePath> inclFromStore = store.getIncludePaths(dependency.getDeclarationReference().getFile(), dependency
                .getDeclaration().getFile());
        Assert.assertTrue(incl == inclFromStore);
    }
}
