package com.cevelop.ctylechecker.tests.quickfix;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.ctylechecker.tests.quickfix.dynamic.DynamicStyleResolutionTest;
import com.cevelop.ctylechecker.tests.quickfix.includes.MissingStandardIncludeQuickfixTest;


@RunWith(Suite.class)
@SuiteClasses({ //
                MissingStandardIncludeQuickfixTest.class, //
                DynamicStyleResolutionTest.class//
})
public class QuickFixTests {

}
