/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.cxxelement.NamespaceDeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureReferenceTest53DefNamespaceAliasAndUsagePart1 extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        checkMainCppRefs();
        checkAliasHRefs();
    }

    private void checkMainCppRefs() throws IOException {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 3, (Object) references.size());

        DeclarationReference fooRef = references.get(1);
        assertDeclaration(getRequiredDependency(fooRef).getDeclaration(), "theNS_foo.h", "foo", 135);

        DeclarationReference myNSRef = references.get(2);
        assertDeclaration(getRequiredDependency(myNSRef).getDeclaration(), "alias.h", "myNS", 10);
        Assert.assertTrue(myNSRef instanceof NamespaceDeclarationReference);
    }

    private void checkAliasHRefs() throws IOException {
        List<DeclarationReference> references = getDeclarationReferences("alias.h");

        Assert.assertEquals((Object) 1, (Object) references.size());

        DeclarationReference theNSRef = references.get(0);
        assertDeclaration(getRequiredDependency(theNSRef).getDeclaration(), "theNS_empty.h", "theNS", 10);
        Assert.assertTrue(theNSRef instanceof NamespaceDeclarationReference);
    }
}
