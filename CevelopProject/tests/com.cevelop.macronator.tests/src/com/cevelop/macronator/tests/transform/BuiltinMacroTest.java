package com.cevelop.macronator.tests.transform;

import static com.cevelop.macronator.tests.testutils.TestUtils.createFunctionStyleMacroDefinition;
import static com.cevelop.macronator.tests.testutils.TestUtils.createMacroDefinition;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.cevelop.macronator.transform.AutoFunctionTransformer;
import com.cevelop.macronator.transform.ConstexprTransformer;
import com.cevelop.macronator.transform.MacroTransformation;
import com.cevelop.macronator.transform.VoidFunctionTransformer;


@RunWith(Parameterized.class)
public class BuiltinMacroTest {

    MacroTransformation transformation;

    public BuiltinMacroTest(final MacroTransformation transformation) {
        this.transformation = transformation;

    }

    @Parameterized.Parameters
    public static Collection<Object[]> configs() {
        final MacroTransformation constexprContaining__FILE__ = new MacroTransformation(new ConstexprTransformer(createMacroDefinition(
                "#define MACRO __FILE__")));
        final MacroTransformation constexprContaining__LINE__ = new MacroTransformation(new ConstexprTransformer(createMacroDefinition(
                "#define MACRO __LINE__")));
        final MacroTransformation autoFunctionContaining__FILE__ = new MacroTransformation(new AutoFunctionTransformer(
                createFunctionStyleMacroDefinition("#define MACRO(A) __FILE__")));
        final MacroTransformation autoFunctionContaining__LINE__ = new MacroTransformation(new AutoFunctionTransformer(
                createFunctionStyleMacroDefinition("#define MACRO(A) __LINE__")));
        final MacroTransformation voidFunctionContaining__FILE__ = new MacroTransformation(new VoidFunctionTransformer(
                createFunctionStyleMacroDefinition("#define MACRO(X) do {__FILE__;} while(0);")));
        final MacroTransformation voidFunctionContaining__LINE__ = new MacroTransformation(new VoidFunctionTransformer(
                createFunctionStyleMacroDefinition("#define MACRO(X) do {__LINE__;} while(0);")));

        return Arrays.asList(new Object[][] { { constexprContaining__FILE__ }, { constexprContaining__LINE__ }, { autoFunctionContaining__FILE__ }, {
                                                                                                                                                      autoFunctionContaining__LINE__ },
                                              { voidFunctionContaining__LINE__ }, { voidFunctionContaining__FILE__ } });
    }

    @Test
    public void testTransformationShouldBeInvalidIfContains__LINE__or__FILE__() throws Exception {
        assertFalse(transformation.isValid());
    }
}
