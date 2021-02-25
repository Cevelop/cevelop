package com.cevelop.constificator.tests.util.semantic.memberfunction;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
    IsConstTest.class,
    OverridesTest.class,
    ShadowsTest.class,
    GetOverriddenTest.class,
    GetShadowedTest.class,
//@formatter:on
})
public class MemberFunctionTests {

}
