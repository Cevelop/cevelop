package com.cevelop.clonewar.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.clonewar.tests.tests.ExtractClassTemplateTest;
import com.cevelop.clonewar.tests.tests.ExtractFunctionTemplateTest;


@RunWith(Suite.class)
@SuiteClasses({ ExtractFunctionTemplateTest.class, ExtractClassTemplateTest.class })
public class PluginUITestSuiteAll {

}
