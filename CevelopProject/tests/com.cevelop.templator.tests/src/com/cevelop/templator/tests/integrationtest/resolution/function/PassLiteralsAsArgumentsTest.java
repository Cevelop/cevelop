package com.cevelop.templator.tests.integrationtest.resolution.function;

import org.junit.Test;

import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.FunctionTemplateResolutionTest;
import com.cevelop.templator.tests.TemplateArgument;


public class PassLiteralsAsArgumentsTest extends FunctionTemplateResolutionTest {

    @Test
    public void testOuterArgumentMapIsInt() {
        testOuterArgumentMap(TemplateArgument.INT);
    }

    @Test
    public void testSubcallArgumentMapIsCharIntCharIntDouble() throws TemplatorException {
        testFirstInnerArgumentMap(TemplateArgument.CHAR, TemplateArgument.INT, TemplateArgument.CHAR, TemplateArgument.INT, TemplateArgument.DOUBLE);
    }

    @Test
    public void testSubcallResolvedToFunctionTemplateAndNotNormalFunction() throws TemplatorException {
        testFirstInnerCallResolvesToFirstDefinition();
    }
}
