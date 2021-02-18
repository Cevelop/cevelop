package com.cevelop.templator.tests.integrationtest.resolution.nontemplate;

import org.junit.Test;

import com.cevelop.templator.plugin.asttools.data.AbstractResolvedNameInfo;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.TemplatorMultiLevelResolutionTest;


public class LambdaTest extends TemplatorMultiLevelResolutionTest {

    @Test
    public void resolvesToFirstDefinition() throws TemplatorException {
        AbstractResolvedNameInfo info = getSubnameInMain(0);
        statementMatchesExpectedDefinition(info.getDefinition(), 0);
    }
}
