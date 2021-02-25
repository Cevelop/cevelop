/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
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
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureReferenceTest12UnresolvedReferenceTest extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 3, (Object) references.size());

        assertResolvable(false, references.get(0));
        assertResolvable(true, references.get(1));
        assertResolvable(false, references.get(2));
    }

    private void assertResolvable(final boolean expected, final DeclarationReference ref) {
        ref.getRequiredDependencies();
        Assert.assertEquals(expected, ref.hadProblemsWhileResolving());
    }
}
