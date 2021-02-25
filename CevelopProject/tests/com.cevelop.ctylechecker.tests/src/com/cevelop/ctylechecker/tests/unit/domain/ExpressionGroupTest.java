package com.cevelop.ctylechecker.tests.unit.domain;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.cevelop.ctylechecker.domain.IGroupExpression;
import com.cevelop.ctylechecker.domain.types.Expression;
import com.cevelop.ctylechecker.domain.types.ExpressionGroup;


public class ExpressionGroupTest {

    @Test
    public void testExpressionGroupCreatedProperly() {
        ExpressionGroup group = new ExpressionGroup("Test Group", true);
        assertTrue(group.getName().equals("Test Group"));
        assertTrue(group.shouldMatchAll().equals(true));
    }

    @Test
    public void testAddingTwoExpressionsToGroupEqualsSizeOfTwo() {
        IGroupExpression group = new ExpressionGroup("Test Group", true);
        group.addExpression(new Expression("Test Expression", "^TestRegex$", true));
        group.addExpression(new Expression("Test Expression 2", "^TestRegex2$", true));
        assertTrue(group.getExpressions().size() == 2);
    }

    @Test
    public void testGroupWithOneExpressionMatchesAll() {
        IGroupExpression group = new ExpressionGroup("Test Group", true);
        group.addExpression(new Expression("Test Expression", "^TestRegex$", true));
        assertTrue(group.check("TestRegex"));
    }

    @Test
    public void testGroupWithTwoExpressionMatchesAny() {
        IGroupExpression group = new ExpressionGroup("Test Group", false);
        group.addExpression(new Expression("Test Expression", "^TestRegex$", true));
        group.addExpression(new Expression("Test Expression 2", "^RegexTest$", true));
        assertTrue(group.check("TestRegex"));
        assertTrue(group.check("RegexTest"));
    }
}
