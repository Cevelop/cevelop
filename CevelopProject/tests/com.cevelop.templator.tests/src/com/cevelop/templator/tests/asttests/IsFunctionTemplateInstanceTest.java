package com.cevelop.templator.tests.asttests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.parser.ParserLanguage;
import org.junit.Test;

import com.cevelop.templator.plugin.asttools.data.NameTypeKind;
import com.cevelop.templator.tests.TestHelper;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.base.CDTTestingUITest;


public class IsFunctionTemplateInstanceTest extends CDTTestingUITest {

    @Test
    public void testIsTemplateFunctionInstanceFromNameGoodCase() throws Exception {

        String source = testFiles.get(getNameOfPrimaryTestFile()).getSource();
        IASTTranslationUnit ast = TestHelper.parse(source, ParserLanguage.CPP);

        IASTNode node = TestHelper.findName(ast, 0, "foo");
        assertTrue(node instanceof IASTName);
        assertFalse(NameTypeKind.isFunctionTemplateInstance(((IASTName) node).resolveBinding()));

        node = TestHelper.findName(ast, 1, "foo");
        assertTrue(node instanceof IASTName);
        assertTrue(NameTypeKind.isFunctionTemplateInstance(((IASTName) node).resolveBinding()));
    }

    @Test
    public void testIsTemplateFunctionInstanceFromNameBadCase() throws Exception {

        String source = testFiles.get(getNameOfPrimaryTestFile()).getSource();
        IASTTranslationUnit ast = TestHelper.parse(source, ParserLanguage.CPP);

        IASTNode node = TestHelper.findName(ast, 0, "bar");
        assertTrue(node instanceof IASTName);
        assertFalse(NameTypeKind.isFunctionTemplateInstance(((IASTName) node).resolveBinding()));

        node = TestHelper.findName(ast, 1, "bar");
        assertTrue(node instanceof IASTName);
        assertFalse(NameTypeKind.isFunctionTemplateInstance(((IASTName) node).resolveBinding()));
    }
}
