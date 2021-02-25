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


public class DeclRefTest12ForwardDeclEnough8Expressions extends IncludatorTest {

    private boolean isFwdDeclRefEnough;
    private String  expectedExpressionText;
    private int     expectedRefCount;
    private int     bClassIndex;

    private boolean checkOtherType;
    private int     otherTypeIndex;
    private String  otherTypeName;
    private boolean otherTypeFwdDeclEnough;

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) expectedRefCount, (Object) references.size());

        DeclarationReference classBRef = references.get(bClassIndex);
        Assert.assertTrue(classBRef.isClassReference());
        assertDeclRefName(expectedExpressionText, classBRef);
        Assert.assertEquals(isFwdDeclRefEnough, classBRef.isForwardDeclarationEnough());

        if (checkOtherType) {
            checkOtherType(references);
        }
    }

    private void checkOtherType(List<DeclarationReference> references) {
        DeclarationReference otherTypeRef = references.get(otherTypeIndex);
        Assert.assertTrue(otherTypeRef.isClassReference());
        assertDeclRefName(otherTypeName, otherTypeRef);
        Assert.assertEquals(otherTypeFwdDeclEnough, otherTypeRef.isForwardDeclarationEnough());
    }

    @Override
    protected void configureTest(Properties properties) {
        expectedRefCount = Integer.parseInt(properties.getProperty("expectedRefCount"));
        expectedExpressionText = properties.getProperty("expectedExpressionText").trim();
        bClassIndex = Integer.parseInt(properties.getProperty("bClassIndex"));
        isFwdDeclRefEnough = Boolean.parseBoolean(properties.getProperty("isFwdDeclEnough"));

        checkOtherType = Boolean.parseBoolean(properties.getProperty("checkOtherType"));
        otherTypeIndex = Integer.parseInt(properties.getProperty("otherTypeIndex", "-1"));
        otherTypeName = properties.getProperty("otherTypeName");
        otherTypeFwdDeclEnough = Boolean.parseBoolean(properties.getProperty("otherTypeFwdDeclEnouth"));
        super.configureTest(properties);
    }
}
