package com.cevelop.macronator.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.ExpansionOverlapsBoundaryException;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexMacro;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.index.IndexFilter;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.parser.IToken;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.cevelop.macronator.MacronatorPlugin;


/**
 * Implements the macro classification process.
 *
 */
public class MacroClassifier {

    private final IASTPreprocessorMacroDefinition            macroDefinition;
    private final MacroProperties                            macroProperties;
    private final Map<ITranslationUnit, IASTTranslationUnit> astCache;

    public MacroClassifier(final IASTPreprocessorMacroDefinition macroDefinition) {
        this(macroDefinition, new HashMap<>());
    }

    public MacroClassifier(final IASTPreprocessorMacroDefinition macroDefinition, final Map<ITranslationUnit, IASTTranslationUnit> astCache) {
        this.macroDefinition = macroDefinition;
        macroProperties = new MacroProperties(macroDefinition);
        this.astCache = astCache;
    }

    public boolean isObjectLike() {
        return macroProperties.isObjectStyle() && !isConfigurational();
    }

    public boolean isFunctionLike() {
        return !macroProperties.isObjectStyle() && !isConfigurational();
    }

    public boolean areDependenciesValid() {
        final List<String> freeVariables = macroProperties.getFreeVariables();
        for (final String variableName : freeVariables) {
            if (!inScope(variableName) || isPointer(variableName)) {
                return false;
            }
        }
        return true;
    }

    private boolean inScope(final String variableName) {
        IASTTranslationUnit translationUnit = macroDefinition.getTranslationUnit();
        final IIndexMacro[] macros = getMacroDefinitions(translationUnit, variableName);
        final boolean isMacro = macros.length == 1;
        final boolean inScope = translationUnit.getScope().find(variableName, translationUnit).length > 0;
        return isMacro || inScope;
    }

    private boolean isPointer(final String variableName) {
        IASTTranslationUnit translationUnit = macroDefinition.getTranslationUnit();
        final IBinding[] find = translationUnit.getScope().find(variableName, translationUnit);
        for (final IBinding binding : find) {
            if (binding instanceof IVariable) {
                final IVariable var = (IVariable) binding;
                final IType type = var.getType();
                if (type instanceof IPointerType) {
                    return true;
                }
            }
        }
        return false;
    }

    private IIndexMacro[] getMacroDefinitions(final IASTTranslationUnit translationUnit, final String variableName) {
        try {
            return translationUnit.getIndex().findMacros(variableName.toCharArray(), IndexFilter.ALL, new NullProgressMonitor());
        } catch (final CoreException e) {
            MacronatorPlugin.log(e);
        }
        return new IIndexMacro[0];
    }

    public boolean isConfigurational() {
        for (final IIndexName macroReference : macroProperties.getReferences()) {
            if (isInsideConditionalPreprocessorStatement(macroReference)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInsideConditionalPreprocessorStatement(final IIndexName macroReference) {
        final IASTTranslationUnit referenceAst = getTranslationUnit(macroReference);
        if (referenceAst == null) {
            return false;
        }
        final List<IASTPreprocessorStatement> statements = Arrays.asList(referenceAst.getAllPreprocessorStatements());
        for (final IASTPreprocessorStatement statement : statements) {
            if (!isMacroDefinition(statement) && isReferenceContainedInStatement(macroReference, statement)) {
                return true;
            }
        }
        return false;
    }

    private boolean isMacroDefinition(final IASTPreprocessorStatement statement) {
        return statement instanceof IASTPreprocessorMacroDefinition;
    }

    private IASTTranslationUnit getTranslationUnit(final IIndexName indexName) {
        final Path filePath = new Path(indexName.getFileLocation().getFileName());
        final ICElement celement = CoreModel.getDefault().create(filePath);
        if (celement == null) {
            return null;
        }
        if (!(celement instanceof ITranslationUnit)) {
            return null;
        }
        final ITranslationUnit referenceTU = (ITranslationUnit) celement;

        try {
            final IIndex index = CCorePlugin.getIndexManager().getIndex(celement.getCProject());
            if (!astCache.containsKey(referenceTU)) {
                astCache.put(referenceTU, referenceTU.getAST(index, ITranslationUnit.AST_SKIP_ALL_HEADERS));
            }
        } catch (final CoreException e) {
            MacronatorPlugin.log(e);
            return null;
        }
        return astCache.get(referenceTU);

    }

    private boolean isReferenceContainedInStatement(final IIndexName macroReference, final IASTPreprocessorStatement statement) {
        try {
            IToken token = statement.getSyntax();
            while (token != null) {
                if (token.getType() == IToken.tIDENTIFIER) {
                    if (token.getImage().equals(new String(macroReference.getSimpleID()))) {
                        return true;
                    }
                }
                token = token.getNext();
            }
        } catch (final ExpansionOverlapsBoundaryException e) {
            MacronatorPlugin.log(e);
        }
        return false;
    }
}
