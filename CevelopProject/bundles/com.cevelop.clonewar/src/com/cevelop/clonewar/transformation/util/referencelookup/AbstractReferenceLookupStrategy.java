package com.cevelop.clonewar.transformation.util.referencelookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.ui.refactoring.utils.ExpressionFinder;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringContext;


/**
 * Abstract base class for reference lookup strategies providing helper methods
 * for index lookup.
 *
 * @author ythrier(at)hsr.ch
 *
 * @param <T>
 * The expression type which refers to the call, e.g.
 * {@link ICPPASTFunctionCallExpression}.
 */

public abstract class AbstractReferenceLookupStrategy<T> implements ReferenceLookupStrategy<T> {

    private Map<IFile, IASTTranslationUnit> loadedTUnits          = new HashMap<>();
    private Map<T, IASTTranslationUnit>     unitToCallAssociation = new HashMap<>();
    protected final CRefactoringContext     context;

    protected AbstractReferenceLookupStrategy(CRefactoringContext astCache) {
        this.context = astCache;
    }

    /**
     * Add a mapping for the reference to the translation unit.
     *
     * @param reference
     * Reference.
     * @param callIndex
     * Call index.
     * @throws CoreException
     * Core exception.
     */
    protected void addUnitToCallMapping(T reference, IIndexName callIndex) throws CoreException {
        unitToCallAssociation.put(reference, loadTUnit(callIndex));
    }

    /**
     * Return the index for a project.
     *
     * @param project
     * Project.
     * @return Index.
     * @throws CoreException
     * Core exception.
     */
    protected IIndex getIndexFor(ICProject project) throws CoreException {
        return CCorePlugin.getIndexManager().getIndex(project);
    }

    /**
     * Find the call.
     *
     * @param candidate
     * Candidate AST node.
     * @return Call expression found.
     */
    @SuppressWarnings("unchecked")
    protected T findCall(IASTNode candidate) {
        IASTNode node = candidate;
        while (node != null) {
            if (isReferenceExpression(node)) return (T) node;
            node = node.getParent();
        }
        return null;
    }

    /**
     * Check if the given node is the reference expression that is searched.
     *
     * @param node
     * Node to check.
     * @return True if the node is the searched expression, otherwise false.
     */
    protected abstract boolean isReferenceExpression(IASTNode node);

    /**
     * Lookup a candidate for the call.
     *
     * @param callIndex
     * Call index.
     * @return Candidate.
     * @throws CoreException
     * Core exception.
     */
    protected IASTName findCandidate(IIndexName callIndex) throws CoreException {
        return ExpressionFinder.findExpressionInTranslationUnit(loadTUnit(callIndex), callIndex);
    }

    /**
     * Get the binding from the index for reference.
     *
     * @param index
     * Index.
     * @param name
     * AST Name.
     * @return Binding.
     * @throws CoreException
     * Core exception.
     */
    protected IBinding getBinding(IIndex index, IASTName name) throws CoreException {
        return index.findBinding(name);
    }

    /**
     * Return the projects available.
     *
     * @return Array of projects.
     * @throws CModelException
     * Model exception.
     */
    protected ICProject[] getProjects() throws CModelException {
        return CCorePlugin.getDefault().getCoreModel().getCModel().getCProjects();
    }

    /**
     * Load a translation unit from a file.
     *
     * @param file
     * File.
     * @return Translation unit.
     * @throws CoreException
     * Core exception.
     */
    private IASTTranslationUnit loadUnitFrom(IFile file) throws CoreException {
        final ITranslationUnit tu = CoreModelUtil.findTranslationUnit(file);
        return context.getAST(tu, new NullProgressMonitor());
    }

    /**
     * Load the translation unit if not already available.
     *
     * @param callIndex
     * Call index.
     * @return Translation unit loaded or the cached one.
     * @throws CoreException
     * Core exception.
     */
    private IASTTranslationUnit loadTUnit(IIndexName callIndex) throws CoreException {
        IFile file = getFileFor(callIndex);
        if (!loadedTUnits.containsKey(file)) loadedTUnits.put(file, loadUnitFrom(file));
        return loadedTUnits.get(file);
    }

    /**
     * Return the file for the call index.
     *
     * @param callIndex
     * Call index.
     * @return File.
     */
    private IFile getFileFor(IIndexName callIndex) {
        return getWorkspaceRoot().getFileForLocation(getPathFor(callIndex.getFileLocation()));
    }

    /**
     * Return a path for the file location.
     *
     * @param fileLocation
     * File location.
     * @return Path.
     */
    private IPath getPathFor(IASTFileLocation fileLocation) {
        return new Path(fileLocation.getFileName());
    }

    /**
     * Returns the workspace root.
     *
     * @return Workspace root.
     */
    private IWorkspaceRoot getWorkspaceRoot() {
        return ResourcesPlugin.getWorkspace().getRoot();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASTTranslationUnit getTranslationUnitOf(T reference) {
        return unitToCallAssociation.get(reference);
    }

    /**
     * Process the call.
     *
     * @param callIndex
     * Call index.
     * @param calls
     * Call list.
     * @throws CoreException
     * Core exception.
     */
    protected void processCall(IIndexName callIndex, List<T> calls) throws CoreException {
        T call = findCall(findCandidate(callIndex));
        if (call != null) {
            calls.add(call);
            addUnitToCallMapping(call, callIndex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findAllReferences(IASTName name) throws CModelException, CoreException {
        List<T> calls = new ArrayList<>();
        for (ICProject project : getProjects()) {
            IIndex index = getIndexFor(project);
            try {
                index.acquireReadLock();
                processCandidates(name, index, calls);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                index.releaseReadLock();
            }
        }
        return calls;
    }

    /**
     * Process possible candidate references.
     *
     * @param name
     * Name of the reference.
     * @param index
     * Index.
     * @param calls
     * List of calls to add.
     * @throws CoreException
     * Core exception.
     */
    protected abstract void processCandidates(IASTName name, IIndex index, List<T> calls) throws CoreException;
}
