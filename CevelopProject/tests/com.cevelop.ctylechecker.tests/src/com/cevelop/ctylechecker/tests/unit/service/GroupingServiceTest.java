package com.cevelop.ctylechecker.tests.unit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.cevelop.ctylechecker.domain.IGrouping;
import com.cevelop.ctylechecker.service.IGroupingService;
import com.cevelop.ctylechecker.service.types.CtylecheckerRuntime;


public class GroupingServiceTest {

    IGroupingService groupingService = CtylecheckerRuntime.getInstance().getRegistry().getGroupingService();

    @Test
    public void testSimpleGroupingCreated() {
        IGrouping grouping = groupingService.createGroup("Some Group", true);
        assertNotNull(grouping);
        assertEquals("Some Group", grouping.getName());
        assertTrue(grouping.getRules().isEmpty());
    }
}
