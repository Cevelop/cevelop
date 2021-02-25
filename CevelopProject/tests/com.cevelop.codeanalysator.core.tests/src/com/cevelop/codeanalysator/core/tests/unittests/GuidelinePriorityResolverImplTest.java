package com.cevelop.codeanalysator.core.tests.unittests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.core.resources.IProject;
import org.junit.Before;
import org.junit.Test;

import com.cevelop.codeanalysator.core.guideline.GuidelinePriorityResolverImpl;
import com.cevelop.codeanalysator.core.guideline.IGuideline;
import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.tests.unittests.stubs.ASTNodeWithProjectStub;
import com.cevelop.codeanalysator.core.tests.unittests.stubs.ProjectStub;
import com.cevelop.codeanalysator.core.tests.unittests.stubs.guideline.GuidelinePreferencesStub;
import com.cevelop.codeanalysator.core.tests.unittests.stubs.guideline.GuidelineStub;
import com.cevelop.codeanalysator.core.tests.unittests.stubs.guideline.SuppressionStrategyStub;


public class GuidelinePriorityResolverImplTest {

   private static final String sharedProblemId = "TS01";

   private final SuppressionStrategyStub       strategy             = new SuppressionStrategyStub();
   private final List<IGuideline>              guidelines           = new ArrayList<>();
   private final GuidelinePreferencesStub      guidelinePreferences = new GuidelinePreferencesStub();
   private final GuidelinePriorityResolverImpl priorityResolver     = new GuidelinePriorityResolverImpl();
   private final ProjectStub                   project              = new ProjectStub();
   private final ASTNodeWithProjectStub        node                 = ASTNodeWithProjectStub.nodeWithProject(project);

   @Before
   public void setUp() {
      priorityResolver.startListening(guidelinePreferences);
   }

   @Test(expected = IllegalArgumentException.class)
   public void startListeningThrowsOnNullGuidelinePreferences() {
      priorityResolver.startListening(null);
   }

   @Test(expected = IllegalStateException.class)
   public void startListeningThrowsOnRepeatedStart() {
      priorityResolver.startListening(guidelinePreferences);
   }

   @Test(expected = IllegalArgumentException.class)
   public void registerSharedRuleThrowsOnNullRule() {
      priorityResolver.registerSharedRule(null);
   }

   @Test(expected = IllegalArgumentException.class)
   public void registerSharedRuleThrowsOnUnsharedRule() {
      GuidelineStub guideline = addGuidelineOfWorkspacePriority("testing.guideline", 0);
      Rule unsharedRule = new Rule("T01", guideline, null);

      priorityResolver.registerSharedRule(unsharedRule);
   }

   @Test(expected = IllegalArgumentException.class)
   public void registerSharedRuleThrowsOnRepeatedRegister() {
      GuidelineStub guideline = addGuidelineOfWorkspacePriority("testing.guideline", 0);
      Rule sharedRule = new Rule("T01", guideline, null, sharedProblemId);
      priorityResolver.registerSharedRule(sharedRule);

      priorityResolver.registerSharedRule(sharedRule);
   }

   @Test(expected = IllegalStateException.class)
   public void computePriorityOrderingsThrowsIfNotListening() {
      GuidelinePriorityResolverImpl localGuidelinePriorityResolver = new GuidelinePriorityResolverImpl();
      localGuidelinePriorityResolver.computePriorityOrderings();
   }

   @Test(expected = IllegalArgumentException.class)
   public void isHighestActiveRuleForNodeThrowsOnNullRule() {
      priorityResolver.isHighestActiveRuleForNode(null, null);
   }

   @Test(expected = IllegalArgumentException.class)
   public void isHighestActiveRuleForNodeThrowsOnNullNode() {
      GuidelineStub guideline = addGuidelineOfWorkspacePriority("testing.guideline", 0);
      Rule unsharedRule = new Rule("T01", guideline, null);
      priorityResolver.isHighestActiveRuleForNode(unsharedRule, null);
   }

   @Test(expected = IllegalArgumentException.class)
   public void isHighestActiveRuleForNodeThrowsOnNullResourceNode() {
      GuidelineStub guideline = addGuidelineOfWorkspacePriority("testing.guideline", 0);
      Rule unsharedRule = new Rule("T01", guideline, null);
      ASTNodeWithProjectStub nodeWithNullResource = ASTNodeWithProjectStub.nodeWithNullResource();
      priorityResolver.isHighestActiveRuleForNode(unsharedRule, nodeWithNullResource);
   }

   @Test(expected = IllegalArgumentException.class)
   public void isHighestActiveRuleForNodeThrowsOnNullProjectNode() {
      GuidelineStub guideline = addGuidelineOfWorkspacePriority("testing.guideline", 0);
      Rule unsharedRule = new Rule("T01", guideline, null);
      ASTNodeWithProjectStub nodeWithNullProject = ASTNodeWithProjectStub.nodeWithProject(null);
      priorityResolver.isHighestActiveRuleForNode(unsharedRule, nodeWithNullProject);
   }

