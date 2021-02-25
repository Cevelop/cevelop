package com.cevelop.constificator.core.util.functional;

@FunctionalInterface
public interface BinaryPredicate<T1, T2> {

    public boolean holdsFor(T1 ancestor, T2 reference);
}
