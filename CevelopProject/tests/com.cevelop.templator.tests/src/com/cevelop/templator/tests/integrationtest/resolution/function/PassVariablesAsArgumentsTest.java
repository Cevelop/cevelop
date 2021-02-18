package com.cevelop.templator.tests.integrationtest.resolution.function;

import org.junit.Test;

import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.FunctionTemplateResolutionTest;
import com.cevelop.templator.tests.TemplateArgument;


public class PassVariablesAsArgumentsTest extends FunctionTemplateResolutionTest {

    @Test
    public void testOuterArgumentMapIsChar() {
        testOuterArgumentMap(TemplateArgument.CHAR);
    }

    @Test
    public void testSubcallArgumentMapIsDoubleIntChar() throws TemplatorException {
        testFirstInnerArgumentMap(TemplateArgument.DOUBLE, TemplateArgument.INT, TemplateArgument.CHAR);
    }

    @Test
    public void testSubcallResolvedToFunctionTemplateAndNotNormalFunction() throws TemplatorException {
        testFirstInnerCallResolvesToFirstDefinition();
    }
}