   @Test
   public void highestActiveRuleForNodeIfUnsharedRule() {
      GuidelineStub guideline = addGuidelineOfWorkspacePriority("testing.guideline", 0);
      Rule unsharedRule = new Rule("T01", guideline, null);

      assertTrue(priorityResolver.isHighestActiveRuleForNode(unsharedRule, node));
   }

   @Test
   public void notHighestActiveRuleForNodeIfUnsharedSuppressedRule() {
      GuidelineStub guideline = addGuidelineOfWorkspacePriority("testing.guideline", 0);
      Rule unsharedSuppressedRule = new Rule("T01", guideline, null);
      strategy.markRuleAsSuppressed(unsharedSuppressedRule);

      assertFalse(priorityResolver.isHighestActiveRuleForNode(unsharedSuppressedRule, node));
   }

   @Test
   public void notHighestActiveRuleForNodeIfUnsharedDisabledRule() {
      GuidelineStub disabledGuideline = addGuidelineOfWorkspacePriority("testing.disabledGuideline", 0);
      guidelinePreferences.disableGuideline(disabledGuideline);
      Rule unsharedDisabledRule = new Rule("T01", disabledGuideline, null);

      assertFalse(priorityResolver.isHighestActiveRuleForNode(unsharedDisabledRule, node));
   }

   @Test
   public void notHighestActiveRuleForNodeIfUnsharedDisabledRuleForProject() {
      GuidelineStub guideline = addGuidelineOfWorkspacePriority("testing.guideline", 0);
      guidelinePreferences.disableGuidelineForProject(guideline, project);
      Rule unsharedDisabledRule = new Rule("T01", guideline, null);

      assertFalse(priorityResolver.isHighestActiveRuleForNode(unsharedDisabledRule, node));
   }

   @Test(expected = IllegalStateException.class)
   public void isHighestActiveRuleForNodeThrowsOnUnregisteredSharedRule() {
      GuidelineStub guideline = addGuidelineOfWorkspacePriority("testing.guideline", 0);
      Rule rule = new Rule("T01", guideline, null, sharedProblemId);

      priorityResolver.computePriorityOrderings();

      priorityResolver.isHighestActiveRuleForNode(rule, node);
   }

   @Test
   public void highestActiveRuleForNodeIfHigherPriorityRule() {
      GuidelineStub highGuideline = addGuidelineOfWorkspacePriority("testing.highGuideline", 0);
      Rule highRule = new Rule("T01H", highGuideline, null, sharedProblemId);
      priorityResolver.registerSharedRule(highRule);

      GuidelineStub lowGuideline = addGuidelineOfWorkspacePriority("testing.lowGuideline", 1);
      Rule lowRule = new Rule("T01L", lowGuideline, null, sharedProblemId);
      priorityResolver.registerSharedRule(lowRule);

      priorityResolver.computePriorityOrderings();

      assertTrue(priorityResolver.isHighestActiveRuleForNode(highRule, node));
      assertFalse(priorityResolver.isHighestActiveRuleForNode(lowRule, node));
   }

   @Test
   public void highestActiveRuleForNodeIfHigherRuleDisabled() {
      GuidelineStub disabledHighGuideline = addGuidelineOfWorkspacePriority("testing.disabledGuideline", 0);
      guidelinePreferences.disableGuideline(disabledHighGuideline);
      Rule disabledHighRule = new Rule("T01DH", disabledHighGuideline, null, sharedProblemId);
      priorityResolver.registerSharedRule(disabledHighRule);

      GuidelineStub lowGuideline = addGuidelineOfWorkspacePriority("testing.lowGuideline", 1);
      Rule lowRule = new Rule("T01L", lowGuideline, null, sharedProblemId);
      priorityResolver.registerSharedRule(lowRule);

      priorityResolver.computePriorityOrderings();

      assertFalse(priorityResolver.isHighestActiveRuleForNode(disabledHighRule, node));
      assertTrue(priorityResolver.isHighestActiveRuleForNode(lowRule, node));
   }

   @Test
   public void highestActiveRuleForNodeIfHigherRuleSuppressed() {
      GuidelineStub highGuideline = addGuidelineOfWorkspacePriority("testing.highGuideline", 0);
      Rule suppressedHighRule = new Rule("T01SH", highGuideline, null, sharedProblemId);
      strategy.markRuleAsSuppressed(suppressedHighRule);
      priorityResolver.registerSharedRule(suppressedHighRule);

      GuidelineStub lowGuideline = addGuidelineOfWorkspacePriority("testing.lowGuideline", 1);
      Rule lowRule = new Rule("T01L", lowGuideline, null, sharedProblemId);
      priorityResolver.registerSharedRule(lowRule);

      priorityResolver.computePriorityOrderings();

      assertFalse(priorityResolver.isHighestActiveRuleForNode(suppressedHighRule, node));
      assertTrue(priorityResolver.isHighestActiveRuleForNode(lowRule, node));
   }

