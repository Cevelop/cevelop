package com.cevelop.conanator.models;

public class Line extends ModelBase implements Cloneable {

    private String value;

    public Line(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String line) {
        this.value = line;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public Line clone() {
        return (Line) super.clone();
    }
}
