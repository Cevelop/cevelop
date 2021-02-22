package com.cevelop.constificator.tests.quickfix.localvariables;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
    LocalVariables_Auto.class,
    LocalVariables_Pointer.class,
    LocalVariables_Simple.class,
//@formatter:on
})
public class LocalVariablesSuite {

}
