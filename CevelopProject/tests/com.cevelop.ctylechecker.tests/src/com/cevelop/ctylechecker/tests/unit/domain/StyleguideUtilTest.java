package com.cevelop.ctylechecker.tests.unit.domain;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.cevelop.ctylechecker.domain.IExpression;
import com.cevelop.ctylechecker.domain.ISingleExpression;
import com.cevelop.ctylechecker.domain.types.Expression;
import com.cevelop.ctylechecker.service.util.StyleguideUtil;


public class StyleguideUtilTest {

    // TODO REPLACE
    //	@Test
    //	public void testMakeCopyOfRuleGeneratesUniqueId() {
    //		IRule commonVariableRuleGoogle = GoogleRuleFactory.createCommonVariableNamingRule();
    //		IRule copiedRule = StyleguideUtil.makeRuleCopy(commonVariableRuleGoogle);
    //		assertTrue(!commonVariableRuleGoogle.getId().equals(copiedRule.getId()));
    //	}

    @Test
    public void testMakeExpressionsCopyGeneratesUniqueIds() {
        List<IExpression> expressions = new ArrayList<>();
        expressions.add(new Expression("Some Expression", "^someRegex$", true));
        List<IExpression> copiedExpressions = StyleguideUtil.makeExpressionsCopy(expressions);
        for (int i = 0; i < expressions.size(); i++) {
            assertTrue(!((ISingleExpression) expressions.get(i)).getId().equals(((ISingleExpression) copiedExpressions.get(i)).getId()));
        }
    }

    // TODO REPLACE
    //	@Test
    //	public void testMakeBindingsCopyGeneratesDuplicate() {
    //		IRule commonVariableRuleGoogle = GoogleRuleFactory.createCommonVariableNamingRule();
    //		List<IConcept> bindings = commonVariableRuleGoogle.getCheckedConcepts();
    //		List<IConcept> copiedBindings = StyleguideUtil.makeConceptsCopy(commonVariableRuleGoogle);
    //		for(int i = 0; i < bindings.size(); i++) {
    //			assertTrue(bindings.get(i).getType().equals(copiedBindings.get(i).getType()));
    //		}
    //	}
}
