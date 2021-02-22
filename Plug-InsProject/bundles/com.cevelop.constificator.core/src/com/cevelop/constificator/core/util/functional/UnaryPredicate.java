package com.cevelop.constificator.core.util.functional;

@FunctionalInterface
public interface UnaryPredicate<T> {

    public boolean evaluate(T suspect);
}
