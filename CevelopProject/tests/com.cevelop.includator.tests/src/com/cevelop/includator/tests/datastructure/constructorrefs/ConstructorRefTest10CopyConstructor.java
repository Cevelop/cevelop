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


public class ConstructorRefTest10CopyConstructor extends IncludatorTest {

    private boolean hasCopyConstrImpl;

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 7, (Object) references.size());
        DeclarationReference copyConstrRef = references.get(6);
        assertDeclRefName("X", copyConstrRef);
        assertNodeSignature("x2", copyConstrRef.getASTNode());
        int expectedOffset = hasCopyConstrImpl ? 27 : 6;
        assertDeclaration(getRequiredDependency(copyConstrRef).getDeclaration(), "X.h", "X", expectedOffset);
    }

    @Override
    protected void configureTest(Properties properties) {
        hasCopyConstrImpl = Boolean.parseBoolean(properties.getProperty("hasCopyConstrImpl"));
        super.configureTest(properties);
    }

}
