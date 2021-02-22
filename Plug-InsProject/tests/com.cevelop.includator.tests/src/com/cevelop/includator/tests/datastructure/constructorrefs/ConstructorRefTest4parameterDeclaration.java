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

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class ConstructorRefTest4parameterDeclaration extends IncludatorTest {

    private boolean hasCopyExplicitCopyConstructor;

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 8, (Object) references.size());
        DeclarationReference copyConstructorRef = references.get(2);
        assertDeclRefName("X", copyConstructorRef);
        assertNodeSignature("x", copyConstructorRef.getASTNode());
        if (hasCopyExplicitCopyConstructor) {
            assertDeclaration(getRequiredDependency(copyConstructorRef).getDeclaration(), "X.h", "X", 27);
        } else { // having no explicit copy constr means that the default resolves to the class X which is located at offset 6
            assertDeclaration(getRequiredDependency(copyConstructorRef).getDeclaration(), "X.h", "X", 6);
        }
    }

    @Override
    protected void configureTest(Properties properties) {
        hasCopyExplicitCopyConstructor = Boolean.parseBoolean(properties.getProperty("containsCorrectCopyConstr"));
        super.configureTest(properties);
    }

}
