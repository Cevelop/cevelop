package com.cevelop.intwidthfixator.tests.refactoring;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.intwidthfixator.tests.refactoring.conversion.ConversionRefactoringTestSuiteAll;
import com.cevelop.intwidthfixator.tests.refactoring.inversion.InversionRefactoringTestSuiteAll;


@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
	ConversionRefactoringTestSuiteAll.class,
	InversionRefactoringTestSuiteAll.class,
})
public class RefactoringTestSuiteAll {

}
