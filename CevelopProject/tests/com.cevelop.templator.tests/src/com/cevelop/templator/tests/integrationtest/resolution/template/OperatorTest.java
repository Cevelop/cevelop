package com.cevelop.templator.tests.integrationtest.resolution.template;

import org.junit.Test;

import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.TemplateArgument;
import com.cevelop.templator.tests.TemplatorMultiLevelResolutionTest;


public class OperatorTest extends TemplatorMultiLevelResolutionTest {

    @Test
    public void rectangleInMainResolvesToDefinition() throws TemplatorException {
        statementMatchesExpectedDefinition();
    }

    @Test
    public void rectangleInMainHasCorrectTemplateArguments() throws TemplatorException {
        testArgumentMap(TemplateArgument.INT, TemplateArgument.INT);
    }
}
