/*******************************************************************************
 * Copyright (c) 2011, 2014 Institute for Software, HSR Hochschule fuer Technik
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


public class DataStructureReferenceTest50NonIncludedMacroRefs extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 3, (Object) references.size());
        DeclarationReference xyRef = references.get(2);
        assertDeclRefDependencyTargetFile("macro.h", xyRef.getRequiredDependencies());
        Assert.assertFalse(xyRef.hadProblemsWhileResolving());
        assertStatusOk();
    }

}
