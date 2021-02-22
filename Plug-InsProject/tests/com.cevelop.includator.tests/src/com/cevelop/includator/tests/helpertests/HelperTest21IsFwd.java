/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.helpertests;

import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.helpers.NodeHelper;
import com.cevelop.includator.tests.base.IncludatorTest;


public class HelperTest21IsFwd extends IncludatorTest {

    private boolean expedted;
    private int     nameToCheckIndex;

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> refs = getActiveFileDeclarationReferences();
        Assert.assertEquals(expedted, NodeHelper.isForwardDeclaration(refs.get(nameToCheckIndex).getASTNode()));
    }

    @Override
    protected void configureTest(Properties properties) {
        expedted = Boolean.parseBoolean(properties.getProperty("expedted", "false"));
        nameToCheckIndex = Integer.parseInt(properties.getProperty("nameToCheckIndex", "0"));
        super.configureTest(properties);
    }
}
