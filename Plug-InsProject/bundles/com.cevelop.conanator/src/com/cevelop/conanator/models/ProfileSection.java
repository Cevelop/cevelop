package com.cevelop.conanator.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ProfileSection extends ModelBase implements Cloneable {

    private String             name;
    private List<SectionEntry> entries = new ArrayList<>();

    public ProfileSection(String name, Collection<String> entries) {
        this.name = name;
        if (entries != null) {
            for (String entry : entries) {
                String[] pair = entry.split("=", 2);
                this.entries.add(new SectionEntry(this, pair[0], pair[1]));
            }
        }
    }

    public ProfileSection(String name) {
        this.name = name;
    }

    public List<SectionEntry> getEntries() {
        return entries;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public ProfileSection clone() {
        ProfileSection clone = (ProfileSection) super.clone();
        clone.entries = new ArrayList<>();
        for (SectionEntry e : entries) {
            SectionEntry eClone = e.clone();
            eClone.setSection(clone);
            clone.entries.add(eClone);
        }

        return clone;
    }
}
