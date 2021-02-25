package com.cevelop.gslator.tests.tests.quickfixes.ES40ToES64ExpressionRules;

import org.eclipse.ui.IMarkerResolution;

import com.cevelop.gslator.checkers.visitors.ES40ToES64ExpressionRules.utils.ES46LossyType;
import com.cevelop.gslator.quickfixes.ES40ToES64ExpressionRules.ES46_02UseNarrowCastQuickFix;


public class ES46_02_09AvoidLossyIntegerConversionsQuickFixTest extends ES46_00AvoidLossyConversionsQuickFixTest {

    @Override
    protected IMarkerResolution getQuickFix() {
        return new ES46_02UseNarrowCastQuickFix();
    }

    @Override
    protected ES46LossyType getLossyType() {
        return ES46LossyType.Integer;
    }
}
