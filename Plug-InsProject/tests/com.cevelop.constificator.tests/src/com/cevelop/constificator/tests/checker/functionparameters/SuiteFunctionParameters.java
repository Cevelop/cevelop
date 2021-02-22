package com.cevelop.constificator.tests.checker.functionparameters;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.constificator.tests.checker.functionparameters.builtin.SuiteBuiltin;
import com.cevelop.constificator.tests.checker.functionparameters.classtype.SuiteClassType;
import com.cevelop.constificator.tests.checker.functionparameters.edgecase.SuiteEdgecase;


@RunWith(Suite.class)
@SuiteClasses({
    //@formatter:off

    SuiteBuiltin.class,
    SuiteClassType.class,
    SuiteEdgecase.class,
//    GH56_ReferenceParameterPassedToConstructor.class,
    GL91_ConstQualifiedLambdaParameter.class,
    GL92_PassedToConstructorViaNonConstReference.class,
    GL93_ParameterWithAliasedTypeAndQualifiers.class,
    GL103_NonConstPointerPassedToConstructor.class,
    GL113_OverloadedOperatorWithLHSByCopy.class,

    //@formatter:on
})
public class SuiteFunctionParameters {

}
