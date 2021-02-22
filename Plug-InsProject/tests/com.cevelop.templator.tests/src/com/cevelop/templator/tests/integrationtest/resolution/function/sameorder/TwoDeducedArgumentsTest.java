package com.cevelop.templator.tests.integrationtest.resolution.function.sameorder;

import org.junit.Test;

import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.FunctionTemplateResolutionTest;
import com.cevelop.templator.tests.TemplateArgument;


public class TwoDeducedArgumentsTest extends FunctionTemplateResolutionTest {

    @Test
    public void testOuterArgumentMapIsIntChar() {
        testOuterArgumentMap(TemplateArgument.INT, TemplateArgument.CHAR);
    }

    @Test
    public void testSubcallArgumentMapIsIntChar() throws TemplatorException {
        testFirstInnerArgumentMap(TemplateArgument.INT, TemplateArgument.CHAR);
    }

    @Test
    public void testSubcallResolvedToFunctionTemplateAndNotNormalFunction() throws TemplatorException {
        testFirstInnerCallResolvesToFirstDefinition();
    }
}
