package com.cevelop.ctylechecker.tests.unit.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import com.cevelop.ctylechecker.domain.IGrouping;
import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.domain.IStyleguide;
import com.cevelop.ctylechecker.domain.types.Grouping;
import com.cevelop.ctylechecker.domain.types.Rule;
import com.cevelop.ctylechecker.service.IStyleguideService;
import com.cevelop.ctylechecker.service.factory.StyleguideFactory;
import com.cevelop.ctylechecker.service.types.CtylecheckerRuntime;


public class StyleguideTest {

    private IStyleguideService styleguideService = CtylecheckerRuntime.getInstance().getRegistry().getStyleguideService();

    @Test
    public void testCreatedRuleIsFoundViaStyleguide() {
        IStyleguide googleStyleguide = StyleguideFactory.createGoogleStyleguide();
        assertNotNull(googleStyleguide);
        assertTrue(!googleStyleguide.getGroupings().isEmpty());
        IGrouping[] groupings = googleStyleguide.getGroupings().toArray(new Grouping[0]);
        assertTrue(!groupings[0].getRules().isEmpty());
        Rule[] rules = groupings[0].getRules().toArray(new Rule[0]);
        Rule firstRule = rules[0];
        IRule foundRule = googleStyleguide.getRule(firstRule.getId());
        assertEquals(firstRule, foundRule);
    }

    @Test
    public void testCreatedGroupIsFoundViaStyleguide() {
        IStyleguide googleStyleguide = StyleguideFactory.createGoogleStyleguide();
        IGrouping newGrouping = new Grouping("New Group");
        Rule newRule = new Rule("New Rule");
        newGrouping.addRule(newRule);
        googleStyleguide.addGrouping(newGrouping);
        Optional<IGrouping> foundGrouping = styleguideService.findGroupOfRule(googleStyleguide, newRule.getId());
        assertTrue(foundGrouping.isPresent());
        assertEquals(newGrouping, foundGrouping.get());
    }

    @Test
    public void testFieldGroupingIsNotNullOnCreation() {
        IStyleguide styleguide = styleguideService.createStyleguide("New Styleguide");
        assertTrue(styleguide.getGroupings().isEmpty());
        assertEquals(0, styleguide.getGroupings().size());
    }

    @Test
    public void testFieldGroupingHasElementsWhenElementsAdded() {
        IStyleguide styleguide = styleguideService.createStyleguide("New Styleguide");
        styleguide.addGrouping(new Grouping("New Group"));
        styleguide.addGrouping(new Grouping("New Group"));
        styleguide.addGrouping(new Grouping("New Group"));
        assertTrue(!styleguide.getGroupings().isEmpty());
        assertEquals(3, styleguide.getGroupings().size());
    }

    @Test
    public void testFieldGroupingHasZeroElementsAddAndDelete() {
        IStyleguide styleguide = styleguideService.createStyleguide("New Styleguide");
        Grouping newGrouping = new Grouping("New Group");
        styleguide.addGrouping(newGrouping);
        styleguide.removeGrouping(newGrouping.getId());
        assertTrue(styleguide.getGroupings().isEmpty());
        assertEquals(0, styleguide.getGroupings().size());
    }

    @Test
    public void testFieldRuleHasElementsWhenElementsAdded() {
        IStyleguide styleguide = styleguideService.createStyleguide("New Styleguide");
        styleguide.addRule(new Rule("New Rule"));
        styleguide.addRule(new Rule("New Rule"));
        styleguide.addRule(new Rule("New Rule"));
        assertTrue(!styleguide.getRules().isEmpty());
        assertEquals(3, styleguide.getRules().size());
    }

    @Test
    public void testFieldRuleHasZeroElementsAddAndDelete() {
        IStyleguide styleguide = styleguideService.createStyleguide("New Styleguide");
        Rule newRule = new Rule("New Rule");
        styleguide.addRule(newRule);
        styleguide.removeRule(newRule.getId());
        assertTrue(styleguide.getRules().isEmpty());
        assertEquals(0, styleguide.getRules().size());
    }

    @Test
    public void testFieldRuleIsNotNullOnCreation() {
        IStyleguide styleguide = styleguideService.createStyleguide("New Styleguide");
        assertTrue(styleguide.getRules().isEmpty());
        assertEquals(0, styleguide.getRules().size());
    }
}
