package com.cevelop.conanator.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NamedSectionParser<Section extends Enum<Section>> extends SectionParser<String> {

    private List<String> fNamelessSection;

    public NamedSectionParser(File file) throws FileNotFoundException, IOException {
        super(file);
    }

    public NamedSectionParser(BufferedReader reader) throws IOException {
        super(reader);
    }

    public List<String> getSection(Section section) {
        return super.getSection(section.name());
    }

    public void setSection(Section section, List<String> data) {
        super.setSection(section.name(), data);
    }

    public List<String> getNamelessSection() {
        return fNamelessSection;
    }

    public void setNamelessSection(List<String> data) {
        fNamelessSection = data;
    }

    @Override
    protected Map<String, List<String>> parse(BufferedReader reader) throws IOException {
        fNamelessSection = new ArrayList<>();
        Map<String, List<String>> sections = new HashMap<>();
        List<String> currentSection = null;
        String line;
        Pattern sectionStart = Pattern.compile("^\\[(.+)\\]");
        Matcher m;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.startsWith("#")) {
                continue;
            } else if ((m = sectionStart.matcher(line)).find()) {
                currentSection = new ArrayList<>();
                sections.put(m.group(1), currentSection);
            } else if (!line.isEmpty()) {
                String[] parts = line.split("#");
                if (parts.length > 0 && !parts[0].isEmpty()) {
                    String value = parts[0].trim();
                    if (currentSection != null) {
                        currentSection.add(value);
                    } else {
                        fNamelessSection.add(value);
                    }
                }
            }
        }

        return sections;
    }

    @Override
    public void save(Writer writer) throws IOException {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, List<String>> section : sections.entrySet()) {
            if (!section.getKey().isEmpty()) {
                builder.append("[" + section.getKey() + "]");
                builder.append(System.lineSeparator());
            }

            if (section.getValue() != null) {
                for (String entry : section.getValue()) {
                    if (entry != null && !entry.isEmpty()) {
                        builder.append(entry);
                        builder.append(System.lineSeparator());
                    }
                }
            }

            if (builder.length() != 0) builder.append(System.lineSeparator());
        }

        writer.write(builder.toString().replaceFirst("(\\r?\\n)++$", ""));
        writer.flush();
    }
}
