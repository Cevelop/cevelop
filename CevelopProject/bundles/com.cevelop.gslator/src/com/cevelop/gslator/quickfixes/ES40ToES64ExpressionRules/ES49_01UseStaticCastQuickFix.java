package com.cevelop.gslator.quickfixes.ES40ToES64ExpressionRules;

import com.cevelop.gslator.ids.IdHelper.Rule;


public class ES49_01UseStaticCastQuickFix extends ES49_00UseNamedCastQuickFix {

    @Override
    public String getLabel() {
        String num = "";
        if (isReinterpretCastQuickFixEnabled(marker)) num = "a) ";
        return Rule.ES49 + ": " + num + "use static_cast()";
    }

    @Override
    protected String getCastName(String targetCast) {
        return "static_cast<" + targetCast + ">";
    }
}
