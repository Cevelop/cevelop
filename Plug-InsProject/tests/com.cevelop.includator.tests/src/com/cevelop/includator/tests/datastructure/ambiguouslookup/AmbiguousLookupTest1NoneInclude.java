/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure.ambiguouslookup;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.tests.base.IncludatorTest;


/**
 * Note: Not ambiguous since CDT 9.3.0
 */
public class AmbiguousLookupTest1NoneInclude extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 2, (Object) references.size());
        DeclarationReference fooRef = references.get(1);
        List<DeclarationReferenceDependency> dependencies = fooRef.getRequiredDependencies();
        assertDeclRefDependencyTargetFile("foo.h", dependencies);
    }
}
