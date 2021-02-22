package com.cevelop.constificator.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.constificator.tests.checker.CheckerTests;
import com.cevelop.constificator.tests.quickfix.QuickFixTests;
import com.cevelop.constificator.tests.util.UtilityTests;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
    QuickFixTests.class,
	CheckerTests.class,
	UtilityTests.class
//@formatter:on
})
public class PluginUITestSuiteAll {

}
