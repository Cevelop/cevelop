package com.cevelop.templator.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.templator.tests.asttests.PluginUITestSuiteAST;
import com.cevelop.templator.tests.integrationtest.PluginUITestSuiteIntegration;
import com.cevelop.templator.tests.testhelpertest.PluginUITestSuiteHelper;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
    PluginUITestSuiteAST.class,
    PluginUITestSuiteHelper.class,
    PluginUITestSuiteIntegration.class
    //@formatter:on
})
public class PluginUITestSuiteAll {}
