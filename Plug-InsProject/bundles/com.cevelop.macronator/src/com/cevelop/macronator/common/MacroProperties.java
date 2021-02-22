package com.cevelop.macronator.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTFunctionStyleMacroParameter;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorFunctionStyleMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.parser.IToken;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.macronator.MacronatorPlugin;


public class MacroProperties {

    private final IASTPreprocessorMacroDefinition macroDefinition;

    public MacroProperties(final IASTPreprocessorMacroDefinition macroDefinition) {
        this.macroDefinition = macroDefinition;
    }

    public boolean isObjectStyle() {
        /*
         * this check is required because IASTFunctionStyleMacroDefintion
         * inherits from ObjectStyleMacroDefinition :S
         */
        return !(macroDefinition instanceof IASTPreprocessorFunctionStyleMacroDefinition);
    }

    public List<String> getFreeVariables() {
        final List<String> freeVariables = new ArrayList<>();
        final String expansion = macroDefinition.getExpansion();
        final LexerAdapter lexerAdapter = new LexerAdapter(expansion);
        while (!lexerAdapter.atEndOfInput()) {
            final IToken token = lexerAdapter.nextToken();
            if (token.getType() == IToken.tIDENTIFIER) {
                if (!freeVariables.contains(token.getImage())) {
                    freeVariables.add(token.getImage());
                }
            }
        }
        freeVariables.removeAll(getParameters());
        return freeVariables;
    }

    private List<String> getParameters() {
        if (isObjectStyle()) {
            return Collections.emptyList();
        }
        final List<String> parameters = new ArrayList<>();
        for (final IASTFunctionStyleMacroParameter parameter : ((IASTPreprocessorFunctionStyleMacroDefinition) macroDefinition).getParameters()) {
            parameters.add(parameter.getParameter());
        }
        return parameters;
    }

    public boolean suggestionsSuppressed() {
        final IProject project = macroDefinition.getTranslationUnit().getOriginatingTranslationUnit().getCProject().getProject();
        final SuppressedMacros suppressedMacros = new SuppressedMacros(project);
        return suppressedMacros.isSuppressed(macroDefinition.getName().toString());
    }

    public IIndexName[] getReferences() {
        try {
            final IIndex index = macroDefinition.getTranslationUnit().getIndex();
            final IIndexBinding binding = index.findBinding(macroDefinition.getName());
            return index.findNames(binding, IIndex.FIND_REFERENCES);
        } catch (final CoreException e) {
            MacronatorPlugin.log(e, "could not obtain macro references");
        }
        return new IIndexName[0];
    }
}
