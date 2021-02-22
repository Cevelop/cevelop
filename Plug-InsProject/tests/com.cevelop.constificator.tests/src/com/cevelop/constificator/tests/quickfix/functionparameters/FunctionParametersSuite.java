package com.cevelop.constificator.tests.quickfix.functionparameters;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
    FreeFunctions_MultipleFiles.class,
    FreeFunctions_SingleFile.class,
    FreeFunctions_SingleFileWithUnrelatedFile.class,
    MemberFunctions_MultipleFiles.class,
	MemberFunctions_SingleFile.class,
//@formatter:on
})
public class FunctionParametersSuite {

}
