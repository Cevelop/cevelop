package com.cevelop.conanator.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ConanProfile extends ModelBase implements Cloneable {

    private String               name;
    private File                 file;
    private List<String>         dependencies;
    private List<Entry>          variables;
    private List<ProfileSection> sections;
    private List<Line>           buildRequires;

    public ConanProfile() {
        this("", null);
    }

    public ConanProfile(String name, File file) {
        this.name = name;
        this.file = file;
        sections = new ArrayList<>();
        variables = new ArrayList<>();
        buildRequires = new ArrayList<>();
        dependencies = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        if (dependencies != null)
            this.dependencies = dependencies;
        else this.dependencies.clear();
    }

    public List<Entry> getVariables() {
        return variables;
    }

    public void setVariables(List<Entry> variables) {
        if (variables != null)
            this.variables = variables;
        else this.variables.clear();
    }

    public List<ProfileSection> getSections() {
        return sections;
    }

    public void setSections(List<ProfileSection> sections) {
        if (sections != null)
            this.sections = sections;
        else this.sections.clear();
    }

    public List<Line> getBuildRequires() {
        return buildRequires;
    }

    public void setBuildRequires(List<Line> buildRequires) {
        if (buildRequires != null)
            this.buildRequires = buildRequires;
        else this.buildRequires.clear();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public ConanProfile clone() {
        ConanProfile clone = (ConanProfile) super.clone();
        clone.dependencies = new ArrayList<>(dependencies);

        clone.variables = new ArrayList<>();
        for (Entry e : variables)
            clone.variables.add(e.clone());

        clone.buildRequires = new ArrayList<>();
        for (Line l : buildRequires)
            clone.buildRequires.add(l.clone());

        clone.sections = new ArrayList<>();
        for (ProfileSection s : sections)
            clone.sections.add(s.clone());

        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConanProfile)) return false;
        ConanProfile other = (ConanProfile) obj;
        if (this.name.equals(other.name)) return true;
        return false;
    }
}
