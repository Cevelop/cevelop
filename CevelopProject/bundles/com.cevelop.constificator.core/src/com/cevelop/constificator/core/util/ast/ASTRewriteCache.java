package com.cevelop.constificator.core.util.ast;

import java.net.URI;
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

    private Map<IASTTranslationUnit, ASTRewrite> astRewriteCache;
    private Map<String, IASTTranslationUnit>     astTranslationUnitCache;
    private IIndex                               index;

    public ASTRewriteCache(IIndex index) {
        this.index = index;
        astTranslationUnitCache = new HashMap<>();
        astRewriteCache = new HashMap<>();
    }

    public ASTRewrite getASTRewrite(ITranslationUnit translationUnit) {
        IASTTranslationUnit astTranslationUnit = getASTTranslationUnit(translationUnit);
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

    public IASTTranslationUnit getASTTranslationUnit(ITranslationUnit translationUnit) {
        if (translationUnit == null) {
            return null;
        }
        URI locationURI = translationUnit.getLocationURI();
        if (locationURI == null) {
            return null;
        }

        String fileLocation = locationURI.toString();
        IASTTranslationUnit astTranslationUnit;

        if (astTranslationUnitCache.containsKey(fileLocation)) {
            astTranslationUnit = astTranslationUnitCache.get(fileLocation);
        } else {
            try {
                astTranslationUnit = translationUnit.getAST(index, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);
                astTranslationUnitCache.put(fileLocation, astTranslationUnit);
            } catch (CoreException e) {
                astTranslationUnit = null;
            }
        }
        return astTranslationUnit;
    }

    public Change getChange() {
        CompositeChange compositeChange = new CompositeChange("");
        for (ASTRewrite rewrite : astRewriteCache.values()) {
            compositeChange.add(rewrite.rewriteAST());
        }
        return compositeChange;
    }

    public IIndex getIndex() {
        return index;
    }
}
