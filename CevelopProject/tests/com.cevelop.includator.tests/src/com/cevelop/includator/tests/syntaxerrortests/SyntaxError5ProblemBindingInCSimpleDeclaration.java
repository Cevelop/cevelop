/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.syntaxerrortests;

import org.junit.Test;

import com.cevelop.includator.optimizer.findunusedincludes.FindUnusedIncludesAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class SyntaxError5ProblemBindingInCSimpleDeclaration extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        runAlgorithm(new FindUnusedIncludesAlgorithm());
        assertStatus("No declaration found for \"k\" in file main.c[1:12,1:13].");
    }
}
