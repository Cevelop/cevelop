package com.cevelop.conanator.tests.hamcrest;

import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;


public class IsMapOfSize<K, V> extends TypeSafeMatcher<Map<K, V>> {

    private final int fSize;

    protected IsMapOfSize(int size) {
        fSize = size;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a map of size '" + fSize + "'");
    }

    @Override
    protected boolean matchesSafely(Map<K, V> item) {
        return item.size() == fSize;
    }

    public static <K, V> Matcher<Map<K, V>> aMapOfSize(int size) {
        return new IsMapOfSize<>(size);
    }
}
