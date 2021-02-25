package com.cevelop.constificator.core.util.functional;

import com.cevelop.constificator.core.util.ast.ASTRewriteCache;


/**
 * The {@code CachedUnaryPredicate} is a unary predicate that uses
 * {@link ASTRewriteCache} to speed up predicate evaluation when crossing
 * translation unit boundaries.
 *
 * @param <T>
 * The object type of the predicates target
 */
@FunctionalInterface
public interface CachedUnaryPredicate<T> {

    public boolean evaluate(T suspect, ASTRewriteCache astCache);
}
