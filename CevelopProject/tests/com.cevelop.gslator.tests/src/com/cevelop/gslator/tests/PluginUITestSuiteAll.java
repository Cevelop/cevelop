package com.cevelop.gslator.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.gslator.tests.suites.TestSuite_00_IFS;
import com.cevelop.gslator.tests.suites.TestSuite_01_BA2016;
import com.cevelop.gslator.tests.suites.TestSuite_02_SA2016;
import com.cevelop.gslator.tests.suites.TestSuite_03_BA2017;


@RunWith(Suite.class)
@SuiteClasses({
   // @formatter:off
	TestSuite_00_IFS.class, //IFS internal extension
	TestSuite_01_BA2016.class, //Kaya, Schmidiger
	TestSuite_02_SA2016.class, //Bislin, Diener
	TestSuite_03_BA2017.class //Bislin, Diener
	// @formatter:on
})
public class PluginUITestSuiteAll {}
