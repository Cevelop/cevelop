package com.cevelop.ctylechecker.quickfix.dynamic.model;

import org.eclipse.core.resources.IFile;


public class MarkerModel {

    public int    charStart;
    public int    charEnd;
    public String name;
    public IFile  file;

    public MarkerModel(int charStart, int charEnd, String name, IFile file) {
        this.charStart = charStart;
        this.charEnd = charEnd;
        this.name = name;
        this.file = file;
    }
}
