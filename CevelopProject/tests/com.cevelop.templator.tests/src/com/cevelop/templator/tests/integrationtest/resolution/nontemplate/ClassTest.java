package com.cevelop.templator.tests.integrationtest.resolution.nontemplate;

import org.junit.Test;

import com.cevelop.templator.tests.TemplatorResolutionTest;


public class ClassTest extends TemplatorResolutionTest {

    @Test
    public void resolvesToFirstDefinition() {
        firstStatementInMainResolvesToFirstDefinition();
    }
}
