/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.testframeworktest;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.tests.base.IncludatorTest;


public class RefactoringTestFrameworkTest extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        Assert.assertEquals("XY.cpp", getNameOfPrimaryTestFile());
        Assert.assertEquals("#include <iostream>" + NL + NL + "int main() { return 0; }", testFiles.get(getNameOfPrimaryTestFile()).getSource());
        Assert.assertEquals("int main() { return 0; }", testFiles.get(getNameOfPrimaryTestFile()).getExpectedSource());
    }
}
