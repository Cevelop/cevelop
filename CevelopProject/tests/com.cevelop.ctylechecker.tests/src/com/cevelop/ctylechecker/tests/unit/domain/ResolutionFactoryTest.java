package com.cevelop.ctylechecker.tests.unit.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.cevelop.ctylechecker.domain.types.AddPrefixResolution;
import com.cevelop.ctylechecker.domain.types.AddSuffixResolution;
import com.cevelop.ctylechecker.domain.types.CaseTransformerResolution;
import com.cevelop.ctylechecker.domain.types.DefaultRenameResolution;
import com.cevelop.ctylechecker.domain.types.ReplaceResolution;
import com.cevelop.ctylechecker.domain.types.util.Expressions;
import com.cevelop.ctylechecker.service.factory.ResolutionFactory;


public class ResolutionFactoryTest {

    @Test
    public void testCaseTransformerResolutionCreated() {
        CaseTransformerResolution resolution = ResolutionFactory.createCaseTransformerResolution(Expressions.SNAKE_CASE);
        assertTrue(resolution instanceof CaseTransformerResolution);
    }

    @Test
    public void testAddPrefixResolutionCreated() {
        AddPrefixResolution resolution = ResolutionFactory.createPrefixResolution(Expressions.SNAKE_CASE);
        assertTrue(resolution instanceof AddPrefixResolution);
    }

    @Test
    public void testAddSuffixResolutionCreated() {
        AddSuffixResolution resolution = ResolutionFactory.createSuffixResolution(Expressions.SNAKE_CASE);
        assertTrue(resolution instanceof AddSuffixResolution);
    }

    @Test
    public void testReplaceResolutionCreated() {
        ReplaceResolution resolution = ResolutionFactory.createReplaceResolution(Expressions.SNAKE_CASE);
        assertTrue(resolution instanceof ReplaceResolution);
    }

    @Test
    public void testDefaultRenameResolutionCreated() {
        DefaultRenameResolution resolution = ResolutionFactory.createDefaultRenameResolution();
        assertTrue(resolution instanceof DefaultRenameResolution);
    }

    @Test
    public void testTypeOfDefaultRenameResolutionIsDefaultRenameResolution() {
        String resolutionType = ResolutionFactory.getType(new DefaultRenameResolution());
        assertEquals(ResolutionFactory.DEFAULT_RENAME_RESOLUTION, resolutionType);
    }

    @Test
    public void testTypeOfCaseTransformerResolutionIsCaseTransformerResolution() {
        String resolutionType = ResolutionFactory.getType(new CaseTransformerResolution(Expressions.SNAKE_CASE));
        assertEquals(ResolutionFactory.CASE_TRANSFORMER_RESOLUTION, resolutionType);
    }

    @Test
    public void testTypeOfAddPrefixResolutionIsAddPrefixResolution() {
        String resolutionType = ResolutionFactory.getType(new AddPrefixResolution(Expressions.SNAKE_CASE));
        assertEquals(ResolutionFactory.ADD_PREFIX_RESOLUTION, resolutionType);
    }

    @Test
    public void testTypeOfAddSuffixResolutionIsAddSuffixResolution() {
        String resolutionType = ResolutionFactory.getType(new AddSuffixResolution(Expressions.SNAKE_CASE));
        assertEquals(ResolutionFactory.ADD_SUFFIX_RESOLUTION, resolutionType);
    }

    @Test
    public void testTypeOfReplaceResolutionIsReplaceResolution() {
        String resolutionType = ResolutionFactory.getType(new ReplaceResolution(Expressions.SNAKE_CASE));
        assertEquals(ResolutionFactory.REPLACE_RESOLUTION, resolutionType);
    }
}