   @Test
   public void highestActiveRuleForNodeIfHighestPriorityEnabledAndNotSuppressedRule() {
      GuidelineStub disabledFirstGuideline = addGuidelineOfWorkspacePriority("testing.disabledFirstGuideline", 0);
      guidelinePreferences.disableGuideline(disabledFirstGuideline);
      Rule disabledFirstRule = new Rule("T01D", disabledFirstGuideline, null, sharedProblemId);
      priorityResolver.registerSharedRule(disabledFirstRule);

      GuidelineStub secondGuideline = addGuidelineOfWorkspacePriority("testing.secondGuideline", 1);
      Rule suppressedSecondRule = new Rule("T01S", secondGuideline, null, sharedProblemId);
      strategy.markRuleAsSuppressed(suppressedSecondRule);
      priorityResolver.registerSharedRule(suppressedSecondRule);

      GuidelineStub fourthGuideline = addGuidelineOfWorkspacePriority("testing.fourthGuideline", 3);
      Rule lowFourthRule = new Rule("T01L", fourthGuideline, null, sharedProblemId);
      priorityResolver.registerSharedRule(lowFourthRule);

      GuidelineStub thirdGuideline = addGuidelineOfWorkspacePriority("testing.thridGuideline", 2);
      Rule highThirdRule = new Rule("T01H", thirdGuideline, null, sharedProblemId);
      priorityResolver.registerSharedRule(highThirdRule);

      priorityResolver.computePriorityOrderings();

      assertFalse(priorityResolver.isHighestActiveRuleForNode(disabledFirstRule, node));
      assertFalse(priorityResolver.isHighestActiveRuleForNode(suppressedSecondRule, node));
      assertTrue(priorityResolver.isHighestActiveRuleForNode(highThirdRule, node));
      assertFalse(priorityResolver.isHighestActiveRuleForNode(lowFourthRule, node));
   }

   @Test
   public void highestActiveRuleForNodeDifferentForProjectsWithDifferentSettings() {
      GuidelineStub firstGuideline = addGuidelineOfWorkspacePriority("testing.firstGuideline", 0);
      Rule firstRule = new Rule("T01", firstGuideline, "testing.firstGuideline.rule", sharedProblemId);
      priorityResolver.registerSharedRule(firstRule);

      GuidelineStub secondGuideline = addGuidelineOfWorkspacePriority("testing.lowGuideline", 1);
      Rule secondRule = new Rule("T02", secondGuideline, "testing.secondGuideline.rule", sharedProblemId);
      priorityResolver.registerSharedRule(secondRule);

      IProject firstProject = new ProjectStub();
      IASTNode firstNode = ASTNodeWithProjectStub.nodeWithProject(firstProject);

      IProject secondProject = new ProjectStub();
      IASTNode secondNode = ASTNodeWithProjectStub.nodeWithProject(secondProject);

      guidelinePreferences.setProjectPriority(firstProject, firstGuideline, 0);
      guidelinePreferences.setProjectPriority(firstProject, secondGuideline, 1);

      guidelinePreferences.setProjectPriority(secondProject, firstGuideline, 1);
      guidelinePreferences.setProjectPriority(secondProject, secondGuideline, 0);

      priorityResolver.computePriorityOrderings();

      assertTrue(priorityResolver.isHighestActiveRuleForNode(firstRule, firstNode));
      assertFalse(priorityResolver.isHighestActiveRuleForNode(secondRule, firstNode));

      assertFalse(priorityResolver.isHighestActiveRuleForNode(firstRule, secondNode));
      assertTrue(priorityResolver.isHighestActiveRuleForNode(secondRule, secondNode));
   }

   @Test
   public void highestActiveRuleForNodeUpdatedAfterPreferencesChange() {
      GuidelineStub laterLowGuideline = addGuidelineOfWorkspacePriority("testing.laterLowGuideline", 0);
      Rule laterLowRule = new Rule("T01H", laterLowGuideline, null, sharedProblemId);
      priorityResolver.registerSharedRule(laterLowRule);

      GuidelineStub laterHighGuideline = addGuidelineOfWorkspacePriority("testing.laterHighGuideline", 1);
      Rule laterHighRule = new Rule("T01L", laterHighGuideline, null, sharedProblemId);
      priorityResolver.registerSharedRule(laterHighRule);

      priorityResolver.computePriorityOrderings();

      guidelinePreferences.setWorkspacePriority(laterLowGuideline, 2);

      assertFalse(priorityResolver.isHighestActiveRuleForNode(laterLowRule, node));
      assertTrue(priorityResolver.isHighestActiveRuleForNode(laterHighRule, node));
   }

   private GuidelineStub addGuidelineOfWorkspacePriority(String guidelineId, int priority) {
      GuidelineStub guideline = new GuidelineStub(guidelineId);
      guideline.setSuppressionStrategy(strategy);
      guidelines.add(guideline);
      guidelinePreferences.setWorkspacePriority(guideline, priority);
      return guideline;
   }
}
