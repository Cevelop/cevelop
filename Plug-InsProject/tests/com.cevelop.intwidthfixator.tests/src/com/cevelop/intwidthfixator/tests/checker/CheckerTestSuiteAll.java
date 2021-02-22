package com.cevelop.intwidthfixator.tests.checker;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
	CastsNegativeTest.class,
	CastsTest.class,
	FunctionsNegativeTest.class,
	FunctionsTest.class,
	TemplatesNegativeTest.class,
	TemplatesTest.class,
	TypedefUsingNegativeTest.class,
	TypedefUsingTest.class,
	VariablesNegativeTest.class,
	VariablesTest.class,
})
public class CheckerTestSuiteAll {

}
