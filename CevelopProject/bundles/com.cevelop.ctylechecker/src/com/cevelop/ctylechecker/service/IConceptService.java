package com.cevelop.ctylechecker.service;

import java.util.List;
import java.util.Optional;

import com.cevelop.ctylechecker.domain.IConcept;


public interface IConceptService {

    Optional<IConcept> getConcept(String pName);

    String simpleName(String pOriginalName);

    String originalName(String pSimpleName);

    IConcept createConcept(String pType);

    List<String> getAll();
}
