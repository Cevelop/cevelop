package com.cevelop.templator.tests.integrationtest.resolution.function;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.templator.plugin.asttools.data.AbstractResolvedNameInfo;
import com.cevelop.templator.plugin.asttools.templatearguments.TemplateArgumentMap;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.FunctionTemplateResolutionTest;


public class GeneralWithTwoLevelTest extends FunctionTemplateResolutionTest {

    @Test
    public void testNoAutomaticResolvedSubcalls() throws TemplatorException {
        int numberOfResolvedSubcallsBefore = firstStatementInMain.getSubNames().size();
        Assert.assertEquals(0, numberOfResolvedSubcallsBefore);

        firstStatementInMain.searchSubNames(loadingProgress);

        int numberOfResolvedSubcallsAfter = firstStatementInMain.getSubNames().size();
        Assert.assertEquals(1, numberOfResolvedSubcallsAfter);
    }

    @Test
    public void testArgumentMapFromSubcallIsEmptyAndNotNull() throws TemplatorException {
        firstStatementInMain.searchSubNames(loadingProgress);

        AbstractResolvedNameInfo subcall = firstStatementInMain.getSubNames().get(0).getInfo();
        TemplateArgumentMap mapFromSubcall = subcall.getTemplateArgumentMap();

        assertEquals(new TemplateArgumentMap(), mapFromSubcall);
    }

}
