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
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureReferenceTest54TemplIdOfTemplSpecialization extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {

        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 4, (Object) references.size());

        DeclarationReference fooRef = references.get(1);
        assertDeclaration(getRequiredDependency(fooRef).getDeclaration(), "foo.h", "foo", 36);
    }
}
