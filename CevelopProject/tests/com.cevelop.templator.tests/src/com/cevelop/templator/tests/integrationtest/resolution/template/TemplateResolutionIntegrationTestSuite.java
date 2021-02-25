package com.cevelop.templator.tests.integrationtest.resolution.template;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
    ExplicitClassTemplateSpecializationTest.class,
    PartialClassTemplateSpecializationTest.class,
    SimpleClassTemplateInstantiation.class,
    AliasTemplateTest.class,
    VariableTemplateTest.class,
    TemplateTemplateParameterTest.class,
    OperatorTest.class
    //@formatter:on
})
public class TemplateResolutionIntegrationTestSuite {}
