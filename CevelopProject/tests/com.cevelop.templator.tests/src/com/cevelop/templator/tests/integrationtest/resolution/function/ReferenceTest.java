package com.cevelop.templator.tests.integrationtest.resolution.function;

import org.junit.Test;

import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.FunctionTemplateResolutionTest;
import com.cevelop.templator.tests.TemplateArgument;


public class ReferenceTest extends FunctionTemplateResolutionTest {

    @Test
    public void testOuterArgumentMapIsDoubleUnsignedLongLong() {
        testOuterArgumentMap(TemplateArgument.DOUBLE, TemplateArgument.UNSIGNED_LONG_LONG);
    }

    @Test
    public void testSubcallArgumentMapIsDoubleIntReferenceIntReference() throws TemplatorException {
        testFirstInnerArgumentMap(TemplateArgument.DOUBLE, TemplateArgument.INT, TemplateArgument.INT);
    }

    @Test
    public void testSubcallResolvedToFunctionTemplateAndNotNormalFunction() throws TemplatorException {
        testFirstInnerCallResolvesToFirstDefinition();
    }
}
