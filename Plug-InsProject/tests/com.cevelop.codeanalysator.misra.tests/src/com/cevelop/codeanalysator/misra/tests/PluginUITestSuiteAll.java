package com.cevelop.codeanalysator.misra.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.codeanalysator.misra.tests.checker.AvoidLossyFloatingPointConversionsCheckerTest;
import com.cevelop.codeanalysator.misra.tests.checker.AvoidLossyFloatingPointFunctionArgumentConversionsCheckerTest;
import com.cevelop.codeanalysator.misra.tests.checker.AvoidLossyFloatingPointToIntegerConversionsCheckerTest;
import com.cevelop.codeanalysator.misra.tests.checker.AvoidLossyFloatingPointToIntegerFunctionArgumentConversionsCheckerTest;
import com.cevelop.codeanalysator.misra.tests.checker.AvoidLossyIntToCharBigConversionsCheckerTest;
import com.cevelop.codeanalysator.misra.tests.checker.AvoidLossyIntToCharBigFunctionArgumentConversionsCheckerTest;
import com.cevelop.codeanalysator.misra.tests.checker.AvoidLossyIntToCharSmallConversionsCheckerTest;
import com.cevelop.codeanalysator.misra.tests.checker.AvoidLossyIntToCharSmallFunctionArgumentConversionsCheckerTest;
import com.cevelop.codeanalysator.misra.tests.checker.AvoidLossyIntegerConversionsCheckerTest;
import com.cevelop.codeanalysator.misra.tests.checker.AvoidLossyIntegerFunctionArgumentConversionsCheckerTest;
import com.cevelop.codeanalysator.misra.tests.checker.AvoidLossySignedToUnsignedConversionsCheckerTest;
import com.cevelop.codeanalysator.misra.tests.checker.BoolExpressionOperandsInfoCheckerTest;
import com.cevelop.codeanalysator.misra.tests.checker.BoolExpressionOperandsWarnCheckerTest;
import com.cevelop.codeanalysator.misra.tests.checker.DeclareLoopVariableInTheInitializerCheckerTest;
import com.cevelop.codeanalysator.misra.tests.quickfix.DeclareLoopVariableInTheInitializerQuickFixTest;


@RunWith(Suite.class)
@SuiteClasses({
   // @formatter:off
   DeclareLoopVariableInTheInitializerCheckerTest.class,
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
   BoolExpressionOperandsWarnCheckerTest.class,
   BoolExpressionOperandsInfoCheckerTest.class,
   DeclareLoopVariableInTheInitializerQuickFixTest.class,
	// @formatter:on
})
public class PluginUITestSuiteAll {}
