package com.cevelop.ctylechecker.tests.unit.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.cevelop.ctylechecker.domain.IGrouping;
import com.cevelop.ctylechecker.domain.types.Grouping;
import com.cevelop.ctylechecker.domain.types.Rule;


public class GroupingTest {

    @Test
    public void testGroupingIsEnabledOnCreationWithNameParamOnly() {
        Grouping grouping = new Grouping("Some Group");
        assertTrue(grouping.isEnabled());
    }

    @Test
    public void testGroupingIsEnabledOnCreationWithEnabledParamSetToTrue() {
        Grouping grouping = new Grouping("Some Group", true);
        assertTrue(grouping.isEnabled());
    }

    @Test
    public void testGroupingIsDisabledOnCreationWithEnabledParamSetToFalse() {
        Grouping grouping = new Grouping("Some Group", false);
        assertTrue(!grouping.isEnabled());
    }

    @Test
    public void testRulesFieldIsEmptyOnSimpleCreation() {
        IGrouping grouping = new Grouping("Some Group");
        assertEquals(true, grouping.getRules().isEmpty());
    }

    @Test
    public void testRulesFieldIsNotEmptyWhenRulesAdded() {
        IGrouping grouping = new Grouping("Some Group");
        grouping.addRule(new Rule("Some Rule"));
        grouping.addRule(new Rule("Some Rule"));
        grouping.addRule(new Rule("Some Rule"));
        grouping.addRule(new Rule("Some Rule"));
        assertEquals(false, grouping.getRules().isEmpty());
        assertEquals(4, grouping.getRules().size());
    }

    @Test
    public void testRulesFieldIsEmptyWhenRulesAddedAndRemovedById() {
        IGrouping grouping = new Grouping("Some Group");
        Rule rule = new Rule("Some Rule");
        grouping.addRule(rule);
        assertEquals(false, grouping.getRules().isEmpty());
        assertEquals(1, grouping.getRules().size());
        grouping.removeRule(rule.getId());
        assertEquals(true, grouping.getRules().isEmpty());
        assertEquals(0, grouping.getRules().size());
    }
}
