package com.cevelop.ctylechecker.domain;

import java.util.List;


public interface IConcept {

    void setQualifiers(List<String> qualifiers);

    List<String> getQualifiers();

    void setType(String type);

    String getType();

}
