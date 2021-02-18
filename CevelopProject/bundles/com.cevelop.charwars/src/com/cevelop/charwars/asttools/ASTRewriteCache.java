package com.cevelop.charwars.asttools;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;


public class ASTRewriteCache {

    private final IIndex                               index;
    private final Map<String, IASTTranslationUnit>     astTranslationUnitCache;
    private final Map<IASTTranslationUnit, ASTRewrite> astRewriteCache;

    public ASTRewriteCache(IIndex index) {
        this.index = index;
        this.astTranslationUnitCache = new HashMap<>();
        this.astRewriteCache = new HashMap<>();
    }

    public IASTTranslationUnit getASTTranslationUnit(ITranslationUnit translationUnit) {
        final String fileLocation = translationUnit.getLocationURI().toString();
        IASTTranslationUnit astTranslationUnit;

        if (astTranslationUnitCache.containsKey(fileLocation)) {
            astTranslationUnit = astTranslationUnitCache.get(fileLocation);
        } else {
            try {
                astTranslationUnit = translationUnit.getAST(index, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);
                astTranslationUnitCache.put(fileLocation, astTranslationUnit);
            } catch (final CoreException e) {
                astTranslationUnit = null;
            }
        }
        return astTranslationUnit;
    }

    public ASTRewrite getASTRewrite(ITranslationUnit translationUnit) {
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
