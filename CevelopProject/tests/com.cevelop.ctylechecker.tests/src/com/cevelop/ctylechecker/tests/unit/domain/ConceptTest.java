package com.cevelop.ctylechecker.tests.unit.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import com.cevelop.ctylechecker.common.ConceptNames;
import com.cevelop.ctylechecker.domain.IConcept;
import com.cevelop.ctylechecker.domain.types.util.Concepts;
import com.cevelop.ctylechecker.domain.types.util.Qualifiers;


public class ConceptTest {

    @Test
    public void testInvalidConceptCantBeFound() {
        String invalidConceptName = "Some Unknown Concept";
        Optional<IConcept> oConcept = Concepts.getConcept(invalidConceptName);
        assertTrue(!oConcept.isPresent());
    }

    @Test
    public void testValidConceptCanBeFound() {
        Optional<IConcept> oConcept = Concepts.getConcept(Concepts.CPP_VARIABLE);
        assertTrue(oConcept.isPresent());
    }

    @Test
    public void testIfConversionFailsInputIsReturned() {
        String name = Concepts.originalName("Test Input");
        assertEquals("Test Input", name);
    }

    @Test
    public void testOriginalNameIsFoundViaSimpleName() {
        assertEquals(Concepts.CPP_VARIABLE, Concepts.originalName(ConceptNames.NAME_VARIABLES));
        assertEquals(Concepts.CPP_METHOD, Concepts.originalName(ConceptNames.NAME_METHODS));
        assertEquals(Concepts.CPP_FUNCTION, Concepts.originalName(ConceptNames.NAME_FUNCTIONS));
        assertEquals(Concepts.CPP_FIELD, Concepts.originalName(ConceptNames.NAME_FIELDS));
        assertEquals(Concepts.CPP_CLASS_TYPE, Concepts.originalName(ConceptNames.NAME_CLASS_TYPES));
        assertEquals(Concepts.CPP_ENUMARATION, Concepts.originalName(ConceptNames.NAME_ENUMARATIONS));
        assertEquals(Concepts.CPP_ENUMARATOR, Concepts.originalName(ConceptNames.NAME_ENUMARATORS));
        assertEquals(Concepts.CPP_NAMESPACE, Concepts.originalName(ConceptNames.NAME_NAMESPACES));
        assertEquals(Concepts.CPP_TYPEDEF, Concepts.originalName(ConceptNames.NAME_TYPEDEFINITIONS));
    }

    @Test
    public void testSimpleNameIsFoundViaOriginalName() {
        assertEquals(ConceptNames.NAME_VARIABLES, Concepts.simpleName(Concepts.CPP_VARIABLE));
        assertEquals(ConceptNames.NAME_METHODS, Concepts.simpleName(Concepts.CPP_METHOD));
        assertEquals(ConceptNames.NAME_FUNCTIONS, Concepts.simpleName(Concepts.CPP_FUNCTION));
        assertEquals(ConceptNames.NAME_FIELDS, Concepts.simpleName(Concepts.CPP_FIELD));
        assertEquals(ConceptNames.NAME_CLASS_TYPES, Concepts.simpleName(Concepts.CPP_CLASS_TYPE));
        assertEquals(ConceptNames.NAME_ENUMARATIONS, Concepts.simpleName(Concepts.CPP_ENUMARATION));
        assertEquals(ConceptNames.NAME_ENUMARATORS, Concepts.simpleName(Concepts.CPP_ENUMARATOR));
        assertEquals(ConceptNames.NAME_NAMESPACES, Concepts.simpleName(Concepts.CPP_NAMESPACE));
        assertEquals(ConceptNames.NAME_TYPEDEFINITIONS, Concepts.simpleName(Concepts.CPP_TYPEDEF));
    }

    @Test
    public void testVariableConceptHasCorrectQualifiers() {
        Optional<IConcept> oConcept = Concepts.getConcept(Concepts.CPP_VARIABLE);
        assertTrue(oConcept.isPresent());
        assertEquals(Qualifiers.variableQualifiers(), oConcept.get().getQualifiers());
    }

    @Test
    public void testFieldConceptHasCorrectQualifiers() {
        Optional<IConcept> oConcept = Concepts.getConcept(Concepts.CPP_FIELD);
        assertTrue(oConcept.isPresent());
        assertEquals(Qualifiers.fieldQualifiers(), oConcept.get().getQualifiers());
    }

    @Test
    public void testFunctionConceptHasCorrectQualifiers() {
        Optional<IConcept> oConcept = Concepts.getConcept(Concepts.CPP_FUNCTION);
        assertTrue(oConcept.isPresent());
        assertEquals(Qualifiers.functionQualifiers(), oConcept.get().getQualifiers());
    }

    @Test
    public void testMethodConceptHasCorrectQualifiers() {
        Optional<IConcept> oConcept = Concepts.getConcept(Concepts.CPP_METHOD);
        assertTrue(oConcept.isPresent());
        assertEquals(Qualifiers.methodQualifiers(), oConcept.get().getQualifiers());
    }
}
