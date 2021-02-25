package com.cevelop.codeanalysator.core.tests.unittests.stubs.guideline;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.ui.IMarkerResolution;

import com.cevelop.codeanalysator.core.guideline.ISuppressionStrategy;
import com.cevelop.codeanalysator.core.guideline.Rule;


public class SuppressionStrategyStub implements ISuppressionStrategy {

   private final Set<String> suppressedRuleNumbers = new HashSet<>();

   public void markRuleAsSuppressed(Rule rule) {
      suppressedRuleNumbers.add(rule.getRuleNr());
   }

   @Override
   public boolean isRuleSuppressedForNode(String ruleNr, IASTNode node) {
      return suppressedRuleNumbers.contains(ruleNr);
   }

   @Override
   public IMarkerResolution getSuppressAllQuickFix() {
      return null;
   }

   @Override
   public IMarkerResolution getSuppressRuleQuickFix(String ruleNr) {
      return null;
   }
}
