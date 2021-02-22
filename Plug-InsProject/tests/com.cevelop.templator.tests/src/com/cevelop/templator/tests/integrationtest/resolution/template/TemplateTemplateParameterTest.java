package com.cevelop.templator.tests.integrationtest.resolution.template;

import org.junit.Test;

import com.cevelop.templator.plugin.asttools.data.AbstractResolvedNameInfo;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.TemplatorMultiLevelResolutionTest;


public class TemplateTemplateParameterTest extends TemplatorMultiLevelResolutionTest {

    @Test
    public void resolvesToExpectedDefinition_1() throws TemplatorException {
        statementMatchesExpectedDefinition(getSubnameInMain(0).getDefinition(), 2);
        statementMatchesExpectedDefinition(getSubnameInMain(0).getDefinition(), 2);
    }

    @Test
    public void resolvesToExpectedDefinition_2() throws TemplatorException {
        AbstractResolvedNameInfo info = getSubname(getSubnameInMain(0), 0);
        statementMatchesExpectedDefinition(info.getDefinition(), 0);
    }

    @Test
    public void resolvesToExpectedDefinition_3() throws TemplatorException {
        AbstractResolvedNameInfo info = getSubname(getSubnameInMain(0), 1);
        statementMatchesExpectedDefinition(info.getDefinition(), 0);
    }

    @Test
    public void resolvesToExpectedDefinition_4() throws TemplatorException {
        AbstractResolvedNameInfo info = getSubname(getSubnameInMain(0), 2);
        statementMatchesExpectedDefinition(info.getDefinition(), 1);
    }

    @Test
    public void resolvesToExpectedDefinition_5() throws TemplatorException {
        AbstractResolvedNameInfo info = getSubname(getSubnameInMain(0), 3);
        statementMatchesExpectedDefinition(info.getDefinition(), 1);
    }
}
