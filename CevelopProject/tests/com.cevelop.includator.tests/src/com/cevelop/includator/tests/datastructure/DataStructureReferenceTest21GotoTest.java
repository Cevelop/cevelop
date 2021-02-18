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
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureReferenceTest21GotoTest extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();
        Assert.assertEquals((Object) 2, (Object) references.size());
        List<DeclarationReferenceDependency> loopDependencies = references.get(1).getRequiredDependencies();
        Assert.assertEquals((Object) 1, (Object) loopDependencies.size());
        assertFileLocation(loopDependencies.get(0), "A.cpp", 20, "A.cpp", 27, 4);
    }
}
