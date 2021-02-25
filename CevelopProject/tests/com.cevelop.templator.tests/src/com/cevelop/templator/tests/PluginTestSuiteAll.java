package com.cevelop.templator.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.templator.tests.asttests.PluginTestSuiteAST;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
    PluginTestSuiteAST.class,
    //@formatter:on
})
public class PluginTestSuiteAll {}
