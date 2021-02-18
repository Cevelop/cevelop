package com.cevelop.templator.tests.integrationtest.resolution.function;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.templator.tests.FunctionTemplateResolutionTest;
import com.cevelop.templator.tests.TemplateArgument;


public class OneLevelWithOneTemplateIdArgumentTest extends FunctionTemplateResolutionTest {

    @Test
    public void testNumberOfSubcallsIsZero() {
        Assert.assertEquals(0, firstStatementInMain.getSubNames().size());
    }

    @Test
    public void testArgumentMapIsDouble() {
        testOuterArgumentMap(TemplateArgument.DOUBLE);
    }
}
