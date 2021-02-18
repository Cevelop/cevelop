package com.cevelop.charwars.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.charwars.tests.checkers.ArrayCheckerTest;
import com.cevelop.charwars.tests.checkers.CStrCheckerTest;
import com.cevelop.charwars.tests.checkers.CStringAliasCheckerTest;
import com.cevelop.charwars.tests.checkers.CStringCheckerTest;
import com.cevelop.charwars.tests.checkers.CStringCleanupCheckerTest;
import com.cevelop.charwars.tests.checkers.CStringParameterCheckerTest;
import com.cevelop.charwars.tests.checkers.PointerParameterCheckerTest;
import com.cevelop.charwars.tests.quickfixes.ArrayQuickFixTest;
import com.cevelop.charwars.tests.quickfixes.CStrQuickFixTest;
import com.cevelop.charwars.tests.quickfixes.PointerParameterQuickFixTest;
import com.cevelop.charwars.tests.quickfixes.cstring.cleanup.CStringCleanupQuickFixTest;
import com.cevelop.charwars.tests.quickfixes.cstring.general.CStringAliasQuickFixTest;
import com.cevelop.charwars.tests.quickfixes.cstring.general.CStringQuickFixTest;
import com.cevelop.charwars.tests.quickfixes.cstring.general.refactorings.ConvertingFunctionTest;
import com.cevelop.charwars.tests.quickfixes.cstring.general.refactorings.MemchrTest;
import com.cevelop.charwars.tests.quickfixes.cstring.general.refactorings.MemcmpTest;
import com.cevelop.charwars.tests.quickfixes.cstring.general.refactorings.MemcpyTest;
import com.cevelop.charwars.tests.quickfixes.cstring.general.refactorings.MemmoveTest;
import com.cevelop.charwars.tests.quickfixes.cstring.general.refactorings.StrcatTest;
import com.cevelop.charwars.tests.quickfixes.cstring.general.refactorings.StrchrTest;
import com.cevelop.charwars.tests.quickfixes.cstring.general.refactorings.StrcmpTest;
import com.cevelop.charwars.tests.quickfixes.cstring.general.refactorings.StrcpyTest;
import com.cevelop.charwars.tests.quickfixes.cstring.general.refactorings.StrcspnTest;
import com.cevelop.charwars.tests.quickfixes.cstring.general.refactorings.StrdupTest;
import com.cevelop.charwars.tests.quickfixes.cstring.general.refactorings.StrlenTest;
import com.cevelop.charwars.tests.quickfixes.cstring.general.refactorings.StrncatTest;
import com.cevelop.charwars.tests.quickfixes.cstring.general.refactorings.StrncmpTest;
import com.cevelop.charwars.tests.quickfixes.cstring.general.refactorings.StrncpyTest;
import com.cevelop.charwars.tests.quickfixes.cstring.general.refactorings.StrpbrkTest;
import com.cevelop.charwars.tests.quickfixes.cstring.general.refactorings.StrrchrTest;
import com.cevelop.charwars.tests.quickfixes.cstring.general.refactorings.StrspnTest;
import com.cevelop.charwars.tests.quickfixes.cstring.general.refactorings.StrstrTest;
import com.cevelop.charwars.tests.quickfixes.cstring.parameter.CStringParameterQuickFixTest;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
	ArrayCheckerTest.class,
	CStringCheckerTest.class,
	CStringAliasCheckerTest.class,
	CStringCleanupCheckerTest.class,
	CStrCheckerTest.class,
	PointerParameterCheckerTest.class,
	CStringParameterCheckerTest.class,
	ArrayQuickFixTest.class,
	CStringQuickFixTest.class,
	CStringAliasQuickFixTest.class,
	CStringCleanupQuickFixTest.class,
	CStrQuickFixTest.class,
	PointerParameterQuickFixTest.class,
	CStringParameterQuickFixTest.class,
	StrlenTest.class,
	StrcmpTest.class,
	StrncmpTest.class,
	MemcmpTest.class,
	StrcatTest.class,
	StrncatTest.class,
	StrcpyTest.class,
	StrncpyTest.class,
	MemcpyTest.class,
	MemmoveTest.class,
	MemchrTest.class,
	StrchrTest.class,
	StrrchrTest.class,
	StrstrTest.class,
	StrcspnTest.class,
	StrspnTest.class,
	StrdupTest.class,
	StrpbrkTest.class,
	ConvertingFunctionTest.class
//@formatter:on
})
public class PluginUITestSuiteAll {}
