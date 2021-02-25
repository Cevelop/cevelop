package com.cevelop.constificator.core.util.functional;

@FunctionalInterface
public interface TernaryPredicate<T1, T2, T3> {

    public boolean holdsFor(T1 t1, T2 t2, T3 t3);
}
