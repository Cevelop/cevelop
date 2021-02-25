package com.cevelop.gslator.quickfixes.ES40ToES64ExpressionRules;

import com.cevelop.gslator.ids.IdHelper.Rule;


public class ES46_02UseNarrowCastQuickFix extends ES46_00AvoidLossyArithmeticConversionsQuickFix {

    @Override
    public String getLabel() {
        return Rule.ES46 + ": b) use narrow_cast()";
    }

    @Override
    public String getCastName() {
        return "narrow_cast";
    }
}
