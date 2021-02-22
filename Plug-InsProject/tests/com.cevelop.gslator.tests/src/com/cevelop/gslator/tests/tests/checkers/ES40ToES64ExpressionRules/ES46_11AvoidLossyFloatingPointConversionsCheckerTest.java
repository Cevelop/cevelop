package com.cevelop.gslator.tests.tests.checkers.ES40ToES64ExpressionRules;

import com.cevelop.gslator.checkers.visitors.ES40ToES64ExpressionRules.utils.ES46LossyType;


public class ES46_11AvoidLossyFloatingPointConversionsCheckerTest extends ES46_00AvoidLossyConversionCheckerTest {

    @Override
    protected ES46LossyType getLossyType() {
        return ES46LossyType.Fp;
    }
}
