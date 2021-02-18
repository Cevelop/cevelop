package com.cevelop.includator.tests.suppresssuggestiontest;

import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;

import com.cevelop.includator.preferences.IncludatorPropertyManager;
import com.cevelop.includator.tests.base.IncludatorTest;


public class SuppressSuggestionTest extends IncludatorTest {

    @After
    @Override
    public void tearDown() throws Exception {
        resetSuppressedSuggestionsProperty();
        super.tearDown();
    }

    private void resetSuppressedSuggestionsProperty() {
        IncludatorPropertyManager.setProperty(getCurrentProject(), IncludatorPropertyManager.SUPPRESSED_SUGGESTIONS_PROPERTY_NAME, "");
    }

    protected void assertSuppressList(String expected) {
        Map<String, Set<String>> suppressedSuggestions = IncludatorPropertyManager.getSuppressedSuggestions(getActiveProject());
        Assert.assertEquals(expected, suppressedSuggestions.toString());
    }
}
