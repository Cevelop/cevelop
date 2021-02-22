package com.cevelop.includator.helpers.filter;

import java.util.ArrayList;
import java.util.Collection;


public class FilterHelper {

    public static <T> Collection<T> filter(Collection<T> target, Predicate<T> predicate) {
        Collection<T> result = new ArrayList<>();
        for (T element : target) {
            if (predicate.matches(element)) {
                result.add(element);
            }
        }
        return result;
    }
}
