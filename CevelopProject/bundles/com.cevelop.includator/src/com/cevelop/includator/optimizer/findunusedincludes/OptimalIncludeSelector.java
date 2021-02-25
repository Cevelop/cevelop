/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.optimizer.findunusedincludes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.cdt.core.index.IIndexFile;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.index.IIndexInclude;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.dependency.FirstLastElementIncludePath;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.helpers.IncludeHelper;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.resources.IncludatorProject;


public class OptimalIncludeSelector {

    private final List<IIndexInclude>                     includes;
    private final Collection<FirstLastElementIncludePath> unprocessedIncludePaths;
    private final IncludatorProject                       project;
    private final ArrayList<IIndexInclude>                allIncludes;
    private final IncludeCoverageInfo                     coverageInfo;
    private ArrayList<FirstLastElementIncludePath>        removedPaths;
    private final String                                  startingFilePath;

    public IncludeCoverageInfo getReplacedIncludes() {
        return coverageInfo;
    }

    public OptimalIncludeSelector(IncludatorFile startingFile, Collection<FirstLastElementIncludePath> includePathList) throws CoreException {
        project = startingFile.getProject();
        IIndexFile indexFile = FileHelper.getIndexFile(startingFile);
        startingFilePath = startingFile.getFilePath();
        includes = getActiveIncludes(IncludatorPlugin.getIndexIncludeStore().getIncludes(indexFile, project.getIndex()));
        allIncludes = new ArrayList<>(includes);
        unprocessedIncludePaths = includePathList;
        coverageInfo = new IncludeCoverageInfo();
    }

    private ArrayList<IIndexInclude> getActiveIncludes(IIndexInclude[] allIncludes) {
        ArrayList<IIndexInclude> result = new ArrayList<>();
        for (IIndexInclude curInclude : allIncludes) {
            try {
                if (curInclude.isActive()) {
                    result.add(curInclude);
                }
            } catch (CoreException e) {
                continue;
            }
        }
        return result;
    }

    public List<IIndexInclude> getUnnecessaryIncludes() throws CoreException {
        runAlg();
        return Arrays.asList(includes.toArray(new IIndexInclude[0]));
    }

    private void runAlg() throws CoreException {
        removedPaths = new ArrayList<>();
        pickNameCorrelatingInclude();
        pickMandatoryIncludes();
        while (hasIncludeChoice()) {
            removedPaths.addAll(removeWorstChoice());
            pickMandatoryIncludes();
        }
    }

    private void pickNameCorrelatingInclude() {
        String extension = FileHelper.stringToPath(startingFilePath).getFileExtension();
        if (extension == null) {
            return;
        }
        String startingFilePathNoExtension = startingFilePath.substring(0, startingFilePath.length() - extension.length());
        if (startingFilePathNoExtension.isEmpty()) {
            return;
        }
        for (FirstLastElementIncludePath curPath : unprocessedIncludePaths) {
            try {
                IIndexInclude directlyIncluded = curPath.getFirstInclude();
                IPath directlyIncludedPath = FileHelper.uriToPath(directlyIncluded.getIncludesLocation().getURI());
                extension = directlyIncludedPath.getFileExtension();
                if (extension == null) {
                    return;
                }
                if ((startingFilePathNoExtension + extension).equals(directlyIncludedPath.toOSString())) {
                    pickInclude(directlyIncluded);
                    break;
                }
            } catch (CoreException e) {
                throw new IncludatorException(e);
            }
        }
    }

    private List<FirstLastElementIncludePath> removeWorstChoice() throws CoreException {
        Map<IncludatorFile, List<FirstLastElementIncludePath>> removeMap = new LinkedHashMap<>();
        IncludatorFile worstChoice = null;
        double penalty = -1;
        for (FirstLastElementIncludePath curPath : unprocessedIncludePaths) {
            IncludatorFile directlyIncludedFile = IncludeHelper.findIncludedFile(curPath.getFirstInclude(), project);
            if (!addToRemoveMap(removeMap, curPath, directlyIncludedFile)) {
                continue;
            }
            double curentPenalty = calculatePenalty(directlyIncludedFile);
            if (curentPenalty >= penalty || worstChoice == null) {
                penalty = curentPenalty;
                worstChoice = directlyIncludedFile;
            }
        }
        List<FirstLastElementIncludePath> removedPaths = removeMap.get(worstChoice);
        unprocessedIncludePaths.removeAll(removedPaths);
        return removedPaths;
    }

    private boolean addToRemoveMap(Map<IncludatorFile, List<FirstLastElementIncludePath>> removeMap, FirstLastElementIncludePath path,
            IncludatorFile directlyIncludedFile) {
        if (removeMap.containsKey(directlyIncludedFile)) {
            removeMap.get(directlyIncludedFile).add(path);
            return false;
        } else {
            ArrayList<FirstLastElementIncludePath> list = new ArrayList<>();
            list.add(path);
            removeMap.put(directlyIncludedFile, list);
            return true;
        }
    }

