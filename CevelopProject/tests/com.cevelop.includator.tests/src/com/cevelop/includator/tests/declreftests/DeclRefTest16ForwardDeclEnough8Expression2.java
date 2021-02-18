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


public class DeclRefTest16ForwardDeclEnough8Expression2 extends IncludatorTest {

    private boolean isFwdDeclRefEnough;
    private int     expectedRefCount;
    private int     bClassIndex;
    private int     bClassIndex2;
    private boolean isFwdDeclRefEnough2;

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) expectedRefCount, (Object) references.size());

        DeclarationReference classBRef = references.get(bClassIndex);
        Assert.assertTrue(classBRef.isClassReference());
        Assert.assertEquals(isFwdDeclRefEnough, classBRef.isForwardDeclarationEnough());
        DeclarationReference classBRef2 = references.get(bClassIndex2);
        Assert.assertTrue(classBRef2.isClassReference());
        Assert.assertEquals(isFwdDeclRefEnough2, classBRef2.isForwardDeclarationEnough());

        assertStatusOk();
    }

    @Override
    protected void configureTest(Properties properties) {
        expectedRefCount = Integer.parseInt(properties.getProperty("expectedRefCount"));
        bClassIndex = Integer.parseInt(properties.getProperty("bClassIndex"));
        bClassIndex2 = Integer.parseInt(properties.getProperty("bClassIndex2"));
        isFwdDeclRefEnough = Boolean.parseBoolean(properties.getProperty("isFwdDeclEnough"));
        isFwdDeclRefEnough2 = Boolean.parseBoolean(properties.getProperty("isFwdDeclEnough2"));
        super.configureTest(properties);
    }
}
