package com.cevelop.conanator.tests.parser.namedsectionparser;

import static com.cevelop.conanator.tests.hamcrest.IsEmptyList.anEmptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;

import org.junit.Test;

import com.cevelop.conanator.tests.parser.support.Conanfile;
import com.cevelop.conanator.tests.parser.support.ParserTest;
import com.cevelop.conanator.utility.NamedSectionParser;


public class NamelessSectionTest extends ParserTest<NamedSectionParser<ParserSections>, String> {

    @Override
    protected NamedSectionParser<ParserSections> createParser() throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader(getSourceText()));
        return new NamedSectionParser<>(reader);
    }

    @Test
    @Conanfile("")
    public void parsingEmptyFileReturnsEmptyNamelessSection() throws Exception {
        assertThat(getParser().getNamelessSection(), is(anEmptyList()));
    }

    @Test
    @Conanfile("nameless_value_one\n" + "nameless_value_two")
    public void parsingTheNamelessSectionReturnsAListContainingItsValues() throws Exception {
        assertThat(getParser().getNamelessSection(), is(equalTo(Arrays.asList("nameless_value_one", "nameless_value_two"))));
    }

    @Test
    @Conanfile("\n" + "   \n" + "\t\n" + "")
    public void parsingTheNamelessSectionIgnoresWhitespaceLines() throws Exception {
        assertThat(getParser().getNamelessSection(), is(anEmptyList()));
    }
}
