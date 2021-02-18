package com.cevelop.templator.tests.integrationtest.resolution.function;

import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.junit.Test;

import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.FunctionTemplateResolutionTest;
import com.cevelop.templator.tests.TemplateArgument;


public class PassAliasesAsArgumentsTest extends FunctionTemplateResolutionTest {

    @Test
    public void testOuterArgumentMapIsInt() {
        testOuterArgumentMap(TemplateArgument.INT);
    }

    @Test
    public void testSubcallArgumentMapIsUnsignedLongLong() throws TemplatorException {
        testFirstInnerArgumentMap(TemplateArgument.UNSIGNED_LONG, TemplateArgument.LONG);
    }

    @Test
    public void testSubcallResolvedToFunctionTemplateAndNotNormalFunction() throws TemplatorException {
        testFirstInnerCallResolvesTo((IASTFunctionDefinition) definitions.get(2));
    }
}
