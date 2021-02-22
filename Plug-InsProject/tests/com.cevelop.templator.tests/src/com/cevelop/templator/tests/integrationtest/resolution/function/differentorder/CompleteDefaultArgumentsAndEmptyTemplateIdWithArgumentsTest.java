package com.cevelop.templator.tests.integrationtest.resolution.function.differentorder;

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
    public void testSubcallArgumentMapIsBoolInt() throws TemplatorException {
        testFirstInnerArgumentMap(TemplateArgument.BOOL, TemplateArgument.INT);
    }

    @Test
    public void testSubcallResolvedToFunctionTemplateAndNotNormalFunction() throws TemplatorException {
        testFirstInnerCallResolvesToFirstDefinition();
    }

}
