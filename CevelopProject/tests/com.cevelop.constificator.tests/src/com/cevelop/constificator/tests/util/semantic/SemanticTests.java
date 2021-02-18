package com.cevelop.constificator.tests.util.semantic;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.constificator.tests.util.semantic.expression.ExpressionTests;
import com.cevelop.constificator.tests.util.semantic.function.FunctionTests;
import com.cevelop.constificator.tests.util.semantic.memberfunction.MemberFunctionTests;
import com.cevelop.constificator.tests.util.semantic.type.TypeTests;
import com.cevelop.constificator.tests.util.semantic.variable.VariableTests;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
    VariableTests.class,
    FunctionTests.class,
    MemberFunctionTests.class,
    TypeTests.class,
    ExpressionTests.class,
//@formatter:on
})
public class SemanticTests {

}
