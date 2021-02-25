package com.cevelop.constificator.tests.util.semantic.expression;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
    IsDereferencedNTimes.class,
    IsResolvedToInstance.class,
    IsArrayElementAccess_OneDimensionalArray.class,
//@formatter:on
})
public class ExpressionTests {

}
