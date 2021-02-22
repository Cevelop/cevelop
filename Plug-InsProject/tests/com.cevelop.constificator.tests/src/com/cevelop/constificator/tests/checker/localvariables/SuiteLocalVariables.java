package com.cevelop.constificator.tests.checker.localvariables;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.constificator.tests.checker.localvariables.builtin.SuiteBuiltin;
import com.cevelop.constificator.tests.checker.localvariables.classtype.SuiteClassType;
import com.cevelop.constificator.tests.checker.localvariables.edgecase.SuiteEdgecase;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off

    SuiteBuiltin.class,
    SuiteClassType.class,
    SuiteEdgecase.class,
    GL92_PassedToConstructorViaNonConstReference.class,
    GL103_NonConstPointerPassedToConstructor.class,
    GL113_OverloadedOperatorWithLHSByCopy.class,

//@formatter:on
})
public class SuiteLocalVariables {

}
