package com.cevelop.includator.helpers.filter;

public interface Predicate<T> {

    boolean matches(T type);
}
