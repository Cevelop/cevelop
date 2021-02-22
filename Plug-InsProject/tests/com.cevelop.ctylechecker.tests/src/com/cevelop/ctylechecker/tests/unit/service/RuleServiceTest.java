package com.cevelop.ctylechecker.tests.unit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;

import com.cevelop.ctylechecker.domain.IConcept;
import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.domain.types.util.Concepts;
import com.cevelop.ctylechecker.domain.types.util.Expressions;
import com.cevelop.ctylechecker.service.IRuleService;
import com.cevelop.ctylechecker.service.types.CtylecheckerRuntime;


public class RuleServiceTest {

    private IRuleService ruleService = CtylecheckerRuntime.getInstance().getRegistry().getRuleService();

    @Test
    public void testSimpleRuleCreated() {
        IRule rule = ruleService.createRule("Some Rule", true);
        assertNotNull(rule);
        assertEquals(0, rule.getCheckedConcepts().size());
        assertEquals(0, rule.getPredefinedExpressions().size());
        assertEquals(0, rule.getCustomExpressions().size());
        assertEquals("", rule.getMessage());
        assertEquals("Some Rule", rule.getName());
    }

    @Test
    public void testRuleIsUpdatedCorrectly() {
        IRule rule = ruleService.createRule("Some Rule", true);
        rule.setMessage("Hello Test Message");
        Optional<IConcept> oConcept = Concepts.getConcept(Concepts.CPP_VARIABLE);
        assertTrue(oConcept.isPresent());
        rule.setCheckedConcepts(Arrays.asList(oConcept.get()));
        rule.setPredefinedExpressions(Arrays.asList(Expressions.SNAKE_CASE));
        IRule toUpdate = ruleService.createRule("Some other Rule", false);
        toUpdate.setMessage("Some other Message");
        toUpdate = ruleService.updateRule(toUpdate, rule);
        assertEquals(rule.getName(), toUpdate.getName());
        assertEquals(rule.getCustomExpressions(), toUpdate.getCustomExpressions());
        assertEquals(rule.getPredefinedExpressions(), toUpdate.getPredefinedExpressions());
        assertEquals(rule.getMessage(), toUpdate.getMessage());
    }
}
