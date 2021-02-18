package com.cevelop.templator.tests.integrationtest.resolution.function;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.FunctionTemplateResolutionTest;


public class GeneralWithOneLevelTest extends FunctionTemplateResolutionTest {

    @Test
    public void testNumberOfSubcalls() throws TemplatorException {
        firstStatementInMain.searchSubNames(loadingProgress);
        Assert.assertEquals(0, firstStatementInMain.getSubNames().size());
    }

    @Test
    public void testNumberOfResolvedSubcallsStaysZero() throws TemplatorException {
        int numberOfResolvedSubcallsBefore = getOnlyFunctionCallSubstatements(firstStatementInMain).size();
        Assert.assertEquals(0, numberOfResolvedSubcallsBefore);

        firstStatementInMain.searchSubNames(loadingProgress);

        int numberOfResolvedSubcallsAfter = firstStatementInMain.getSubNames().size();
        Assert.assertEquals(0, numberOfResolvedSubcallsAfter);
    }

    public void testResolvedSubcallsNotNull() {
        assertTrue(firstStatementInMain.getSubNames() != null);
    }

}
