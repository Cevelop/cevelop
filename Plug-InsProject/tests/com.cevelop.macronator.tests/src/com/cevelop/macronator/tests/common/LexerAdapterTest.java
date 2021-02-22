package com.cevelop.macronator.tests.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.cdt.core.parser.IToken;
import org.junit.Test;

import com.cevelop.macronator.common.LexerAdapter;


public class LexerAdapterTest {

    @Test
    public void testShouldReturnNextToken() {
        String input = "token";
        LexerAdapter lexerAdapter = new LexerAdapter(input);
        IToken nextToken = lexerAdapter.nextToken();
        assertEquals("token", nextToken.getImage());
        assertTrue(lexerAdapter.atEndOfInput());
    }

    @Test
    public void testShouldReportEndOfInput() {
        String input = "token";
        LexerAdapter lexerAdapter = new LexerAdapter(input);
        IToken nextToken = lexerAdapter.nextToken();
        assertEquals("token", nextToken.getImage());
        assertTrue(lexerAdapter.atEndOfInput());
    }

    @Test
    public void testShouldReportEndOfInputCorrectlyWhenInputEmpty() {
        String emptyInput = "";
        LexerAdapter lexerAdapter = new LexerAdapter(emptyInput);
        assertTrue(lexerAdapter.atEndOfInput());
    }
}
