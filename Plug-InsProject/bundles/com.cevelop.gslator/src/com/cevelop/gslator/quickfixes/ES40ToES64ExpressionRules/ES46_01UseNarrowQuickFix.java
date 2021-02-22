package com.cevelop.gslator.quickfixes.ES40ToES64ExpressionRules;

import com.cevelop.gslator.ids.IdHelper.Rule;


public class ES46_01UseNarrowQuickFix extends ES46_00AvoidLossyArithmeticConversionsQuickFix {

    @Override
    public String getLabel() {
        return Rule.ES46 + ": a) use narrow()";
    }

    @Override
    public String getCastName() {
        return "narrow";
    }
}
