package com.cevelop.includator.helpers.matchers;

public interface Matcher<O, T extends Throwable> {

    public boolean matches(O object) throws T;
}
