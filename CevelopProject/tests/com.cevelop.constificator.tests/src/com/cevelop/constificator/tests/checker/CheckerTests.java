package com.cevelop.constificator.tests.checker;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.constificator.tests.checker.functionparameters.SuiteFunctionParameters;
import com.cevelop.constificator.tests.checker.localvariables.SuiteLocalVariables;
import com.cevelop.constificator.tests.checker.memberfunctions.SuiteMemberFunctions;
import com.cevelop.constificator.tests.checker.membervariables.SuiteMemberVariables;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
    SuiteLocalVariables.class,
    SuiteFunctionParameters.class,
    SuiteMemberFunctions.class,
    SuiteMemberVariables.class
//@formatter:on
})
public class CheckerTests {

}
