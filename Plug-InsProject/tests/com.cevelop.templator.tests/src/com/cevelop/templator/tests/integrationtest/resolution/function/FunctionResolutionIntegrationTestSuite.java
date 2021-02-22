package com.cevelop.templator.tests.integrationtest.resolution.function;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.templator.tests.integrationtest.resolution.function.differentorder.DifferentOrderTestSuite;
import com.cevelop.templator.tests.integrationtest.resolution.function.sameorder.OverloadedFunctionWithTemplateArgumentTest;
import com.cevelop.templator.tests.integrationtest.resolution.function.sameorder.SameOrderTestSuite;


@RunWith(Suite.class)
@SuiteClasses({
    //@formatter:off
    SameOrderTestSuite.class,
    DifferentOrderTestSuite.class,
    GeneralWithOneLevelTest.class,
    GeneralWithTwoLevelTest.class,
    OneLevelWithOneDeducedArgumentTest.class,
    OneLevelWithOneTemplateIdArgumentTest.class,
    OverloadedFunctionWithTemplateArgumentTest.class,
    ClassKeywordInsteadOfTypename.class,
    FunctionThatIsInNamespaceResolutionTest.class,
    PassAliasesAsArgumentsTest.class,
    PassLiteralsAsArgumentsTest.class,
    PassNormalFunctionsAsArgumentsTest.class,
    PassNormalParametersAsArgumentsTest.class,
    PassOwnTypesAsArgumentsTest.class,
    PassVariablesAsArgumentsTest.class,
    PointerTest.class,
    ReferenceTest.class,
    //@formatter:on
})
/**
 * This tests have a separate test suite for the future, because resolving class template
 * tests will have their own testsuite in the future and it is easier to seperate them now
 * instead of renaming everything later. This allows for much shorter file and testclass names because
 * the a part of the description is already in the package name.
 */
public class FunctionResolutionIntegrationTestSuite {}
