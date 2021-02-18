package com.cevelop.ctylechecker.tests.unit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import com.cevelop.ctylechecker.common.ExpressionNames;
import com.cevelop.ctylechecker.domain.ExpressionType;
import com.cevelop.ctylechecker.domain.IExpression;
import com.cevelop.ctylechecker.domain.IGroupExpression;
import com.cevelop.ctylechecker.domain.ISingleExpression;
import com.cevelop.ctylechecker.domain.ResolutionHint;
import com.cevelop.ctylechecker.domain.types.DefaultRenameResolution;
import com.cevelop.ctylechecker.domain.types.util.Expressions;
import com.cevelop.ctylechecker.service.IExpressionService;
import com.cevelop.ctylechecker.service.types.CtylecheckerRuntime;


public class ExpressionServiceTest {

    IExpressionService expressionService = CtylecheckerRuntime.getInstance().getRegistry().getExpressionService();

    @Test
    public void testSimpleExpressionCreated() {
        ISingleExpression expression = expressionService.createExpression("Some Expression", "^Regex$", true);
        assertNotNull(expression);
        assertEquals("Some Expression", expression.getName());
        assertEquals("^Regex$", expression.getExpression());
        assertEquals(ExpressionType.SINGLE, expression.getType());
        assertEquals(DefaultRenameResolution.class.getSimpleName(), expression.getResolution().getClass().getSimpleName());
        assertEquals("", expression.getArgument());
        assertEquals(ResolutionHint.NONE, expression.getHint());
    }

    @Test
    public void testSimpleGroupExpressionCreated() {
        IGroupExpression group = expressionService.createExpressionGroup("Group Expression", true);
        assertNotNull(group);
        assertEquals("Group Expression", group.getName());
        assertTrue(group.getExpressions().isEmpty());
        assertEquals(0, group.getPrefered().size());
        assertEquals(ResolutionHint.NONE, group.getHint());
    }

    @Test
    public void testComplexGroupExpressionCreated() {
        IGroupExpression group = expressionService.createExpressionGroup("Group Expression", true);
        IGroupExpression subGroup = expressionService.createExpressionGroup("Subgroup Expression", true);
        ISingleExpression expression1 = expressionService.createExpression("Some Expression 1", "^Regex$", true);
        ISingleExpression expression2 = expressionService.createExpression("Some Expression 2", "^Regex$", true);
        expression2.setHint(ResolutionHint.PREFERED);
        subGroup.setHint(ResolutionHint.PREFERED);
        group.addExpression(expression1);
        group.addExpression(expression2);
        group.addExpression(subGroup);
        assertEquals(3, group.getExpressions().size());
        assertEquals(2, group.getPrefered().size());
        assertTrue(!group.isPrefered());
        assertTrue(group.containsPrefered());
        assertTrue(subGroup.isPrefered());
    }

    @Test
    public void testAddSingleExpressionToExpressionGroup() {
        IGroupExpression group = expressionService.createExpressionGroup("Group Expression", true);
        ISingleExpression expression1 = expressionService.createExpression("Some Expression 1", "^Regex$", true);
        expressionService.addToExpressionGroup(group, expression1);
        assertTrue(!group.getExpressions().isEmpty());
    }

    @Test
    public void testGetExpressionsOfGroup() {
        IGroupExpression group = expressionService.createExpressionGroup("Group Expression", true);
        IGroupExpression subGroup = expressionService.createExpressionGroup("Subgroup Expression", true);
        ISingleExpression expression1 = expressionService.createExpression("Some Expression 1", "^Regex$", true);
        ISingleExpression expression2 = expressionService.createExpression("Some Expression 2", "^Regex$", true);
        expression2.setHint(ResolutionHint.PREFERED);
        subGroup.setHint(ResolutionHint.PREFERED);
        group.addExpression(expression1);
        group.addExpression(expression2);
        group.addExpression(subGroup);
        List<IExpression> groupExpressions = expressionService.getExpressions(group);
        assertEquals(group.getExpressions().size(), groupExpressions.size());
        assertEquals(group.getExpressions(), groupExpressions);
    }

    @Test
    public void testGetAllPredefinedExpressions() {
        List<ISingleExpression> expressions = expressionService.getAll();
        assertEquals(expressions, Expressions.all());
    }

    @Test
    public void testFindSnakeCaseExpressionInPredefinedList() {
        Optional<ISingleExpression> expression = expressionService.find(ExpressionNames.SNAKE_CASE_NAME);
        assertTrue(expression.isPresent());
        assertEquals(ExpressionNames.SNAKE_CASE_NAME, expression.get().getName());
    }
}
