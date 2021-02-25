package com.cevelop.ctylechecker.tests.unit.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.cevelop.ctylechecker.common.ConceptNames;
import com.cevelop.ctylechecker.domain.ISingleExpression;
import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.domain.types.Concept;
import com.cevelop.ctylechecker.domain.types.Expression;
import com.cevelop.ctylechecker.domain.types.Rule;
import com.cevelop.ctylechecker.domain.types.util.Qualifiers;


public class RuleTest {

    @Test
    public void testNewRuleWithOnlyNameInitializedHasFieldsEmpty() {
        IRule rule = new Rule("New Rule");
        assertTrue(rule.getCheckedConcepts().isEmpty());
        assertTrue(rule.getPredefinedExpressions().isEmpty());
        assertTrue(rule.getCustomExpressions().isEmpty());
    }

    @Test
    public void testNewRuleWithOnlyNameInitializedHasFieldsNotNull() {
        IRule rule = new Rule("New Rule");
        assertTrue(rule.getCheckedConcepts() != null);
        assertTrue(rule.getPredefinedExpressions() != null);
        assertTrue(rule.getCustomExpressions() != null);
    }

    @Test
    public void testNewRuleIsAutomaticallyEnabledOnCreationWithOnlyNameInitialized() {
        Rule rule = new Rule("New Rule");
        assertEquals(true, rule.isEnabled());
    }

    @Test
    public void testNewRuleIsEnabledWithParemeterEnabledSetToTrue() {
        Rule rule = new Rule("New Rule", true);
        assertEquals(true, rule.isEnabled());
    }

    @Test
    public void testNewRuleIsDisabledWithParemeterEnabledSetToFalse() {
        Rule rule = new Rule("New Rule", false);
        assertEquals(false, rule.isEnabled());
    }

    @Test
    public void testRuleIsApplicableToSourceFileWithCheckConceptOnFile() {
        Rule rule = new Rule("New Rule");
        Concept fileConcept = new Concept(ConceptNames.NAME_SOURCE_FILE);
        fileConcept.setQualifiers(Arrays.asList(Qualifiers.FILE_BODY));
        rule.setCheckedConcepts(Arrays.asList(fileConcept));
        Boolean pIsHeaderFile = false;
        assertTrue(rule.isApplicableToFile(pIsHeaderFile));
    }

    @Test
    public void testRuleDoesntMatchFullFileNameWithEnding() {
        Rule rule = new Rule("New Rule");
        Concept fileConcept = new Concept(ConceptNames.NAME_SOURCE_FILE);
        fileConcept.setQualifiers(Arrays.asList(Qualifiers.FILE_BODY));
        rule.setCheckedConcepts(Arrays.asList(fileConcept));
        Boolean pIsHeaderFile = false;
        assertTrue(rule.isApplicableToFile(pIsHeaderFile));
        ISingleExpression expression = new Expression("File Body Expression");
        expression.setExpression("^Main$");
        expression.shouldMatch(true);
        rule.setCustomExpressions(Arrays.asList(expression));
        String pFileName = "Main";
        assertTrue(rule.matches(pFileName));
    }
}
