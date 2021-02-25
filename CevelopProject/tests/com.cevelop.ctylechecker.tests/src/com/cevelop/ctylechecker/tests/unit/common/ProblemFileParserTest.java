package com.cevelop.ctylechecker.tests.unit.common;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;

import org.junit.Test;

import com.cevelop.ctylechecker.problems.CtyleProblem;
import com.cevelop.ctylechecker.problems.ProblemFileParser;


public class ProblemFileParserTest {

    private static final String NL = System.getProperty("line.separator");

    @Test
    public void testRegularInput() throws MalformedURLException {
        final String problem = "Problem Statement";
        final String explanation = "problem explanation";
        final String input = "# " + problem + NL + NL + explanation + NL + NL + "[lmgtfy](http://www.google.com)" + NL +
                             "[lmytfy](http://www.yahoo.com)";

        final CtyleProblem parse = ProblemFileParser.parse(input);
        assertEquals(problem, parse.getProblem());
        assertEquals(explanation, parse.getExplanation());
        final HashMap<String, URL> hashMap = new HashMap<>();
        hashMap.put("lmgtfy", new URL("http://www.google.com"));
        hashMap.put("lmytfy", new URL("http://www.yahoo.com"));
        assertEquals(hashMap, parse.getResources());
    }

    @Test
    public void testInputWithoutUrls() throws MalformedURLException {
        final String problem = "Problem Statement";
        final String explanation = "problem explanation";

        final String input = "# " + problem + NL + NL + explanation + NL;
        final CtyleProblem parse = ProblemFileParser.parse(input);
        assertEquals(problem, parse.getProblem());
        assertEquals(explanation, parse.getExplanation());
        assertEquals(Collections.emptyMap(), parse.getResources());
    }

    @Test(expected = RuntimeException.class)
    public void testMultiparagraphExplanations() throws MalformedURLException {
        final String problem = "Problem Statement";
        final String explanation = "problem explanation p1" + NL + NL + "problem explanation p2" + NL + NL + "problem explanation p3" + NL;
        final String input = "# " + problem + NL + NL + explanation + NL + NL;
        ProblemFileParser.parse(input);

    }

    @Test(expected = RuntimeException.class)
    public void testMissingProblemExplanation() throws MalformedURLException {
        final String problem = "Problem Statement";
        final String input = "# " + problem + NL + NL + "[lmgtfy](http://www.google.com)" + NL + "[lmytfy](http://www.yahoo.com)";
        ProblemFileParser.parse(input);

    }

    @Test(expected = RuntimeException.class)
    public void testMissingProblemStatement() throws MalformedURLException {
        final String input = "problem explanation" + NL + NL + "[lmgtfy](http://www.google.com)" + NL + "[lmytfy](http://www.yahoo.com)";
        ProblemFileParser.parse(input);
    }
}
