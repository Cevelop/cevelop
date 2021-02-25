package com.cevelop.ctylechecker.domain.types;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import com.cevelop.ctylechecker.domain.IConcept;


public class Concept implements IConcept {

    @Expose
    private String       type;
    @Expose
    private List<String> qualifiers;

    public Concept(String pType) {
        setType(pType);
        setQualifiers(new ArrayList<>());
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public List<String> getQualifiers() {
        return qualifiers;
    }

    @Override
    public void setQualifiers(List<String> qualifiers) {
        this.qualifiers = qualifiers;
    }

    public boolean equals(IConcept pConcept) {
        return type.equals(pConcept.getType());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Concept ? equals((IConcept) obj) : false;
    }

}
