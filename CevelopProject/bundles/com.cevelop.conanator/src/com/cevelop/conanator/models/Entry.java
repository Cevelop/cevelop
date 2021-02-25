package com.cevelop.conanator.models;

public class Entry extends ModelBase implements Cloneable {

    protected String key;
    protected String value;

    public Entry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public Entry clone() {
        return (Entry) super.clone();
    }
}
