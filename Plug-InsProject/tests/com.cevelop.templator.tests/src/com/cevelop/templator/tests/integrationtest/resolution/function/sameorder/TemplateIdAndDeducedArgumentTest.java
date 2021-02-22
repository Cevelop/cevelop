package com.cevelop.templator.tests.integrationtest.resolution.function.sameorder;

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
    public void testSubcallArgumentMapIsIntInt() throws TemplatorException {
        testFirstInnerArgumentMap(TemplateArgument.INT, TemplateArgument.INT);
    }

    @Test
    public void testSubcallResolvedToFunctionTemplateAndNotNormalFunction() throws TemplatorException {
        testFirstInnerCallResolvesToFirstDefinition();
    }
}
