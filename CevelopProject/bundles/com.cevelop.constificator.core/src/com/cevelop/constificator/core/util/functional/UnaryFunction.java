package com.cevelop.constificator.core.util.functional;

@FunctionalInterface
public interface UnaryFunction<T> {

    void call(T thing);
}
