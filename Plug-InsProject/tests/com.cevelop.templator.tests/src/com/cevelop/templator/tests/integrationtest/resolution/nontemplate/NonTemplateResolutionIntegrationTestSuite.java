package com.cevelop.templator.tests.integrationtest.resolution.nontemplate;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
    ClassTest.class,
    LambdaTest.class
    //@formatter:on
})
public class NonTemplateResolutionIntegrationTestSuite {}
