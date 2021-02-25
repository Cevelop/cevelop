package com.cevelop.ctylechecker.tests.unit.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import com.cevelop.ctylechecker.domain.IConcept;
import com.cevelop.ctylechecker.domain.types.util.Concepts;
import com.cevelop.ctylechecker.service.IConceptService;
import com.cevelop.ctylechecker.service.types.CtylecheckerRuntime;


public class ConceptServiceTest {

    private IConceptService conceptService = CtylecheckerRuntime.getInstance().getRegistry().getConceptService();

    @Test
    public void testCreateConcept() {
        IConcept concept = conceptService.createConcept(Concepts.CPP_VARIABLE);
        assertNotNull(concept);
    }

    @Test
    public void testFindConceptCppVariable() {
        Optional<IConcept> oConcept = conceptService.getConcept(Concepts.CPP_VARIABLE);
        assertTrue(oConcept.isPresent());
    }
}
