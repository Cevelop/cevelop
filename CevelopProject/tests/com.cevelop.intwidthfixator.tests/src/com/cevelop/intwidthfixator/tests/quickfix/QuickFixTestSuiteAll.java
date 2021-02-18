package com.cevelop.intwidthfixator.tests.quickfix;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
	CastsQFTest.class,
	FunctionsQFTest.class,
	SettingsQFTest.class,
	TemplatesQFTest.class,
	TypedefUsingQFTest.class,
	VariablesQFTest.class,
})
public class QuickFixTestSuiteAll {

}
