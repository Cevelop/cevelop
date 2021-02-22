package com.cevelop.conanator.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.conanator.tests.parser.AllParserTests;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
	AllParserTests.class,
//@formatter:on
})
public class PluginUITestSuiteAll {

}
