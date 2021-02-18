package com.cevelop.templator.tests.asttests;

import static org.junit.Assert.assertEquals;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.parser.ParserLanguage;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.junit.Test;

import com.cevelop.templator.plugin.asttools.ASTTools;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.TemplatorSimpleTest;
import com.cevelop.templator.tests.TestHelper;


public class ExtractTemplateInstanceNameTest extends TemplatorSimpleTest {

    @Test
    // template<typename T> void foo(T value){}
    // int main() {
    // foo(5.2);
    // }
    public void testExtractTemplateInstanceNameWithoutAngleBrackets() throws Exception {
        String source = TestHelper.getCommentAbove(getClass());
        IASTTranslationUnit ast = TestHelper.parse(source, ParserLanguage.CPP);

        IASTName expectedFunctionName = TestHelper.findName(ast, 1, "foo");
        IASTName foundFunctionName = ASTTools.extractTemplateInstanceName(expectedFunctionName);

        assertEquals(expectedFunctionName, foundFunctionName);
    }

    @Test
    // template<typename T> void foo(T value){}
    // int main() {
    // foo<double>(5.2);
    // }
    public void testExtractTemplateInstanceNameWithAngleBrackets() throws Exception {
        String source = TestHelper.getCommentAbove(getClass());
        IASTTranslationUnit ast = TestHelper.parse(source, ParserLanguage.CPP);

        IASTName expectedFunctionName = (IASTName) TestHelper.findName(ast, 1, "foo").getParent();
        IASTName foundFunctionName = ASTTools.extractTemplateInstanceName(expectedFunctionName);

        assertEquals(expectedFunctionName, foundFunctionName);
    }

    @Test(expected = TemplatorException.class)
    public void extractTemplateInstanceNameWithNoParent() throws TemplatorException {
        ASTTools.extractTemplateInstanceName(new CPPASTName());
    }
}
