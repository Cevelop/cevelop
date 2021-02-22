package com.cevelop.ctylechecker.tests.unit.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import com.cevelop.ctylechecker.common.ConceptNames;
import com.cevelop.ctylechecker.domain.IConcept;
import com.cevelop.ctylechecker.domain.types.util.Concepts;
import com.cevelop.ctylechecker.domain.types.util.Qualifiers;


public class ConceptsTest {

    @Test
    public void testOriginalNameToSimpleNameConversion() {
        String originalName = Concepts.CPP_VARIABLE;
        String simpleName = Concepts.simpleName(originalName);
        assertEquals(simpleName, ConceptNames.NAME_VARIABLES);
    }

    @Test
    public void testSimpleNameToOriginalNameConversion() {
        String simpleName = ConceptNames.NAME_VARIABLES;
        String originalName = Concepts.originalName(simpleName);
        assertEquals(originalName, Concepts.CPP_VARIABLE);
    }

    @Test
    public void testSimpleNameToOriginalNameFailedConversionReturnsSimpleName() {
        String simpleName = "some concept";
        String originalName = Concepts.originalName(simpleName);
        assertEquals(simpleName, originalName);
    }

    @Test
    public void testOriginalNameToSimpleNameFailedConversionReturnsOriginalName() {
        String originalName = "CPP_NonExisting";
        String simpleName = Concepts.simpleName(originalName);
        assertEquals(originalName, simpleName);
    }

    @Test
    public void testVariableBindingIsInitialized() {
        Optional<IConcept> oConcept = Concepts.getConcept(Concepts.CPP_VARIABLE);
        assertTrue(oConcept.isPresent());
        IConcept variableConcept = oConcept.get();
        assertEquals(variableConcept.getType(), Concepts.CPP_VARIABLE);
        assertEquals(variableConcept.getQualifiers().size(), Qualifiers.variableQualifiers().size());
    }

    @Test
    public void testFieldBindingIsInitialized() {
        Optional<IConcept> oConcept = Concepts.getConcept(Concepts.CPP_FIELD);
        assertTrue(oConcept.isPresent());
        IConcept variablConcept = oConcept.get();
        assertEquals(variablConcept.getType(), Concepts.CPP_FIELD);
        assertEquals(variablConcept.getQualifiers().size(), Qualifiers.fieldQualifiers().size());
    }

    @Test
    public void testNonExistingBindingAccessReturnsNonPresent() {
        Optional<IConcept> oConcept = Concepts.getConcept("CPP_Variables");
        assertTrue(!oConcept.isPresent());
    }
}
