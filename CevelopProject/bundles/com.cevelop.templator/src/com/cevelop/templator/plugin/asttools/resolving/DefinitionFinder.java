package com.cevelop.templator.plugin.asttools.resolving;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNodeSelector;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPFieldSpecialization;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.templator.plugin.asttools.IIndexAction;
import com.cevelop.templator.plugin.asttools.IndexAction;
import com.cevelop.templator.plugin.asttools.data.ASTCache;
import com.cevelop.templator.plugin.logger.TemplatorException;


/** Find the definition of a binding in the AST or index. */
public class DefinitionFinder {

    private IIndex              index;
    private IASTTranslationUnit ast;
    private ASTCache            astCache;

    public DefinitionFinder(IIndex index, IASTTranslationUnit ast) {
        this.index = index;
        this.ast = ast;
        astCache = new ASTCache(index);
    }

    public IASTName findDefinition(IBinding binding) throws TemplatorException {
        if (binding == null) {
            return null;
        }
        if (binding instanceof CPPFieldSpecialization) {
            CPPFieldSpecialization fieldSpec = (CPPFieldSpecialization) binding;
            binding = fieldSpec.getSpecializedBinding();
        }
        // for a problem binding with only one candidate, the candidate is taken
        if (binding instanceof IProblemBinding) {
            IProblemBinding problemBinding = (IProblemBinding) binding;
            IBinding[] candidateBindings = problemBinding.getCandidateBindings();
            if (candidateBindings.length != 1) {
                throw new TemplatorException("More than one possible candidate for " + binding);
            }
            binding = candidateBindings[0];
        }

        // first check if the binding is in the AST
        IASTName[] definitionNames = findDefinitionInAST(binding);
        // if not search for it in the index, parse the containing file, get the AST and search the definition there
        if (definitionNames.length == 0) {
            definitionNames = findDefinitionInIndex(binding);
        }
        if (definitionNames.length != 1) {
            throw new TemplatorException("Found " + definitionNames.length + " definitions for " + binding + ", expected 1");
        }

        return definitionNames[0];
    }

    private IASTName[] findDefinitionInAST(IBinding specializedTemplateBinding) {
        IASTName[] definitionNames = ast.getDefinitionsInAST(specializedTemplateBinding);
        return definitionNames;
    }

    private IASTName[] findDefinitionInIndex(final IBinding binding) throws TemplatorException {
        try {
            if (index != null) {

            }
            IIndexName indexNameForBinding = findIndexName(binding);
            IASTTranslationUnit astForExternalFile = astCache.getAST(indexNameForBinding.getFile().getLocation());
            IASTName[] astDefinitions = astForExternalFile.getDefinitionsInAST(binding);
            // if still nothing found, get definition via nodeselector as fallback solution
            if (astDefinitions.length == 0) {
                return new IASTName[] { findDefinitionWithNodeSelector(astForExternalFile, indexNameForBinding) };
            }
            return astDefinitions;
        } catch (CoreException e) {
            throw new TemplatorException("Failed to read names from index", e);
        }

    }

    private static IASTName findDefinitionWithNodeSelector(IASTTranslationUnit astForExternalFile, IIndexName indexNameForTemplateBinding)
            throws TemplatorException {
        int offset = indexNameForTemplateBinding.getNodeOffset();
        int length = indexNameForTemplateBinding.getNodeLength();

        IASTNodeSelector nodeSelector = astForExternalFile.getNodeSelector(indexNameForTemplateBinding.getFileLocation().getFileName());
        IASTName functionDefinitionName = nodeSelector.findEnclosingName(offset, length);

        if (functionDefinitionName == null) {
            throw new TemplatorException("Could not find an enclosing name with offset " + offset + " and length " + length);
        }
        return functionDefinitionName;
    }

    private IIndexName findIndexName(final IBinding binding) throws TemplatorException {
        IIndexName[] foundIndexNames = IndexAction.<IIndexName[]>perform(index, new IIndexAction() {

            @SuppressWarnings("unchecked")
            @Override
            public IIndexName[] doAction(IIndex index) throws CoreException {
                return index.findNames(binding, IIndex.FIND_DEFINITIONS);
            }
        });

        if (foundIndexNames == null || foundIndexNames.length != 1) {
            throw new TemplatorException("Could not find index name for " + binding.getName() + ". Maybe try rebuilding the index.");
        }

        return foundIndexNames[0];
    }

}
