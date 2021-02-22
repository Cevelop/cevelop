package com.cevelop.intwidthfixator.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.intwidthfixator.tests.checker.CheckerTestSuiteAll;
import com.cevelop.intwidthfixator.tests.quickfix.QuickFixTestSuiteAll;
import com.cevelop.intwidthfixator.tests.refactoring.RefactoringTestSuiteAll;


@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
	CheckerTestSuiteAll.class,
	QuickFixTestSuiteAll.class,
	RefactoringTestSuiteAll.class,
})
public class PluginUITestSuiteAll {

}
