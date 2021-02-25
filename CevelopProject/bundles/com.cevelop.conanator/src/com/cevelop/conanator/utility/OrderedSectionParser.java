package com.cevelop.conanator.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class OrderedSectionParser extends SectionParser<Integer> {

    public OrderedSectionParser(File file) throws FileNotFoundException, IOException {
        super(file);
    }

    public OrderedSectionParser(BufferedReader reader) throws IOException {
        super(reader);
    }

    @Override
    protected Map<Integer, List<String>> parse(BufferedReader reader) throws IOException {
        Map<Integer, List<String>> sections = new TreeMap<>();
        List<String> currentSection = new ArrayList<>();
        int sectionNr = 0;
        String line;

        sections.put(sectionNr++, currentSection);

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.isEmpty() || line.startsWith("#")) {
                currentSection = new ArrayList<>();
                sections.put(sectionNr++, currentSection);
            } else {
                String[] split = line.split("#");
                if (split.length > 0) {
                    currentSection.add(split[0].trim());
                }
            }
        }

        return sections;
    }

    @Override
    public void save(Writer writer) throws IOException {
        StringBuilder builder = new StringBuilder();

        for (List<String> section : sections.values()) {
            if (section != null) {
                for (String entry : section) {
                    if (entry != null && !entry.isEmpty()) {
                        builder.append(entry);
                        builder.append(System.lineSeparator());
                    }
                }
            }

            builder.append(System.lineSeparator());
        }

        writer.write(builder.toString().replaceFirst("(\\r?\\n)++$", ""));
        writer.flush();
    }
}
