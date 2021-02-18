package com.cevelop.macronator.tests.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorFunctionStyleMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.internal.core.dom.rewrite.astwriter.ASTWriter;

import com.cevelop.macronator.common.ParserAdapter;
import com.cevelop.macronator.transform.MacroTransformation;


@SuppressWarnings("restriction")
public class TestUtils {

    public static IASTPreprocessorMacroDefinition createMacroDefinition(final String code) {
        ParserAdapter parser = new ParserAdapter(code);
        IASTTranslationUnit translationUnit = parser.parse();
        assertFalse(parser.encounteredErrors());
        return (IASTPreprocessorMacroDefinition) translationUnit.getAllPreprocessorStatements()[0];
    }

    public static IASTPreprocessorFunctionStyleMacroDefinition createFunctionStyleMacroDefinition(final String code) {
        IASTPreprocessorMacroDefinition macroDefinition = createMacroDefinition(code);
        if (macroDefinition instanceof IASTPreprocessorFunctionStyleMacroDefinition) {
            return (IASTPreprocessorFunctionStyleMacroDefinition) macroDefinition;
        }
        throw new RuntimeException(String.format("\"%s\" is not a function style macro definition", code));
    }

    public static void assertTransformationEquals(String expected, MacroTransformation transformation) {
        assertTrue("validate expected code", isValid(expected));
        assertEquals(format(expected), transformation.getCode());
    }

    public static String format(String code) {
        if (isValid(code)) {
            return new ASTWriter().write(new ParserAdapter(code).parse());
        } else {
            fail("formatting failed. invalid code: " + code);
            return "<formatting failed>";
        }
    }

    private static boolean isValid(String code) {
        ParserAdapter parser = new ParserAdapter(code);
        parser.parse();
        return !parser.encounteredErrors();
    }

    public static IASTTranslationUnit createTranslationUnit(String input) {
        ParserAdapter parserAdapter = new ParserAdapter(input);
        IASTTranslationUnit inputAST = parserAdapter.parse();
        return inputAST;
    }
}
