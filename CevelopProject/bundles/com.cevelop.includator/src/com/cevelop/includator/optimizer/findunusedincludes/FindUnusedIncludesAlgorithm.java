/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.optimizer.findunusedincludes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.index.IIndexInclude;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.cxxelement.Declaration;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.dependency.FirstLastElementIncludePath;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.helpers.IncludatorStatus;
import com.cevelop.includator.helpers.IncludeHelper;
import com.cevelop.includator.helpers.comparators.IndexFirstLastElementIncludePathComparator;
import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.preferences.IncludatorPropertyManager;
import com.cevelop.includator.resources.IncludatorFile;


public class FindUnusedIncludesAlgorithm extends Algorithm {

    protected IncludatorFile                             file;
    private IncludeCoverageInfo                          includeCoverageInfo;
    private boolean                                      suggestCoveredIncludes;
    private TreeSet<FirstLastElementIncludePath>         paths;
    private Collection<IASTPreprocessorIncludeStatement> includesIgnoredByIndex;

    @Override
    protected void run() {
        try {
            file = startingPoint.getFile();
            if (!shouldRunAlg()) {
                return;
            }

            Collection<DeclarationReference> declarationReferences = file.getDeclarationReferences();
            monitorWorked(0.3);

            initAllPaths(declarationReferences);
            monitorWorked(0.3);

            OptimalIncludeSelector optimalIncludeSelector = new OptimalIncludeSelector(file, paths);
            Collection<IIndexInclude> unnecessaryIncludes = optimalIncludeSelector.getUnnecessaryIncludes();
            includeCoverageInfo = optimalIncludeSelector.getReplacedIncludes();
            monitorWorked(0.1);

            suggestCoveredIncludes = suggestCoveredIncludes();

            createIndexIncludeResults(unnecessaryIncludes);

            List<IIndexInclude> allIncludes = optimalIncludeSelector.getAllIndexIncludes();
            initIncludesIgnoredByIndex(allIncludes);
            createNonIndexIncludeResults(includesIgnoredByIndex);
            monitorWorked(0.1);

        } catch (CoreException e) {
            throw new IncludatorException("Problem while finding unused includes.", e);
        }
    }

    /**
     * Includes contained in the AST which are ignored by the indexer are exact duplicates and not represented in the indexer. They should be proposed
     * for removal anyway.
     *
     * @param allIndexIncludes
     * @return
     */
    private void initIncludesIgnoredByIndex(List<IIndexInclude> allIndexIncludes) {
        Map<Integer, IASTPreprocessorIncludeStatement> astIncludeMap = new LinkedHashMap<>();
        for (IASTPreprocessorIncludeStatement curAstInclude : file.getIncludes()) {
            astIncludeMap.put(curAstInclude.getName().getFileLocation().getNodeOffset(), curAstInclude);
        }
        for (IIndexInclude curIndexInclude : allIndexIncludes) {
            try {
                astIncludeMap.remove(curIndexInclude.getNameOffset());
            } catch (CoreException e) {
                continue;
            }
        }
        includesIgnoredByIndex = astIncludeMap.values();
    }

    private boolean shouldRunAlg() throws CoreException {
        if (FileHelper.getIndexFile(file) == null) {
            String logMessage = "Problem when finding unused includes for file \"" + file.getFilePath() + "\": no IIndexFile exists.";
            IncludatorPlugin.logStatus(new IncludatorStatus(new IncludatorException(logMessage)), file);
            return false;
        }
        return true;
    }

    protected void createIndexIncludeResults(Collection<IIndexInclude> unnecessaryIncludes) throws CoreException {
        for (IIndexInclude curInclude : unnecessaryIncludes) {
            if (!shouldSkipToPropose(curInclude)) {
                addIncludeSuggestion(curInclude);
            }
        }
    }

    private void addIncludeSuggestion(IIndexInclude includeToAdd) throws CoreException {
        IASTPreprocessorIncludeStatement unusedIncludeNode = IncludeHelper.findIncludeForLocation(file, includeToAdd.getNameOffset());
        if (unusedIncludeNode != null) {
            String reason = createCoveredReasonMessage(includeToAdd);
            addSuggestion(new UnusedIncludeSuggestion(unusedIncludeNode, file, FindUnusedIncludesAlgorithm.class, reason));
        }
    }

    private boolean shouldSikpBecauseOfSameSourceAndTargetName(IIndexInclude include) {
        String propertyName = IncludatorPropertyManager.DONT_SUGGEST_REMOVAL_OF_NAME_CORRELATING_HEADER;
        if (!IncludatorPropertyManager.getBooleanProperty(startingPoint.getProject(), propertyName)) {
            return false;
        }
        try {
            if (!include.isResolved()) {
                return false;
            }
            String includingPath = FileHelper.uriToStringPath(include.getIncludedBy().getLocation().getURI());
            String includedPath = FileHelper.uriToStringPath(include.getIncludesLocation().getURI());
            String includingFileNameWithoutExtension = new Path(includingPath).removeFileExtension().lastSegment();
            String includedFileNameWithoutExtension = new Path(includedPath).removeFileExtension().lastSegment();
            return includingFileNameWithoutExtension.equals(includedFileNameWithoutExtension);
        } catch (CoreException e) {
            IncludatorPlugin.logStatus(new IncludatorStatus(e), (String) null);
        }
        return false;
    }

