package com.cevelop.ctylechecker.tests.unit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import com.cevelop.ctylechecker.domain.IGrouping;
import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.domain.IStyleguide;
import com.cevelop.ctylechecker.service.IGroupingService;
import com.cevelop.ctylechecker.service.IRuleService;
import com.cevelop.ctylechecker.service.IStyleguideService;
import com.cevelop.ctylechecker.service.factory.StyleguideFactory;
import com.cevelop.ctylechecker.service.types.CtylecheckerRuntime;


public class StyleguideServiceTest {

    IStyleguideService styleguideService = CtylecheckerRuntime.getInstance().getRegistry().getStyleguideService();
    IRuleService       ruleService       = CtylecheckerRuntime.getInstance().getRegistry().getRuleService();
    IGroupingService   groupingService   = CtylecheckerRuntime.getInstance().getRegistry().getGroupingService();

    @Test
    public void testSimpleStyleguideCreated() {
        IStyleguide styleguide = styleguideService.createStyleguide("Test Styleguide");
        assertEquals("Test Styleguide", styleguide.getName());
        assertEquals(0, styleguide.getGroupings().size());
        assertEquals(0, styleguide.getRules().size());
    }

    @Test
    public void testSimpleStyleguideIsCopied() {
        IStyleguide styleguide = styleguideService.createStyleguide("Test Styleguide");
        IStyleguide copy = styleguideService.makeCopy(styleguide);
        assertEquals(styleguide.getName(), copy.getName());
        assertEquals(styleguide.getGroupings().isEmpty(), copy.getGroupings().isEmpty());
    }

    @Test
    public void testGoogleStyleguideCopied() {
        IStyleguide googleStyleguide = StyleguideFactory.createGoogleStyleguide();
        IStyleguide copy = styleguideService.makeCopy(googleStyleguide);
        assertEquals(googleStyleguide.getName(), copy.getName());
        assertTrue(!googleStyleguide.getId().equals(copy.getId()));
        assertEquals(googleStyleguide.getGroupings().size(), copy.getGroupings().size());
        assertEquals(googleStyleguide.getRules().size(), copy.getRules().size());
        for (IGrouping grouping : googleStyleguide.getGroupings()) {
            assertNull(copy.getGrouping(grouping.getId()));
        }
        for (IRule rule : googleStyleguide.getRules()) {
            assertNull(copy.getRule(rule.getId()));
        }
    }

    @Test
    public void testGroupOfRuleFound() {
        IStyleguide styleguide = styleguideService.createStyleguide("Test Styleguide");
        IRule rule = ruleService.createRule("Some Rule", true);
        IGrouping grouping = groupingService.createGroup("Some Group", true);
        grouping.addRule(rule);
        styleguide.addGrouping(grouping);
        Optional<IGrouping> foundGrouping = styleguideService.findGroupOfRule(styleguide, rule);
        assertTrue(foundGrouping.isPresent());
        assertEquals(grouping.getId(), foundGrouping.get().getId());
    }
}
