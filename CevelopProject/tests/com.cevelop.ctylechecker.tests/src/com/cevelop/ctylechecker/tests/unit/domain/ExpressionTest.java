package com.cevelop.ctylechecker.tests.unit.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.cevelop.ctylechecker.domain.ISingleExpression;
import com.cevelop.ctylechecker.domain.OrderPriority;
import com.cevelop.ctylechecker.domain.ResolutionHint;
import com.cevelop.ctylechecker.domain.types.DefaultRenameResolution;
import com.cevelop.ctylechecker.domain.types.Expression;


public class ExpressionTest {

    @Test
    public void testSimpleRegexChecksTrueOnMatch() {
        ISingleExpression expression = new Expression("Test Expression", "^simpleString$", true);
        assertTrue(expression.check("simpleString"));
    }

    @Test
    public void testSimpleRegexChecksTrueOnDontMatch() {
        ISingleExpression expression = new Expression("Test Expression", "^simpleString$", false);
        assertTrue(!expression.check("simpleString"));
        assertTrue(expression.check("some other string"));
    }

    @Test
    public void testExpressionHasDefaultResolutionOnCreation() {
        ISingleExpression expression = new Expression("Test Expression");
        assertTrue(expression.getResolution() instanceof DefaultRenameResolution);
    }

    @Test
    public void testExpressionDefaultResolutionReturnsInput() {
        ISingleExpression expression = new Expression("Test Expression");
        assertTrue(expression.getResolution() instanceof DefaultRenameResolution);
        String toTransform = "testVariableName";
        String transformed = expression.getResolution().transform("testVariableName");
        assertEquals(transformed, toTransform);
    }

    @Test
    public void testExpressionOrderPriorityIsHighOnCreation() {
        ISingleExpression expression = new Expression("Test Expression");
        assertTrue(expression.getOrder().equals(OrderPriority.HIGH));
    }

    @Test
    public void testExpressionHintIsNoneOnCreation() {
        ISingleExpression expression = new Expression("Test Expression");
        assertTrue(expression.getHint().equals(ResolutionHint.NONE));
    }
}
