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


public class DataStructureReferenceTest41TypedefToMemberFunction extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 9, (Object) references.size());

        DeclarationReference typeRef = references.get(0);
        assertDeclaration(getRequiredDependency(typeRef).getDeclaration(), "src/Baum.h", "Baum", 6);
        Assert.assertTrue(!typeRef.isForwardDeclarationEnough());

        DeclarationReference memFunRef = references.get(8);
        assertDeclaration(getRequiredDependency(memFunRef).getDeclaration(), "src/Baum.h", "foo", 55);
        Assert.assertTrue(!memFunRef.isForwardDeclarationEnough());
    }
}
