package com.cevelop.ctylechecker.domain.types;

import java.util.UUID;

import com.google.gson.annotations.Expose;

import com.cevelop.ctylechecker.domain.ICtyleElement;


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

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Boolean isEnabled() {
        return enabled;
    }

    @Override
    public void isEnabled(Boolean pEnabled) {
        this.enabled = pEnabled;
    }
}
