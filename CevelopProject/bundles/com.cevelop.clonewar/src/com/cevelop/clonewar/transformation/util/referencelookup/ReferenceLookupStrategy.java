package com.cevelop.clonewar.transformation.util.referencelookup;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.core.runtime.CoreException;


/**
 * Lookup strategy to find references of a name over all projects. For example,
 * to find function calls or type references.
 *
 * @author ythrier(at)hsr.ch
 *
 * @param <T>
 * The expression type which refers to the call, e.g.
 * {@link ICPPASTFunctionCallExpression}.
 */
public interface ReferenceLookupStrategy<T> {

    /**
     * Find all references of the given name over the projects in the workspace.
     *
     * @param name
     * Name to find the references.
     * @return List of all calls.
     * @throws CModelException
     * Model exception.
     * @throws CoreException
     * Core exception.
     */
    public List<T> findAllReferences(IASTName name) throws CModelException, CoreException;

    /**
     * Return the translation unit which is associated to the given reference.
     *
     * @param reference
     * Reference to get the associated translation unit.
     * @return Translation unit.
     */
    public IASTTranslationUnit getTranslationUnitOf(T reference);
}
