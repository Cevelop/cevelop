package com.cevelop.gslator.quickfixes.utils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;


public class ASTRewriteStore {

    private final IIndex                               index;
    private final Map<String, IASTTranslationUnit>     astTranslationUnitCache;
    private final Map<IASTTranslationUnit, ASTRewrite> astRewriteCache;

    public ASTRewriteStore(final IIndex index) {
        this.index = index;
        astTranslationUnitCache = new HashMap<>();
        astRewriteCache = new HashMap<>();
    }

    public IASTTranslationUnit getASTTranslationUnit(final ITranslationUnit translationUnit) {
        if (translationUnit == null) {
            return null;
        }
        URI locationURI = translationUnit.getLocationURI();
        if (locationURI == null) {
            return null;
        }

        final String fileLocation = locationURI.toString();
        IASTTranslationUnit astTranslationUnit;

        if (astTranslationUnitCache.containsKey(fileLocation)) {
            astTranslationUnit = astTranslationUnitCache.get(fileLocation);
        } else {
            try {
                index.acquireReadLock();
                astTranslationUnit = translationUnit.getAST(index, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);
                astTranslationUnitCache.put(fileLocation, astTranslationUnit);
            } catch (final CoreException | InterruptedException e) {
                astTranslationUnit = null;
            } finally {
                index.releaseReadLock();
            }
        }
        return astTranslationUnit;
    }

    public ASTRewrite getASTRewrite(final IASTNode node) {
        return node != null ? getASTRewrite(node.getTranslationUnit().getOriginatingTranslationUnit()) : null;
    }

    public ASTRewrite getASTRewrite(final ITranslationUnit translationUnit) {
        final IASTTranslationUnit astTranslationUnit = getASTTranslationUnit(translationUnit);
        ASTRewrite rewrite = null;

        if (astTranslationUnit != null) {
            if (astRewriteCache.containsKey(astTranslationUnit)) {
                rewrite = astRewriteCache.get(astTranslationUnit);
            } else {
                rewrite = ASTRewrite.create(astTranslationUnit);
                astRewriteCache.put(astTranslationUnit, rewrite);
            }
        }

        return rewrite;
    }

    public Change getChange() {
        final CompositeChange compositeChange = new CompositeChange("");
        for (final ASTRewrite rewrite : astRewriteCache.values()) {
            compositeChange.add(rewrite.rewriteAST());
        }
        return compositeChange;
    }

    public IIndex getIndex() {
        return index;
    }
}
