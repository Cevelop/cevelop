package com.cevelop.codeanalysator.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.codeanalysator.core.tests.refactoring.OverriderRefactoringTest;
import com.cevelop.codeanalysator.core.tests.refactoring.StructClassSwitcherRefactoringTest;
import com.cevelop.codeanalysator.core.tests.unittests.GuidelinePreferencesImplTest;
import com.cevelop.codeanalysator.core.tests.unittests.GuidelinePriorityResolverImplTest;
import com.cevelop.codeanalysator.core.tests.unittests.RuleSuppressionTest;
import com.cevelop.codeanalysator.core.tests.visitor.VisitorCompositeTest;

@RunWith(Suite.class)
@SuiteClasses({
	// @formatter:off
   VisitorCompositeTest.class,
   RuleSuppressionTest.class,
   GuidelinePreferencesImplTest.class,
   GuidelinePriorityResolverImplTest.class,
   OverriderRefactoringTest.class,
   StructClassSwitcherRefactoringTest.class
	// @formatter:on
})
public class PluginUITestSuiteAll {
}
