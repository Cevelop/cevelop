package com.cevelop.templator.tests.integrationtest.resolution.function.sameorder;

import org.junit.Test;

import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.FunctionTemplateResolutionTest;
import com.cevelop.templator.tests.TemplateArgument;


public class TemplateIdAndDefaultArgumentTest extends FunctionTemplateResolutionTest {

    @Test
    public void testOuterArgumentMapIsIntLong() {
        testOuterArgumentMap(TemplateArgument.INT, TemplateArgument.LONG);
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
