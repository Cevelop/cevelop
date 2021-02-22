package com.cevelop.macronator.tests.common;

import static com.cevelop.macronator.tests.testutils.TestUtils.createTranslationUnit;
import static org.junit.Assert.assertEquals;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroExpansion;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.junit.Test;

import com.cevelop.macronator.common.LocalExpansion;


public class LocalExpansionTest {

    @Test
    public void testFunctionStyleLocalExpansion() throws Exception {
        String input = "#define MACRO(A) (A) * (A) \n" + "int main() {" + "    MACRO(5);" + "}";
        IASTTranslationUnit inputAST = createTranslationUnit(input);
        IASTPreprocessorMacroExpansion macroExpansion = inputAST.getMacroExpansions()[0];
        String localExpansion = new LocalExpansion(macroExpansion).getExpansion();
        String expectedExpansion = "(5) * (5)";
        assertEquals(localExpansion + " does not match " + expectedExpansion, localExpansion, expectedExpansion);
    }

    @Test
    public void testObjectStyleLocalExpansion() throws Exception {
        String input = "#define MACRO 5 + 5 \n" + "int main() {" + "    MACRO;" + "}";
        IASTTranslationUnit inputAST = createTranslationUnit(input);
        IASTPreprocessorMacroExpansion macroExpansion = inputAST.getMacroExpansions()[0];
        String localExpansion = new LocalExpansion(macroExpansion).getExpansion();
        String expectedExpansion = "5 + 5";
        assertEquals(localExpansion + " does not match " + expectedExpansion, localExpansion, expectedExpansion);
    }
}
