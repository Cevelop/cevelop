package com.cevelop.ctylechecker.domain;

import java.util.UUID;


public interface ICtyleElement {

    UUID getId();

    String getName();

    void setName(String pName);

    Boolean isEnabled();

    void isEnabled(Boolean pEnabled);
}
