package com.cevelop.templator.tests.integrationtest.resolution.auto;

import org.junit.Test;

import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.TemplateArgument;
import com.cevelop.templator.tests.TemplatorMultiLevelResolutionTest;


public class AutoPrimaryTest extends TemplatorMultiLevelResolutionTest {

    @Test
    public void firstStatementResolvesToFirstDefinition() throws TemplatorException {
        statementMatchesExpectedDefinition();
    }

    @Test
    public void testArgumentMapIs_int_int() throws TemplatorException {
        testArgumentMap(TemplateArgument.INT, TemplateArgument.INT);
    }
}
