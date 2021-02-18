package com.cevelop.codeanalysator.core.tests.unittests.stubs.guideline;

import com.cevelop.codeanalysator.core.guideline.IGuideline;
import com.cevelop.codeanalysator.core.guideline.ISuppressionStrategy;


public class GuidelineStub implements IGuideline {

   private String               guidelineId;
   private ISuppressionStrategy strategy;

   public GuidelineStub(String guidelineId) {
      this.guidelineId = guidelineId;
   }

   @Override
   public String getName() {
      return "GuidelineStub";
   }

   @Override
   public String getId() {
      return guidelineId;
   }

   @Override
   public ISuppressionStrategy getSuppressionStrategy() {
      return strategy;
   }

   public void setSuppressionStrategy(ISuppressionStrategy strategy) {
      this.strategy = strategy;
   }
}
