package com.cevelop.ctylechecker.tests.unit.domain;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.cevelop.ctylechecker.domain.types.util.Qualifiers;


public class QualifiersTest {

    @Test
    public void testFieldQualifiersContainsVisibilityAttributes() {
        List<String> fieldQualifiers = Qualifiers.fieldQualifiers();
        assertTrue(fieldQualifiers.contains(Qualifiers.PUBLIC));
        assertTrue(fieldQualifiers.contains(Qualifiers.PROTECTED));
        assertTrue(fieldQualifiers.contains(Qualifiers.PRIVATE));
    }

}
