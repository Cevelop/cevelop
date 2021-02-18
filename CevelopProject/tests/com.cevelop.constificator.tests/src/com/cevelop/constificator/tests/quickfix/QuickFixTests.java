package com.cevelop.constificator.tests.quickfix;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.constificator.tests.quickfix.functionparameters.FunctionParametersSuite;
import com.cevelop.constificator.tests.quickfix.localvariables.LocalVariablesSuite;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
    LocalVariablesSuite.class,
    FunctionParametersSuite.class
//@formatter:on
})
public class QuickFixTests {

}
