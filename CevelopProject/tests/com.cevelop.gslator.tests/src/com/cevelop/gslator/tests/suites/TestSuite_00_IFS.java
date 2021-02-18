package com.cevelop.gslator.tests.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.gslator.tests.tests.checkers.ES05ToES34DeclarationRules.ES20AlwaysInitializeAnObjectCheckerTest;
import com.cevelop.gslator.tests.tests.quickfixes.ES05ToES34DeclarationRules.ES20AlwaysInitializeAnObjectQuickFixTest;


@RunWith(Suite.class)
@SuiteClasses({
   // @formatter:off
	ES20AlwaysInitializeAnObjectCheckerTest.class,
	ES20AlwaysInitializeAnObjectQuickFixTest.class
	// @formatter:on
})
public class TestSuite_00_IFS {}
