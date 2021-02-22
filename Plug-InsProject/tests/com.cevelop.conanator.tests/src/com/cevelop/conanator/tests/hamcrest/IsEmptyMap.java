package com.cevelop.conanator.tests.hamcrest;

import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.Matcher;


public class IsEmptyMap<K, V> extends IsMapOfSize<K, V> {

    private IsEmptyMap() {
        super(0);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("an empty map");
    }

    public static <K, V> Matcher<Map<K, V>> anEmptyMap() {
        return aMapOfSize(0);
    }
}
