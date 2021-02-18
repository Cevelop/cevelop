package com.cevelop.conanator.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;


public abstract class SectionParser<TMapKey> {

    protected File                       file;
    protected Map<TMapKey, List<String>> sections;

    public SectionParser(File file) throws FileNotFoundException, IOException {
        this.file = file;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            sections = parse(reader);
        }
    }

    public SectionParser(BufferedReader reader) throws IOException {
        sections = parse(reader);
    }

    public List<String> getSection(TMapKey section) {
        return sections.get(section);
    }

    public Map<TMapKey, List<String>> getSections() {
        return sections;
    }

    public void setSection(TMapKey section, List<String> data) {
        sections.put(section, data);
    }

    protected abstract Map<TMapKey, List<String>> parse(BufferedReader reader) throws IOException;

    public void save() throws IOException {
        if (file == null) throw new IOException("Cannot save() to file 'null'");
        save(file);
    };

    public void save(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            save(writer);
        }
    }

    public abstract void save(Writer writer) throws IOException;
}
