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
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureReferenceTest22UnnamedFunctionParam extends IncludatorTest {

    private int expectedRefDepCount;

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();
        Assert.assertEquals((Object) expectedRefDepCount, (Object) references.size());
        assertDeclarationReference("Arg", "A.cpp", 36, references.get(expectedRefDepCount - 1));
    }

    @Override
    protected void configureTest(Properties properties) {
        expectedRefDepCount = Integer.parseInt(properties.getProperty("expectedRefDepCount"));
        super.configureTest(properties);
    }
}
