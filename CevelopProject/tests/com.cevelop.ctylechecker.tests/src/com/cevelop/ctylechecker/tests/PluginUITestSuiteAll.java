package com.cevelop.ctylechecker.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.ctylechecker.tests.checker.CheckerTests;
import com.cevelop.ctylechecker.tests.quickfix.QuickFixTests;


@RunWith(Suite.class)
@SuiteClasses({ //
                CheckerTests.class, //
                QuickFixTests.class,//
})
public class PluginUITestSuiteAll {

}
