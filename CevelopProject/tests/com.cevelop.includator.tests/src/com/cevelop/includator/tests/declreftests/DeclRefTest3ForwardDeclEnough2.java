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


public class DeclRefTest3ForwardDeclEnough2 extends IncludatorTest {

    private boolean isForwardDeclEnough;

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) (isForwardDeclEnough ? 3 : 5), (Object) references.size());

        int classBIndex = isForwardDeclEnough ? 1 : 2;
        assertAreClassReferences(references, classBIndex);
        DeclarationReference classBRef = references.get(classBIndex);
        assertDeclarationReference("B", "main.cpp", 30, classBRef);
        Assert.assertEquals(isForwardDeclEnough, classBRef.isForwardDeclarationEnough());
    }

    @Override
    protected void configureTest(Properties properties) {
        isForwardDeclEnough = Boolean.valueOf(properties.getProperty("expected")).booleanValue();
        super.configureTest(properties);
    }
}
