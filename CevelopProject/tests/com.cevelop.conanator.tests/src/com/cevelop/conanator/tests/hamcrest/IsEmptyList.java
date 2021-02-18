package com.cevelop.conanator.tests.hamcrest;

import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;


public class IsEmptyList<T> extends TypeSafeMatcher<List<T>> {

    @Override
    public void describeTo(Description description) {
        description.appendText("an empty map");
    }

    @Override
    protected boolean matchesSafely(List<T> item) {
        return item.isEmpty();
    }

    public static <T> Matcher<List<T>> anEmptyList() {
        return new IsEmptyList<>();
    }
}
