package com.cevelop.templator.tests.integrationtest.resolution.function.sameorder;

import org.junit.Test;

import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.FunctionTemplateResolutionTest;
import com.cevelop.templator.tests.TemplateArgument;


public class CompleteDefaultArgumentsAndEmptyTemplateIdWithArgumentsTest extends FunctionTemplateResolutionTest {

    @Test
    public void testOuterArgumentMapIsIntBool() {
        testOuterArgumentMap(TemplateArgument.INT, TemplateArgument.BOOL);
    }

    @Test
    public void testSubcallArgumentMapIsIntBool() throws TemplatorException {
        testFirstInnerArgumentMap(TemplateArgument.INT, TemplateArgument.BOOL);
    }

    @Test
    public void testSubcallResolvedToFunctionTemplateAndNotNormalFunction() throws TemplatorException {
        testFirstInnerCallResolvesToFirstDefinition();
    }

}
