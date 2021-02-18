package com.cevelop.templator.tests.integrationtest.resolution.template;

import org.junit.Test;

import com.cevelop.templator.tests.TemplateArgument;
import com.cevelop.templator.tests.TemplatorResolutionTest;


public class AliasTemplateTest extends TemplatorResolutionTest {

    @Test
    public void resolvesToExpectedDefinition() {
        firstStatementInMainMatchesExpectedDefinition();
    }

    @Test
    public void testArgumentMapIs_int() {
        testArgumentMap(TemplateArgument.INT);
    }
}
