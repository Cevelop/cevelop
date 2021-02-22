package com.cevelop.conanator.models;

public class SectionEntry extends Entry implements Cloneable {

    public ProfileSection section;

    public SectionEntry(ProfileSection section) {
        this(section, "", "");
    }

    public SectionEntry(ProfileSection section, String key, String value) {
        super(key, value);
        this.section = section;
    }

    public ProfileSection getSection() {
        return section;
    }

    public void setSection(ProfileSection section) {
        this.section = section;
    }

    @Override
    public String toString() {
        return key;
    }

    @Override
    public SectionEntry clone() {
        return (SectionEntry) super.clone();
    }
}
