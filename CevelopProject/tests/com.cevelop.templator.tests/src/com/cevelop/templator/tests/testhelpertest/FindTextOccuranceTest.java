/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.templator.tests.testhelpertest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.parser.ParserLanguage;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDeclarator;
import org.junit.Test;

import com.cevelop.templator.tests.TestHelper;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.base.CDTTestingUITest;


public class FindTextOccuranceTest extends CDTTestingUITest {

    @Test
    public void findTextOccuranceNullTest() throws Throwable {
        String source = testFiles.get(getNameOfPrimaryTestFile()).getSource();
        IASTTranslationUnit ast = TestHelper.parse(source, ParserLanguage.CPP);

        IASTNode occurance = TestHelper.findName(ast, 0, "foo");
        assertNotNull("Occurance 0 is null", occurance);
        assertEquals("foo", occurance.toString());

        occurance = TestHelper.findName(ast, 3, "foo");
        assertNotNull("Occurance 3 is null", occurance);
        assertEquals("foo", occurance.toString());

        occurance = TestHelper.findName(ast, 4, "foo");
        assertNull("Occurance 4 should be null", occurance);
    }

    @Test
    public void findTextOccuranceTypeTest() throws Throwable {
        String source = testFiles.get(getNameOfPrimaryTestFile()).getSource();
        IASTTranslationUnit ast = TestHelper.parse(source, ParserLanguage.CPP);

        IASTNode occurance = TestHelper.findName(ast, 0, "bar");
        assertTrue("Occurance 0 should be a declaration", occurance.getParent() instanceof CPPASTFunctionDeclarator);

        occurance = TestHelper.findName(ast, 1, "bar");
        assertTrue("Occurance 1 should be a definition", occurance.getParent().getParent() instanceof ICPPASTFunctionCallExpression);

    }
}
