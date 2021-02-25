package com.cevelop.codeanalysator.core.tests.unittests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.tests.unittests.stubs.guideline.GuidelineStub;
import com.cevelop.codeanalysator.core.tests.unittests.stubs.guideline.SuppressionStrategyStub;


public class RuleSuppressionTest {

   @Test
   public void ruleNotSuppressedTest() {
      SuppressionStrategyStub strategy = new SuppressionStrategyStub();
      GuidelineStub guideline = new GuidelineStub("testing.guideline");
      guideline.setSuppressionStrategy(strategy);
      Rule rule = new Rule("T01", guideline, null, null);

      assertFalse(rule.isSuppressedForNode(null));
   }

   @Test
   public void ruleSuppressedTest() {
      SuppressionStrategyStub strategy = new SuppressionStrategyStub();
      GuidelineStub guideline = new GuidelineStub("testing.guideline");
      guideline.setSuppressionStrategy(strategy);
      Rule rule = new Rule("T01", guideline, null, null);

      strategy.markRuleAsSuppressed(rule);

      assertTrue(rule.isSuppressedForNode(null));
   }
}
