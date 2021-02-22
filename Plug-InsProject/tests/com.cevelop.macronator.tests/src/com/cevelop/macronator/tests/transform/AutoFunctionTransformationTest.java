package com.cevelop.macronator.tests.transform;

import static com.cevelop.macronator.tests.testutils.TestUtils.assertTransformationEquals;
import static com.cevelop.macronator.tests.testutils.TestUtils.createFunctionStyleMacroDefinition;
import static org.junit.Assert.assertFalse;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorFunctionStyleMacroDefinition;
import org.junit.Test;

import com.cevelop.macronator.transform.AutoFunctionTransformer;
import com.cevelop.macronator.transform.MacroTransformation;


public class AutoFunctionTransformationTest {

    @Test
    public void testShouldProduceCorrectTransformationForParameterizedExpressionWithOneParameter() {
        final String macro = "#define SQUARE(X) (X) * (X)";
        final String macroTranslation = "template<typename T1> constexpr inline auto SQUARE(T1&& X) -> decltype((X) * (X)){return ((X) * (X));}";
        assertTransformationEquals(macroTranslation, createTransformation(macro));
    }

    private MacroTransformation createTransformation(final String macro) {
        return new MacroTransformation(new AutoFunctionTransformer(createFunctionStyleMacroDefinition(macro)));
    }

    @Test
    public void testShouldProduceCorrectTransformationForParameterizedExpressionWithTwoParameters() {
        final String macro = "#define ADD(A, B) ((A) + (B))";
        final String expectedTransformation =
                                            "template<typename T1, typename T2> constexpr inline auto ADD(T1&& A, T2&& B) -> decltype(((A) + (B))) {return (((A) + (B)));}";
        assertTransformationEquals(expectedTransformation, createTransformation(macro));
    }

    @Test
    public void testShouldProduceCorrectTransformationForParameterizedExpressionWithThreeParameters() {
        final String macro = "#define MACRO(A, B, C) X\n";
        final String expectedTransformation =
                                            "template<typename T1, typename T2, typename T3> constexpr inline auto MACRO(T1&& A, T2&& B, T3&& C) -> decltype(X) { return (X);}";
        assertTransformationEquals(expectedTransformation, createTransformation(macro));
    }

    @Test
    public void testReturnFunctionTransformationShouldBeInvalidIfFunctionDoesntExpandIntoExpression() throws Exception {
        final IASTPreprocessorFunctionStyleMacroDefinition macroDefinition = createFunctionStyleMacroDefinition("#define MACRO(A) do {x;} while(0);");
        assertFalse(new MacroTransformation(new AutoFunctionTransformer(macroDefinition)).isValid());
    }

    @Test
    public void test_BUG101_ShouldProduceCorrectTransformationForBinaryMacroWithConditionalExpression() throws Exception {
        final String macro = "#define MIN(x,y) (x < y ? x : y)";
        final String expectedTransformation =
                "template<typename T1, typename T2> inline constexpr auto MIN(T1&& x, T2&& y) -> decltype((x < y ? x : y)) {return ((x < y ? x : y));}";
        assertTransformationEquals(expectedTransformation, createTransformation(macro));
    }
}
