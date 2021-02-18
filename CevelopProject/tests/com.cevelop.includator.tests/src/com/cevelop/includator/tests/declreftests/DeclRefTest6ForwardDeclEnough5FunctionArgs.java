/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.declreftests;

import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DeclRefTest6ForwardDeclEnough5FunctionArgs extends IncludatorTest {

    private boolean isForwardDeclEnough;
    private int     expectedRefCount;

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) expectedRefCount, (Object) references.size());

        int classBIndex = expectedRefCount == 3 ? 1 : 3;
        assertAreClassReferences(references, classBIndex);
        DeclarationReference classBRef = references.get(classBIndex);
        assertDeclarationReference("B", "main.cpp", 24, classBRef);
        Assert.assertEquals(isForwardDeclEnough, classBRef.isForwardDeclarationEnough());
    }

    @Override
    protected void configureTest(Properties properties) {
        isForwardDeclEnough = Boolean.valueOf(properties.getProperty("expected")).booleanValue();
        expectedRefCount = Integer.valueOf(properties.getProperty("expectedRefCount")).intValue();
        super.configureTest(properties);
    }
}
