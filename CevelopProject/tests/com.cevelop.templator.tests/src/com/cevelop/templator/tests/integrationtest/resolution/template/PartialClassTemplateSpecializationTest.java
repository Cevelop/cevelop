package com.cevelop.templator.tests.integrationtest.resolution.template;

import org.junit.Test;

import com.cevelop.templator.tests.TemplatorResolutionTest;


public class PartialClassTemplateSpecializationTest extends TemplatorResolutionTest {

    @Test
    public void resolvesToFirstDefinition() {
        firstStatementInMainResolvesToDefinition(definitions.get(1));
    }
}
