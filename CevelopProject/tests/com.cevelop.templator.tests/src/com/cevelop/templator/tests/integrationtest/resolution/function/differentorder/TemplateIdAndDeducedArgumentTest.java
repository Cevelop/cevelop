package com.cevelop.templator.tests.integrationtest.resolution.function.differentorder;

import org.junit.Test;

import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.FunctionTemplateResolutionTest;
import com.cevelop.templator.tests.TemplateArgument;


public class TemplateIdAndDeducedArgumentTest extends FunctionTemplateResolutionTest {

    @Test
    public void testOuterArgumentMapIsDoubleInt() {
        testOuterArgumentMap(TemplateArgument.DOUBLE, TemplateArgument.INT);
    }

    @Test
    public void testSubcallArgumentMapIsDoubleDouble() throws TemplatorException {
        testFirstInnerArgumentMap(TemplateArgument.DOUBLE, TemplateArgument.DOUBLE);
    }

    @Test
    public void testSubcallResolvedToFunctionTemplateAndNotNormalFunction() throws TemplatorException {
        testFirstInnerCallResolvesToFirstDefinition();
    }
}
