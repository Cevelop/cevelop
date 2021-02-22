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


public class AmbiguousLookupTest2BothIncluded extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 2, (Object) references.size());
        DeclarationReference fooRef = references.get(1);
        List<DeclarationReferenceDependency> dependencies = fooRef.getRequiredDependencies();
        assertDeclRefDependencyTargetFile("foo.h", dependencies);
        String candidateFooH = "foo.h[1:6,1:9]";
        String candidateOtherFooH = "otherFoo.h[2:10,2:13]";
        String multipleDefsMsg = "Found multiple definitions of foo in main.cpp[5:5,5:8]. Prefering foo in foo.h[1:6,1:9]. Candidates are:";
        String ambiguousMsg = "Use of foo in main.cpp[5:5,5:8] is ambiguous. Candidates are:";
        assertStatus(multipleDefsMsg, candidateFooH, candidateOtherFooH, ambiguousMsg, candidateFooH, candidateOtherFooH);
    }
}
