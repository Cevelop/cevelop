/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure.constructorrefs;

import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.ConstructorDeclarationReference;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class ConstructorRefTest13ExplConstrImplicitSubConstr extends IncludatorTest {

    private int     expectedRefTargetOffset;
    private boolean isCopyConstr;

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) (isCopyConstr ? 4 : 3), (Object) references.size());

        ConstructorDeclarationReference zConstrRef = getConstructorReference(references.get(1));
        Assert.assertTrue(zConstrRef.isImplicit());
        assertDeclRefName("Z", zConstrRef);
        assertNodeSignature("Y", zConstrRef.getASTNode());
        assertDeclaration(getRequiredDependency(zConstrRef).getDeclaration(), "Z.h", "Z", expectedRefTargetOffset);
    }

    @Override
    protected void configureTest(Properties properties) {
        isCopyConstr = Boolean.parseBoolean(properties.getProperty("isCopyConstr"));
        expectedRefTargetOffset = Integer.parseInt(properties.getProperty("expectedRefTargetOffset"));
        super.configureTest(properties);
    }

}
