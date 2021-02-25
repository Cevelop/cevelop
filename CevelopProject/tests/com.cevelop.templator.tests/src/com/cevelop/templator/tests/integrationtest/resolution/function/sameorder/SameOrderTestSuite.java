package com.cevelop.templator.tests.integrationtest.resolution.function.sameorder;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
    //@formatter:off
    OverloadedFunctionWithTemplateArgumentTest.class,
    TwoDeducedArgumentsTest.class,
    TemplateIdAndDeducedArgumentTest.class,
    TemplateIdAndDefaultArgumentTest.class,
    TemplateIdAndDeducedAndDefaultArgumentTest.class,
    ResolvesToExactlyMatchingNonTemplateWithoutTemplateIdTest.class,
    CompleteTemplateIdWithTwoArgumentsTest.class,
    CompleteTemplateIdWithoutArgumentsTest.class,
    CompleteDefaultArgumentsAndEmptyTemplateIdWithoutArgumentsTest.class,
    CompleteDefaultArgumentsAndEmptyTemplateIdWithArgumentsTest.class,
    //@formatter:on
})
/**
 * Tests for function call resolutions where the functions parameters have the same declaration
 * order as the template parameters in the template declaration.
 */
public class SameOrderTestSuite {}
