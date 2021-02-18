package com.cevelop.ctylechecker.tests.unit.domain;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.cevelop.ctylechecker.domain.types.util.ReservedNamesChecker;


public class ReservedNamesCheckerTest {

    @Test
    public void testMainIsReservedName() {
        assertTrue(ReservedNamesChecker.check("main"));
    }

    @Test
    public void testNonMainIsNotReservedName() {
        assertTrue(!ReservedNamesChecker.check("mainly"));
    }

    @Test
    public void testNonMainIsNotReservedName2() {
        assertTrue(!ReservedNamesChecker.check("support"));
    }

    @Test
    public void testNonMainIsNotReservedName3() {
        assertTrue(!ReservedNamesChecker.check("cevelop"));
    }
}
