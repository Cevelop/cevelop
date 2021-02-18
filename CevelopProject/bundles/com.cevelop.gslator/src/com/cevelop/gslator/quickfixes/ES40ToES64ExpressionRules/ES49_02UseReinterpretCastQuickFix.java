package com.cevelop.gslator.quickfixes.ES40ToES64ExpressionRules;

import org.eclipse.core.resources.IMarker;

import com.cevelop.gslator.ids.IdHelper.Rule;


public class ES49_02UseReinterpretCastQuickFix extends ES49_00UseNamedCastQuickFix {

    @Override
    public boolean isApplicable(IMarker marker) {
        if (super.isApplicable(marker)) {
            return isReinterpretCastQuickFixEnabled(marker);
        }
        return false;
    }

    @Override
    public String getLabel() {
        return Rule.ES49 + ": b) use reinterpret_cast() (use with caution)";
    }

    @Override
    protected String getCastName(String targetCast) {
        return "reinterpret_cast<" + targetCast + ">";
    }
}
