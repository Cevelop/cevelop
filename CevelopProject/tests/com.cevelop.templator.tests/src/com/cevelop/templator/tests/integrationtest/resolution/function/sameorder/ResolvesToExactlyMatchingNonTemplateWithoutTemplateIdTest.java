package com.cevelop.templator.tests.integrationtest.resolution.function.sameorder;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.templator.plugin.asttools.data.AbstractResolvedNameInfo;
import com.cevelop.templator.plugin.asttools.templatearguments.TemplateArgumentMap;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.FunctionTemplateResolutionTest;
import com.cevelop.templator.tests.TemplateArgument;


public class ResolvesToExactlyMatchingNonTemplateWithoutTemplateIdTest extends FunctionTemplateResolutionTest {

    @Test
    public void testOuterArgumentMapIsBool() {
        testOuterArgumentMap(TemplateArgument.BOOL);
    }

    @Test
    public void testNumberOfInnerSubcallsIsZero() throws TemplatorException {
        // the function name itself
        firstStatementInMain.searchSubNames(loadingProgress);
        AbstractResolvedNameInfo innerCall = getOnlyFunctionCallSubstatements(firstStatementInMain).get(0).getInfo();
        innerCall.searchSubNames(loadingProgress);
        // zero because its not possible to click itself
        Assert.assertEquals(0, getOnlyFunctionCallSubstatements(innerCall).size());
    }

    @Test
    public void testInnerArgumentMapIsEmpty() throws TemplatorException {
        firstStatementInMain.searchSubNames(loadingProgress);
        assertEquals(new TemplateArgumentMap(), getOnlyFunctionCallSubstatements(firstStatementInMain).get(0).getInfo().getTemplateArgumentMap());
    }

    @Test
    public void testSubcallResolvedToNormalFunctionAndNotFunctionTemplate() throws TemplatorException {
        testFirstInnerCallResolvesToFirstDefinition();
    }
}
