package com.cevelop.constificator.tests.util.semantic.variable;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
    IsUsedAsVariadicInVarargsFunctionTest.class,
    IsUsedInCallToDeferredFunctionTest.class,
    IsUsedInCallToTemplateFunctionTest.class,
//@formatter:on
})
public class VariableTests {

}
