package com.cevelop.ctylechecker.service.types;

import java.util.List;
import java.util.Optional;

import com.cevelop.ctylechecker.domain.IConcept;
import com.cevelop.ctylechecker.domain.types.Concept;
import com.cevelop.ctylechecker.domain.types.util.Concepts;
import com.cevelop.ctylechecker.service.IConceptService;


public class ConceptService implements IConceptService {

    @Override
    public Optional<IConcept> getConcept(String pName) {
        return Concepts.getConcept(pName);
    }

    @Override
    public String simpleName(String pOriginalName) {
        return Concepts.simpleName(pOriginalName);
    }

    @Override
    public String originalName(String pSimpleName) {
        return Concepts.originalName(pSimpleName);
    }

    @Override
    public IConcept createConcept(String pType) {
        return new Concept(pType);
    }

    @Override
    public List<String> getAll() {
        return Concepts.all();
    }

}
