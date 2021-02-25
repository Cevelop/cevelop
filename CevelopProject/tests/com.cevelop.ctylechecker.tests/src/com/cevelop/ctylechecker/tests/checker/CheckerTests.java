package com.cevelop.ctylechecker.tests.checker;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.ctylechecker.tests.checker.classes.ExplicitConstructorCheckerTest;
import com.cevelop.ctylechecker.tests.checker.classes.MemberInitializerCheckerTest;
import com.cevelop.ctylechecker.tests.checker.classes.RedundantAccessSpecifierTest;
import com.cevelop.ctylechecker.tests.checker.cute.AssertCheckerTest;
import com.cevelop.ctylechecker.tests.checker.global.GlobalNonConstVariableCheckerTest;
import com.cevelop.ctylechecker.tests.checker.header.IncludeGuardCheckerTest;
import com.cevelop.ctylechecker.tests.checker.header.UsingCheckerTest;
import com.cevelop.ctylechecker.tests.checker.includes.MissingSelfIncludeCheckerTest;
import com.cevelop.ctylechecker.tests.checker.includes.MissingStandardIncludeCheckerTest;
import com.cevelop.ctylechecker.tests.checker.includes.NonSystemIncludesFirstCheckerTest;
import com.cevelop.ctylechecker.tests.checker.includes.SelfIncludePositionCheckerTest;
import com.cevelop.ctylechecker.tests.checker.includes.SuperfluousStandardIncludeCheckerTest;
import com.cevelop.ctylechecker.tests.checker.io.CinCoutCheckerTest;
import com.cevelop.ctylechecker.tests.checker.io.IostreamCheckerTest;


@RunWith(Suite.class)
@SuiteClasses({ //
                AssertCheckerTest.class, //
                GlobalNonConstVariableCheckerTest.class, //
                IncludeGuardCheckerTest.class, //
                UsingCheckerTest.class, //
                MissingStandardIncludeCheckerTest.class, //
                MissingSelfIncludeCheckerTest.class, //
                NonSystemIncludesFirstCheckerTest.class, //
                SelfIncludePositionCheckerTest.class, //
                SuperfluousStandardIncludeCheckerTest.class, //
                IostreamCheckerTest.class, //
                CinCoutCheckerTest.class, //
                MemberInitializerCheckerTest.class, //
                ExplicitConstructorCheckerTest.class, //
                RedundantAccessSpecifierTest.class //
})
public class CheckerTests {

}
