package com.cevelop.constificator.tests.checker.localvariables.edgecase;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
    ConstexprVariables.class,
    ReadonlyAccessToMembers.class,
    ReturnMovePessimization.class,
    RangeBasedFor.class,
//    LocalVariableTypeAlias.class,
//@formatter:on
})
public class SuiteEdgecase {

}
