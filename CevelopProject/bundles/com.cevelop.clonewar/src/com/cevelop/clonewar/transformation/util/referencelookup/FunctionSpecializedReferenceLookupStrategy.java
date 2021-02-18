package com.cevelop.clonewar.transformation.util.referencelookup;

import java.util.Collections;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.internal.ui.viewsupport.IndexUI;
import org.eclipse.core.runtime.CoreException;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringContext;


/**
 * Lookup strategy for function references (calls) of specialized (already
 * template) functions.
 *
 * @author ythrier(at)hsr.ch
 */

public class FunctionSpecializedReferenceLookupStrategy extends AbstractReferenceLookupStrategy<ICPPASTFunctionCallExpression> {

    public FunctionSpecializedReferenceLookupStrategy(CRefactoringContext context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void processCandidates(IASTName name, IIndex index, List<ICPPASTFunctionCallExpression> calls) throws CoreException {
        for (IBinding binding : (List<? extends IBinding>) findSpecializations(name, index)) {
            IIndexName[] callIndexes = index.findNames(binding, IIndex.FIND_ALL_OCCURRENCES);
            for (IIndexName callIndex : callIndexes) {
                processCall(callIndex, calls);
            }
        }
    }

    /**
     * Find the specialized function calls.
     *
     * @param name
     * AST Name.
     * @param index
     * Index entry.
     * @return List of specialized candidates.
     * @throws CoreException
     * Core exception.
     */
    private List<? extends IBinding> findSpecializations(IASTName name, IIndex index) throws CoreException {
        IBinding binding = getBinding(index, name);
        if (binding == null) {
            return Collections.emptyList();
        }
        return IndexUI.findSpecializations(context.getIndex(), binding);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isReferenceExpression(IASTNode node) {
        return (node instanceof ICPPASTFunctionCallExpression);
    }
}
