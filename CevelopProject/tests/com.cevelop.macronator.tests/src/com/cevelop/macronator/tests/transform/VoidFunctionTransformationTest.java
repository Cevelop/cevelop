package com.cevelop.macronator.tests.transform;

import static com.cevelop.macronator.tests.testutils.TestUtils.assertTransformationEquals;
import static com.cevelop.macronator.tests.testutils.TestUtils.createFunctionStyleMacroDefinition;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.cevelop.macronator.transform.MacroTransformation;
import com.cevelop.macronator.transform.VoidFunctionTransformer;


public class VoidFunctionTransformationTest {

    @Test
    public void testShouldProduceCorrectVoidFunctionCodeForParameterizedExpressionWithOneParameter() {
        final String macro = "#define MACRO(A) (X)";
        final String macroTransformation = "template<typename T1> void MACRO(T1&& A) {(X);}";
        assertTransformationEquals(macroTransformation, createTransformation(macro));
    }

    @Test
    public void testTransformationShouldBeValidIfTransformationIsPossible() {
        final String macro = "#define MACRO(A) (X)";
        assertTrue(createTransformation(macro).isValid());
    }

    @Test
    public void testTransformationShouldBeValidIfTransformationIsTheEmptyString() {
        final String macro = "#define MACRO(A) ";
        assertTrue(createTransformation(macro).isValid());
    }

    @Test
    public void testTransformationIsValidShouldBeFalseIfReplacementTextSyntaxIsIncorrect() {
        final String macro = "#define MACRO(A) if (";
        assertFalse(createTransformation(macro).isValid());
    }

    @Test
    public void testShouldProduceCorrectVoidFunctionCodeForParameterizedExpressionWithTwoParameters() {
        final String macro = "#define MACRO(A, B) (X)";
        final String macroTransformation = "template<typename T1, typename T2> void MACRO(T1&& A, T2&& B) {(X);}";
        assertTransformationEquals(macroTransformation, createTransformation(macro));
    }

    @Test
    public void testShouldProduceCorrectVoidFunctionForParameterizedExpressionWithThreeParameters() {
        final String macro = "#define MACRO(A, B, C) (X)";
        final String macroTransformation = "template<typename T1, typename T2, typename T3> void MACRO(T1&& A, T2&& B, T3&& C) {(X);}";
        assertTransformationEquals(macroTransformation, createTransformation(macro));
    }

    @Test
    public void testShouldProduceCorrectVoidFunctionForDoWhileStatement() {
        final String macro = "#define MACRO(X) do { X; } while (0)";
        final String macroTransformation = "template<typename T1> void MACRO(T1&& X) {do { X; } while (0);}";
        assertTransformationEquals(macroTransformation, createTransformation(macro));
    }

    @Test
    public void testShouldNotAddSemicolonToStatementIfAlreadyExisting() throws Exception {
        final String macro = "#define MACRO(X) do {X;} while(0);";
        final String macroTransformation = "template<typename T1> void MACRO(T1&& X) {do { X; } while (0);}";
        assertTransformationEquals(macroTransformation, createTransformation(macro));
    }

    private MacroTransformation createTransformation(final String macro) {
        return new MacroTransformation(new VoidFunctionTransformer(createFunctionStyleMacroDefinition(macro)));
    }
}
