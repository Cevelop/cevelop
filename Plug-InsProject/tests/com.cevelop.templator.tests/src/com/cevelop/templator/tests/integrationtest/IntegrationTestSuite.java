package com.cevelop.templator.tests.integrationtest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.templator.tests.integrationtest.resolution.auto.AutoResolutionIntegrationTestSuite;
import com.cevelop.templator.tests.integrationtest.resolution.function.FunctionResolutionIntegrationTestSuite;
import com.cevelop.templator.tests.integrationtest.resolution.nontemplate.NonTemplateResolutionIntegrationTestSuite;
import com.cevelop.templator.tests.integrationtest.resolution.template.TemplateResolutionIntegrationTestSuite;
import com.cevelop.templator.tests.integrationtest.view.tree.TreeViewIntegrationTestSuite;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
    FunctionResolutionIntegrationTestSuite.class,
    TemplateResolutionIntegrationTestSuite.class,
    AutoResolutionIntegrationTestSuite.class,
    NonTemplateResolutionIntegrationTestSuite.class,
    TreeViewIntegrationTestSuite.class
    //@formatter:on
})
public class IntegrationTestSuite {}
