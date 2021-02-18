package com.cevelop.conanator.tests.parser.namedsectionparser;

import static com.cevelop.conanator.tests.hamcrest.IsEmptyList.anEmptyList;
import static com.cevelop.conanator.tests.hamcrest.IsEmptyMap.anEmptyMap;
import static com.cevelop.conanator.tests.hamcrest.IsMapOfSize.aMapOfSize;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.cevelop.conanator.tests.parser.support.Conanfile;
import com.cevelop.conanator.tests.parser.support.ParserTest;
import com.cevelop.conanator.utility.NamedSectionParser;


public class NamedSectionsTest extends ParserTest<NamedSectionParser<ParserSections>, String> {

    @Override
    protected NamedSectionParser<ParserSections> createParser() throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader(getSourceText()));
        return new NamedSectionParser<>(reader);
    }

    @Test
    @Conanfile("")
    public void parsingEmptyFileReturnsEmptySectionMap() throws Exception {
        assertThat(getParser().getSections(), is(anEmptyMap()));
    }

    @Test
    @Conanfile("[empty]")
    public void parsingAnEmptySectionReturnsAMapContainingThatSectionWithNoValues() throws Exception {
        assertThat(getParser().getSections(), is(aMapOfSize(1)));
        assertThat(getParser().getSections(), hasEntry(is(equalTo("empty")), is(anEmptyList())));
    }

    @Test
    @Conanfile("[empty_lines]\n" + "entry_one\n" + "\n" + "entry_two\n" + "\n" + "\n" + "entry_three")
    public void parsingASectionContainingOnlyNewlinesReturnsAMapContainingThatSectionWithNoValues() throws Exception {
        List<String> expectedEntries = Arrays.asList("entry_one", "entry_two", "entry_three");

        assertThat(getParser().getSections(), is(aMapOfSize(1)));
        assertThat(getParser().getSections(), hasEntry(is(equalTo("empty_lines")), is(equalTo(expectedEntries))));
    }

    @Test
    @Conanfile("")
    public void queryingAParserForANonExistantSectionReturnsNull() throws Exception {
        assertThat(getParser().getSection("does_not_exist"), is(nullValue()));
    }

    @Test
    @Conanfile("[single_value]\n" + "entry")
    public void parsingASectionWithASingleValueReturnsAMapContainingThatSectionWithASingleValue() throws Exception {
        assertThat(getParser().getSections(), is(aMapOfSize(1)));
        assertThat(getParser().getSections(), hasEntry(is(equalTo("single_value")), is(equalTo(Arrays.asList("entry")))));
    }

    @Test
    @Conanfile("")
    public void savingTheResultOfAnEmptyParseWritesEmptyOutput() throws Exception {
        StringWriter buffer = new StringWriter();
        getParser().save(buffer);

        assertThat(buffer.toString(), is(equalTo("")));
    }

    @Test
    @Conanfile("\n" + "[single_value]\n" + "entry\n" + "\n" + "[empty_lines]\n" + "entry_one\n" + "\n" + "\n" + "entry_two\n" + "\n")
    public void savingTheResultOfANonEmptyParseWritesOutputSectionsWithoutEmptyLines() throws Exception {
        StringWriter buffer = new StringWriter();
        getParser().save(buffer);
        final String expected = "[single_value]\n" + "entry\n\n" + "[empty_lines]\n" + "entry_one\n" + "entry_two";

        assertThat(buffer.toString(), is(equalTo(expected)));
    }
}
