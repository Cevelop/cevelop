package com.cevelop.conanator.tests.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.cevelop.conanator.utility.OrderedSectionParser;


public class OrderedSectionParserTest {

    private OrderedSectionParser parser;
    private OrderedSectionParser emptyParser;

    @Before
    public void setUp() throws Exception {
        StringBuilder builder = new StringBuilder(100);
        builder.append("first\n");
        builder.append("section\n");
        builder.append("entries\n");
        builder.append("\n");
        builder.append("\n");
        builder.append("after\n");
        builder.append("empty section\n");
        builder.append("# comment\n");
        builder.append("trailing # comment\n");

        try (BufferedReader reader = new BufferedReader(new StringReader(builder.toString()))) {
            parser = new OrderedSectionParser(reader);
        }

        try (BufferedReader reader = new BufferedReader(new StringReader(""))) {
            emptyParser = new OrderedSectionParser(reader);
        }
    }

    @After
    public void tearDown() throws Exception {
        parser = null;
        emptyParser = null;
    }

    @Test
    public void test_getSection_first() {
        List<String> expected = Arrays.asList("first", "section", "entries");
        assertEquals(expected, parser.getSection(0));
    }

    @Test
    public void test_getSection_empty() {
        assertTrue(parser.getSection(1).isEmpty());
    }

    @Test
    public void test_getSection_afterEmpty() {
        List<String> expected = Arrays.asList("after", "empty section");
        assertEquals(expected, parser.getSection(2));
    }

    @Test
    public void test_getSection_containingCommentedValue() {
        List<String> expected = Arrays.asList("trailing");
        assertEquals(expected, parser.getSection(3));
    }

    @Test
    public void test_getSection_nonexistent() {
        assertNull(parser.getSection(42));
    }

    @Test
    public void test_setSection() {
        List<String> expected = Arrays.asList("i set", "this section", "myself");
        parser.setSection(0, expected);
        assertEquals(expected, parser.getSection(0));
    }

    @Test
    public void test_editSection() {
        List<String> section = parser.getSection(0);
        section.set(0, "edited first");
        section.add("and added more");

        List<String> expected = Arrays.asList("edited first", "section", "entries", "and added more");
        assertEquals(expected, parser.getSection(0));
    }

    @Test
    public void test_save_singleSection() throws IOException {
        try (StringWriter writer = new StringWriter()) {
            emptyParser.setSection(0, Arrays.asList("first", "section"));
            emptyParser.save(writer);

            String expected = "first\nsection";
            assertEquals(expected, writer.toString());
        }
    }

    @Test
    public void test_save_multipleSections() throws IOException {
        try (StringWriter writer = new StringWriter()) {
            emptyParser.setSection(0, Arrays.asList("first", "section"));
            emptyParser.setSection(1, Arrays.asList());
            emptyParser.setSection(2, Arrays.asList("after empty"));
            emptyParser.save(writer);

            String expected = "first\nsection\n\n\nafter empty";
            assertEquals(expected, writer.toString());
        }
    }

    @Test
    public void test_save_emptySection() throws IOException {
        try (StringWriter writer = new StringWriter()) {
            emptyParser.setSection(0, Arrays.asList());
            emptyParser.save(writer);

            String expected = "";
            assertEquals(expected, writer.toString());
        }
    }

    @Test
    public void test_save_nullSection() throws IOException {
        try (StringWriter writer = new StringWriter()) {
            emptyParser.setSection(0, null);
            emptyParser.save(writer);

            String expected = "";
            assertEquals(expected, writer.toString());
        }
    }

    @Test
    public void test_save_ignoreEmptyAndNullLines() throws IOException {
        try (StringWriter writer = new StringWriter()) {
            emptyParser.setSection(0, Arrays.asList("before empty", "", null, "after empty"));
            emptyParser.save(writer);

            String expected = "before empty\nafter empty";
            assertEquals(expected, writer.toString());
        }
    }
}
