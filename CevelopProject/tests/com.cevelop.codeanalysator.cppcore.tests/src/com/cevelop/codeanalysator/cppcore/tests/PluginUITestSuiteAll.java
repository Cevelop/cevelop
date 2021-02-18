package com.cevelop.codeanalysator.cppcore.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.codeanalysator.cppcore.tests.checker.AlwaysInitializeAnObjectCheckerTest;
import com.cevelop.codeanalysator.cppcore.tests.checker.AvoidConversionOperatorsCheckerTest;
import com.cevelop.codeanalysator.cppcore.tests.checker.AvoidLossyFloatingPointConversionsCheckerTest;
import com.cevelop.codeanalysator.cppcore.tests.checker.AvoidLossyFloatingPointFunctionArgumentConversionsCheckerTest;
import com.cevelop.codeanalysator.cppcore.tests.checker.AvoidLossyFloatingPointToIntegerConversionsCheckerTest;
import com.cevelop.codeanalysator.cppcore.tests.checker.AvoidLossyFloatingPointToIntegerFunctionArgumentConversionsCheckerTest;
import com.cevelop.codeanalysator.cppcore.tests.checker.AvoidLossyIntToCharBigConversionsCheckerTest;
import com.cevelop.codeanalysator.cppcore.tests.checker.AvoidLossyIntToCharBigFunctionArgumentConversionsCheckerTest;
import com.cevelop.codeanalysator.cppcore.tests.checker.AvoidLossyIntToCharSmallConversionsCheckerTest;
import com.cevelop.codeanalysator.cppcore.tests.checker.AvoidLossyIntToCharSmallFunctionArgumentConversionsCheckerTest;
import com.cevelop.codeanalysator.cppcore.tests.checker.AvoidLossyIntegerConversionsCheckerTest;
import com.cevelop.codeanalysator.cppcore.tests.checker.AvoidLossyIntegerFunctionArgumentConversionsCheckerTest;
import com.cevelop.codeanalysator.cppcore.tests.checker.AvoidLossySignedToUnsignedConversionsCheckerTest;
import com.cevelop.codeanalysator.cppcore.tests.checker.DeclareLoopVariableInTheInitializerCheckerTest;
import com.cevelop.codeanalysator.cppcore.tests.checker.DestructorHasNoBodyCheckerTest;
import com.cevelop.codeanalysator.cppcore.tests.checker.DestructorWithMissingDeleteStatementsCheckerTest;
import com.cevelop.codeanalysator.cppcore.tests.checker.DontUseVariableForTwoUnrelatedPurposesCheckerTest;
import com.cevelop.codeanalysator.cppcore.tests.checker.MissingSpecialMemberFunctionsCheckerTest;
import com.cevelop.codeanalysator.cppcore.tests.checker.NoDestructorCheckerTest;
import com.cevelop.codeanalysator.cppcore.tests.checker.RedundantOperationsCheckerTest;
import com.cevelop.codeanalysator.cppcore.tests.quickfix.DeclareLoopVariableInTheInitializerQuickFixTest;


@RunWith(Suite.class)
@SuiteClasses({
   // @formatter:off
   AlwaysInitializeAnObjectCheckerTest.class,
   AvoidConversionOperatorsCheckerTest.class,
   DeclareLoopVariableInTheInitializerCheckerTest.class,
   MissingSpecialMemberFunctionsCheckerTest.class,
   RedundantOperationsCheckerTest.class,
   AvoidLossyFloatingPointConversionsCheckerTest.class,
   AvoidLossyFloatingPointFunctionArgumentConversionsCheckerTest.class,
   AvoidLossyFloatingPointToIntegerConversionsCheckerTest.class,
   AvoidLossyFloatingPointToIntegerFunctionArgumentConversionsCheckerTest.class,
   AvoidLossyIntegerConversionsCheckerTest.class,
   AvoidLossyIntegerFunctionArgumentConversionsCheckerTest.class,
   AvoidLossyIntToCharBigConversionsCheckerTest.class,
   AvoidLossyIntToCharBigFunctionArgumentConversionsCheckerTest.class,
   AvoidLossyIntToCharSmallConversionsCheckerTest.class,
   AvoidLossyIntToCharSmallFunctionArgumentConversionsCheckerTest.class,
   AvoidLossySignedToUnsignedConversionsCheckerTest.class,
   NoDestructorCheckerTest.class,
   DestructorHasNoBodyCheckerTest.class,
   DestructorWithMissingDeleteStatementsCheckerTest.class,
   DontUseVariableForTwoUnrelatedPurposesCheckerTest.class,
   DeclareLoopVariableInTheInitializerQuickFixTest.class,
	// @formatter:on
})
public class PluginUITestSuiteAll {}
