package com.cevelop.constificator.core.util.functional;

import com.cevelop.constificator.core.util.ast.ASTRewriteCache;


@FunctionalInterface
public interface CachedBinaryPredicate<T1, T2> {

    public boolean holdsFor(T1 ancestor, T2 reference, ASTRewriteCache cache);
}
