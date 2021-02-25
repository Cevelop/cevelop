package com.cevelop.templator.tests.integrationtest.resolution.auto;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
	AutoUnclickableTest.class,
	AutoPrimaryTest.class,
	AutoFromFunctionCallTest.class,
	AutoMixedTest.class,
	AutoSpecializationTest.class
//@formatter:on
})
public class AutoResolutionIntegrationTestSuite {}