    private boolean shouldSkipToPropose(IIndexInclude include) throws CoreException {
        boolean isCovered = includeCoverageInfo.isCoveredByOtherIncludes(include);
        return (isCovered && !suggestCoveredIncludes) || shouldSikpBecauseOfSameSourceAndTargetName(include);
    }

    private void createNonIndexIncludeResults(Collection<IASTPreprocessorIncludeStatement> includesIgnoredByIndex) {
        for (IASTPreprocessorIncludeStatement curInclude : includesIgnoredByIndex) {
            addSuggestion(new UnusedIncludeSuggestion(curInclude, file, FindUnusedIncludesAlgorithm.class, "Duplicate include."));
        }
    }

    private boolean suggestCoveredIncludes() {
        return IncludatorPropertyManager.getBooleanProperty(startingPoint.getProject(),
                IncludatorPropertyManager.SUGGEST_REMOVAL_OF_COVERED_INCLUDES_PREFERENCE_NAME);
    }

    private String createCoveredReasonMessage(IIndexInclude include) throws CoreException {
        if (!includeCoverageInfo.isCoveredByOtherIncludes(include)) {
            return "No reference requires include.";
        }
        StringBuffer reason = new StringBuffer();
        reason.append("Covered through:");
        for (IIndexInclude replacingInclude : includeCoverageInfo.getCoveringIncludes(include)) {
            reason.append(" ").append(replacingInclude.getName());
        }
        return reason.toString();
    }

    private void initAllPaths(Collection<DeclarationReference> references) {
        paths = new TreeSet<>(new IndexFirstLastElementIncludePathComparator());
        Collection<IncludatorFile> allreadyAddedTargetFiles = new HashSet<>();
        for (DeclarationReference curReference : references) {
            Collection<DeclarationReferenceDependency> dependencies = curReference.getRequiredDependencies();
            if (!containsUnresolvedDependency(dependencies, curReference)) {
                addPaths(dependencies, allreadyAddedTargetFiles);
            }
        }
    }

    private void addPaths(Collection<DeclarationReferenceDependency> dependencies, Collection<IncludatorFile> allreadyAddedTargetFiles) {
        for (DeclarationReferenceDependency curDependency : dependencies) {
            Declaration declaration = curDependency.getDeclaration();
            if (declaration == null) {
                continue;
            }
            IncludatorFile targetFile = declaration.getFile();
            if (!curDependency.isLocalDependency() && !allreadyAddedTargetFiles.contains(targetFile)) {
                allreadyAddedTargetFiles.add(targetFile);
                Collection<FirstLastElementIncludePath> pathsToAdd = curDependency.getFirstLastElementIncludePaths();
                if (curDependency.hadProblemWhileReslovingFirstLastElementPaths()) {
                    handleUnincludedDeclRefDependency(curDependency);
                }
                paths.addAll(pathsToAdd);
            }
        }
    }

    protected void handleUnincludedDeclRefDependency(DeclarationReferenceDependency curDependency) {
        String startFilePath = curDependency.getDeclarationReference().getFile().getSmartPath();
        String endFilePath = curDependency.getDeclaration().getFile().getSmartPath();
        String logMessage = "Could not find any path from file \"" + startFilePath + "\" to \"" + endFilePath + "\" (when following #includes).";
        IncludatorPlugin.logStatus(new IncludatorStatus(IStatus.WARNING, logMessage), file);
    }

    private boolean containsUnresolvedDependency(Collection<DeclarationReferenceDependency> dependencies, DeclarationReference reference) {
        if (reference.hadProblemsWhileResolving()) {
            String path = reference.getFile().getSmartPath();
            String rawSignature = reference.getName();
            String positionString = FileHelper.getExtendedPositionString(reference.getASTNode());
            String leadingMessage = (dependencies.size() == 0) ? "No declaration found" : "Could not resolve reference";
            String logMessage = leadingMessage + " for \"" + rawSignature + "\" in file " + path + positionString + ".";
            IncludatorPlugin.logStatus(new IncludatorStatus(IStatus.WARNING, logMessage), file);
            return true;
        }
        return false;
    }

    @Override
    public String getInitialProgressMonitorMessage(String resourceName) {
        return "Finding unused includes in " + resourceName;
    }

    @Override
    public Set<Class<? extends Algorithm>> getInvolvedAlgorithmTypes() {
        HashSet<Class<? extends Algorithm>> involvedAlgorithms = new HashSet<>();
        involvedAlgorithms.add(FindUnusedIncludesAlgorithm.class);
        return involvedAlgorithms;
    }

    @Override
    public AlgorithmScope getScope() {
        return AlgorithmScope.EDITOR_SCOPE;
    }

    @Override
    public void reset() {
        file = null;
        includeCoverageInfo = null;
        suggestCoveredIncludes = false;
        paths = null;
        includesIgnoredByIndex = null;
        super.reset();
    }
}
