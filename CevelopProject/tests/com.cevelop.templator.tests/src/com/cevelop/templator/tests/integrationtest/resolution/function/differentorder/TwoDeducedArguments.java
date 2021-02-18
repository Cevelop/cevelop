package com.cevelop.templator.tests.integrationtest.resolution.function.differentorder;

import org.junit.Test;

import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.FunctionTemplateResolutionTest;
import com.cevelop.templator.tests.TemplateArgument;


public class TwoDeducedArguments extends FunctionTemplateResolutionTest {

    @Test
    public void testOuterArgumentMapIsIntChar() {
        testOuterArgumentMap(TemplateArgument.INT, TemplateArgument.CHAR);
    }

    @Test
    public void testSubcallArgumentMapIsCharInt() throws TemplatorException {
        testFirstInnerArgumentMap(TemplateArgument.CHAR, TemplateArgument.INT);
    }

    @Test
    public void testSubcallResolvedToFunctionTemplateAndNotNormalFunction() throws TemplatorException {
        testFirstInnerCallResolvesToFirstDefinition();
    }
}
