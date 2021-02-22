package com.cevelop.macronator.tests.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.junit.Test;

import com.cevelop.macronator.common.ParserAdapter;


public class ParserAdapterTest {

    private ParserAdapter parserAdapter;

    @Test
    public void testShouldReportNoErrorsWhenParsingValidCppCode() throws Exception {
        String validCppCode = "constexpr auto PI = 3.1415;";
        parse(validCppCode);
        assertNoErrors();
    }

    @Test
    public void testShouldReportErrorsIfCodeIsInvalid() throws Exception {
        String invalidCppCode = "constexpr auto PI 31";
        parse(invalidCppCode);
        assertErrorsOccured();
    }

    @Test
    public void testShouldReportErrorsIfInputInputIsAType() throws Exception {
        String invalidCppCode = "(char*)";
        parse(invalidCppCode);
        assertErrorsOccured();
    }

    private IASTTranslationUnit parse(String code) {
        parserAdapter = new ParserAdapter(code);
        return parserAdapter.parse();
    }

    private void assertNoErrors() {
        assertFalse(parserAdapter.encounteredErrors());
    }

    private void assertErrorsOccured() {
        assertTrue(parserAdapter.encounteredErrors());
    }
}
