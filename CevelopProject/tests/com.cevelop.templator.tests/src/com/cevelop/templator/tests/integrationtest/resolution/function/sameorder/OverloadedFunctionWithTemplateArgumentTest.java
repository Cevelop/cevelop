package com.cevelop.templator.tests.integrationtest.resolution.function.sameorder;

import org.junit.Test;

import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.FunctionTemplateResolutionTest;
import com.cevelop.templator.tests.TemplateArgument;


public class OverloadedFunctionWithTemplateArgumentTest extends FunctionTemplateResolutionTest {

    @Test
    public void testSubcallResolvedToDoubleId() throws TemplatorException {
        testFirstInnerCallResolvesToFirstDefinition();
    }

    @Test
    public void testArgumentMapIsDouble() {
        testOuterArgumentMap(TemplateArgument.DOUBLE);
    }
}
