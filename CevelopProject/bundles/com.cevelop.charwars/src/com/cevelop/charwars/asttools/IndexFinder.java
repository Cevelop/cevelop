package com.cevelop.charwars.asttools;

import org.eclipse.cdt.codan.core.cxx.CxxAstUtils;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBinding;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.index.IndexFilter;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.charwars.utils.ErrorLogger;


public class IndexFinder {

    public static void findDefinitions(IASTName name, ASTRewriteCache rewriteCache, ResultHandler resultHandler) {
        findNames(name, rewriteCache, resultHandler, IIndex.FIND_DEFINITIONS);
    }

    public static void findDeclarations(IASTName name, ASTRewriteCache rewriteCache, ResultHandler resultHandler) {
        findNames(name, rewriteCache, resultHandler, IIndex.FIND_DECLARATIONS);
    }

    public static void findAllOccurrences(IASTName name, ASTRewriteCache rewriteCache, ResultHandler resultHandler) {
        findNames(name, rewriteCache, resultHandler, IIndex.FIND_ALL_OCCURRENCES);
    }

    private static void findNames(IASTName name, ASTRewriteCache rewriteCache, ResultHandler resultHandler, int flags) {
        try {
            IIndex index = rewriteCache.getIndex();
            IIndexBinding adaptedBinding = index.adaptBinding(name.resolveBinding());
            IIndexName[] indexNames = index.findNames(adaptedBinding, flags);

            for (IIndexName indexName : indexNames) {
                ITranslationUnit translationUnit = CxxAstUtils.getTranslationUnitFromIndexName(indexName);
                if (translationUnit == null) {
                    continue;
                }

                IASTTranslationUnit astTranslationUnit = rewriteCache.getASTTranslationUnit(translationUnit);
                ASTRewrite rewrite = rewriteCache.getASTRewrite(translationUnit);

                IASTName declarationName = (IASTName) ASTAnalyzer.getMarkedNode(astTranslationUnit, indexName.getNodeOffset(), indexName
                        .getNodeLength());
                if (resultHandler.handleResult(declarationName, rewrite) == IndexFinderInstruction.ABORT_SEARCH) {
                    break;
                }
            }
        } catch (CoreException e) {
            ErrorLogger.log("Search for names with Indexer failed", e);
            throw new RuntimeException(e);
        }
    }

    public static enum IndexFinderInstruction {
        CONTINUE_SEARCH, ABORT_SEARCH
    }

    public static interface ResultHandler {

        IndexFinderInstruction handleResult(IASTName name, ASTRewrite rewrite);
    }

    public static IIndexBinding[] findBindings(IASTName name) throws Exception {
        IIndex index = name.getTranslationUnit().getIndex();
        ICPPBinding adaptedBinding = (ICPPBinding) index.adaptBinding(name.resolveBinding());

        if (adaptedBinding != null) {
            return index.findBindings(adaptedBinding.getQualifiedNameCharArray(), IndexFilter.ALL_DECLARED, null);
        } else {
            return new IIndexBinding[0];
        }
    }
}
