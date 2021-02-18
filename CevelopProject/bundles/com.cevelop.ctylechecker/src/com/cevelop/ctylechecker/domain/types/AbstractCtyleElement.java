package com.cevelop.ctylechecker.domain.types;

import java.util.UUID;

import com.cevelop.ctylechecker.domain.ICtyleElement;
import com.google.gson.annotations.Expose;


public abstract class AbstractCtyleElement implements ICtyleElement {

    @Expose
    private String  name;
    @Expose
    private UUID    id;
    @Expose
    private Boolean enabled;

    public AbstractCtyleElement(String pName, Boolean pEnabled) {
        id = UUID.randomUUID();
        name = pName;
        enabled = pEnabled;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void isEnabled(Boolean pEnabled) {
        this.enabled = pEnabled;
    }
}