    private double calculatePenalty(IncludatorFile file) throws CoreException {
        double amountRecusivelyIncludedFiles = IncludatorPlugin.getRecursiveIndexIncludeStore().getRecursiveIncludeRelations(file).size();
        double weight = 2 + amountRecusivelyIncludedFiles;
        double countTargetFilesIncluded = calculateTargetFilesIncluded(file);
        return weight / (countTargetFilesIncluded * countTargetFilesIncluded);
    }

    private int calculateTargetFilesIncluded(IncludatorFile startingFile) throws CoreException {
        HashSet<String> set = new HashSet<>();
        for (FirstLastElementIncludePath curPath : unprocessedIncludePaths) {
            if (FileHelper.uriToStringPath(curPath.getFirstInclude().getIncludesLocation().getURI()).equals(startingFile.getFilePath())) {
                set.add(FileHelper.uriToStringPath(curPath.getLastInclude().getIncludesLocation().getURI()));
            }
        }
        return set.size();
    }

    private boolean hasIncludeChoice() {
        return !unprocessedIncludePaths.isEmpty();
    }

    private void pickMandatoryIncludes() throws CoreException {
        // if the map doesn't contain a file as key, this means it is encountered the first time.
        // if the map already contains a file as key and the entry's value is null, this means several paths with that target file were already
        // processed.
        // if, after processing, a path (value) is not null, the target file (key) is only reachable trough that path.
        Map<String, FirstLastElementIncludePath> singleIncludePathMap = new LinkedHashMap<>();

        for (FirstLastElementIncludePath curPath : unprocessedIncludePaths) {
            String targetFile = FileHelper.uriToStringPath(curPath.getLastInclude().getIncludesLocation().getURI());
            if (singleIncludePathMap.containsKey(targetFile)) {
                singleIncludePathMap.put(targetFile, null);
            } else {
                singleIncludePathMap.put(targetFile, curPath);
            }
        }

        for (Entry<String, FirstLastElementIncludePath> entry : singleIncludePathMap.entrySet()) {
            if (entry.getValue() != null) {
                IIndexInclude mandatoryInclude = entry.getValue().getFirstInclude();
                pickInclude(mandatoryInclude);
            }
        }
    }

    private void pickInclude(IIndexInclude include) throws CoreException {
        includes.remove(include);
        List<FirstLastElementIncludePath> pathsMatchingFirstInclude = findPathsMatchingFirstInclude(include);
        for (FirstLastElementIncludePath curPath : pathsMatchingFirstInclude) {
            String lastIncludedFile = FileHelper.uriToStringPath(curPath.getLastInclude().getIncludesLocation().getURI());
            removeAllPathsMatchingLastIncludedFile(lastIncludedFile, include);

            Iterator<FirstLastElementIncludePath> removedPathsItr = removedPaths.iterator();
            while (removedPathsItr.hasNext()) {
                FirstLastElementIncludePath removedPath = removedPathsItr.next();
                if (curPath.getLastInclude().getFullName().equals(removedPath.getLastInclude().getFullName())) {
                    coverageInfo.setCoveredBy(include, removedPath.getFirstInclude());
                    removedPathsItr.remove();
                }
            }
        }

    }

    private List<FirstLastElementIncludePath> findPathsMatchingFirstInclude(IIndexInclude include) throws CoreException {
        List<FirstLastElementIncludePath> result = new ArrayList<>();
        for (FirstLastElementIncludePath curPath : unprocessedIncludePaths) {
            if (equals(include, curPath.getFirstInclude())) {
                result.add(curPath);
            }
        }
        return result;
    }

    private void removeAllPathsMatchingLastIncludedFile(String lastIncludedFile, IIndexInclude mandatoryInclude) throws CoreException {
        Iterator<FirstLastElementIncludePath> iterator = unprocessedIncludePaths.iterator();
        while (iterator.hasNext()) {
            FirstLastElementIncludePath curPath = iterator.next();
            String curLastPathIncludeFilePath = FileHelper.uriToStringPath(curPath.getLastInclude().getIncludesLocation().getURI());
            if (lastIncludedFile.equals(curLastPathIncludeFilePath)) {
                coverageInfo.setCoveredBy(mandatoryInclude, curPath.getFirstInclude());
                iterator.remove();
            }
        }
    }

    private boolean equals(IIndexInclude first, IIndexInclude second) throws CoreException {
        IIndexFileLocation firstLocation = first.getIncludesLocation();
        IIndexFileLocation secondLocation = second.getIncludesLocation();
        return firstLocation.getURI().equals(secondLocation.getURI());
    }

    public List<IIndexInclude> getAllIndexIncludes() {
        return allIncludes;
    }
}
