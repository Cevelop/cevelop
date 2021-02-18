package com.cevelop.constificator.tests.checker.functionparameters.edgecase;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
    //@formatter:off

    ConstructorArguments.class,
    ShiftOperators.class,
    UnnamedParameters.class,
    MoveSpecialMembers.class,
//    FunctionParamTypeAlias.class,

    //@formatter:on
})
public class SuiteEdgecase {

}
