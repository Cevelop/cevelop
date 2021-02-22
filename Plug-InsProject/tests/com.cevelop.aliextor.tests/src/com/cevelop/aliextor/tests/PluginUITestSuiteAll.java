package com.cevelop.aliextor.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.aliextor.tests.tests.AliasTemplateTest;
import com.cevelop.aliextor.tests.tests.FunctionRefactoringTest;
import com.cevelop.aliextor.tests.tests.PartialSelectionRefactoringTest;
import com.cevelop.aliextor.tests.tests.SimpleRefactoringTest;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
	SimpleRefactoringTest.class,
	FunctionRefactoringTest.class,
	PartialSelectionRefactoringTest.class,
	AliasTemplateTest.class
//@formatter:on
})
public class PluginUITestSuiteAll {}
