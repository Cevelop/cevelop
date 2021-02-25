package com.cevelop.intwidthfixator.tests.refactoring.inversion;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
	InversionRefactoringAllTest.class,
	InversionRefactoringNeitherTest.class,
	InversionRefactoringInt8Test.class,
	InversionRefactoringInt16Test.class,
	InversionRefactoringInt32Test.class,
	InversionRefactoringInt64Test.class,
	InversionRefactoringSignedTest.class,
	InversionRefactoringUnsignedTest.class,
})
public class InversionRefactoringTestSuiteAll {

}
