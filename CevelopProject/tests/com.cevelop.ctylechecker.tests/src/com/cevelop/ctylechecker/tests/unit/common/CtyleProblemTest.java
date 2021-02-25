package com.cevelop.ctylechecker.tests.unit.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;

import org.junit.Test;

import com.cevelop.ctylechecker.problems.CtyleProblem;


public class CtyleProblemTest {

    @Test
    public void testDefaultConstructor() {
        final CtyleProblem ctyle = new CtyleProblem();
        assertEquals("", ctyle.getProblem());
        assertEquals("", ctyle.getExplanation());
        assertEquals(Collections.emptyMap(), ctyle.getResources());
    }

    @Test
    public void testConstructorWithValues() throws MalformedURLException {
        final String problem = "problem";
        final String explanation = "explanation";
        final HashMap<String, URL> hashMap = new HashMap<>();
        hashMap.put("lmgtfy", new URL("http://www.google.com"));
        hashMap.put("lmytfy", new URL("http://www.yahoo.com"));
        final CtyleProblem ctyle = new CtyleProblem(problem, explanation, hashMap);
        assertEquals(problem, ctyle.getProblem());
        assertEquals(explanation, ctyle.getExplanation());
        assertEquals(hashMap, ctyle.getResources());
    }

    @Test
    public void testEqualsSelf() {
        final CtyleProblem self = new CtyleProblem();
        assertTrue(self.equals(self));

    }

    @Test
    public void testEqualsOtherWithSameValues() throws MalformedURLException {
        final String problem = "problem";
        final String explanation = "explanation";
        final HashMap<String, URL> hashMap = new HashMap<>();
        hashMap.put("lmgtfy", new URL("http://www.google.com"));
        hashMap.put("lmytfy", new URL("http://www.yahoo.com"));
        final CtyleProblem lhs = new CtyleProblem(problem, explanation, hashMap);
        final CtyleProblem rhs = new CtyleProblem(problem, explanation, hashMap);
        assertTrue(lhs.equals(rhs));
    }

    @Test
    public void testEqualsWithDifferentValues() throws MalformedURLException {
        final String problemlhs = "problemlhs";
        final String explanationlhs = "explanationlhs";
        final String problemrhs = "problemrhs";
        final String explanationrhs = "explanationrhs";
        final HashMap<String, URL> hashMap = new HashMap<>();
        hashMap.put("lmgtfy", new URL("http://www.google.com"));
        hashMap.put("lmytfy", new URL("http://www.yahoo.com"));
        final CtyleProblem lhs = new CtyleProblem(problemlhs, explanationlhs, hashMap);
        final CtyleProblem rhs = new CtyleProblem(problemrhs, explanationrhs, hashMap);
        assertFalse(lhs.equals(rhs));
    }

    @Test
    public void testEqualsWithOtherObject() {
        final CtyleProblem ctyle = new CtyleProblem();
        final Object o = new Object();
        assertFalse(ctyle.equals(o));
    }

    @Test
    public void testSameHashCodeWithSameObject() throws MalformedURLException {
        final String problem = "problem";
        final String explanation = "explanation";
        final HashMap<String, URL> hashMap = new HashMap<>();
        hashMap.put("lmgtfy", new URL("http://www.google.com"));
        hashMap.put("lmytfy", new URL("http://www.yahoo.com"));
        final CtyleProblem ctyle = new CtyleProblem(problem, explanation, hashMap);
        final int hashCode1 = ctyle.hashCode();
        final int hashCode2 = ctyle.hashCode();
        assertEquals(hashCode1, hashCode2);
    }

}
