/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.stores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFile;
import org.eclipse.cdt.core.index.IIndexInclude;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.dependency.FullIncludePath;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.resources.IncludatorFile;


public class IncludePathStore {

    private final HashMap<StringPair, ArrayList<FullIncludePath>> includePaths;
    private boolean                                               isTargetPartOfProject;
    private FullIncludePath                                       workPath;
    private ArrayList<FullIncludePath>                            workResultList;
    private int                                                   smallestMatchingPathLength;
    private IncludatorFile                                        startingFile;
    private IndexIncludeStore                                     includeStore;
    private IIndex                                                index;

    public IncludePathStore() {
        includePaths = new HashMap<>();
    }

    public ArrayList<FullIncludePath> getIncludePaths(IncludatorFile from, IncludatorFile to) {
        StringPair key = new StringPair(from.getFilePath(), to.getFilePath());
        if (!includePaths.containsKey(key)) {
            ArrayList<FullIncludePath> pathsToAdd = findAllIncludePaths(from, to);
            includePaths.put(key, pathsToAdd);
            return pathsToAdd;
        }
        return includePaths.get(key);
    }

    public void clear() {
        for (ArrayList<FullIncludePath> curList : includePaths.values()) {
            clear(curList);
        }
        includePaths.clear();
    }

    private void clear(ArrayList<FullIncludePath> list) {
        for (FullIncludePath curPath : list) {
            curPath.clear();
        }
    }

    private ArrayList<FullIncludePath> findAllIncludePaths(IncludatorFile fromFile, IncludatorFile toFile) {
        startingFile = fromFile;
        workResultList = new ArrayList<>(1);
        workPath = new FullIncludePath();
        smallestMatchingPathLength = Integer.MAX_VALUE;
        isTargetPartOfProject = toFile.isPartOfProject();
        includeStore = IncludatorPlugin.getIndexIncludeStore();
        index = fromFile.getProject().getIndex();
        try {
            IIndexFile indexFileFrom = FileHelper.getIndexFile(fromFile);
            IIndexFile indexFileTo = FileHelper.getIndexFile(toFile.getFilePath(), toFile.getProject());
            IIndexInclude[] possibleElements = index.findIncludedBy(indexFileTo, IIndex.DEPTH_INFINITE);
            HashSet<IIndexFile> possiblePathElements = new HashSet<>();
            for (IIndexInclude include : possibleElements) {
                possiblePathElements.add(include.getIncludedBy());
            }

            recursiveFindTarget(indexFileFrom, indexFileTo, possiblePathElements);
        } catch (CoreException e) {
            throw new IncludatorException(e);
        }
        return workResultList;
    }

    private void recursiveFindTarget(IIndexFile fromFile, IIndexFile toFile, Set<IIndexFile> possiblePathElements) throws CoreException {

        if (equals(fromFile, toFile)) {
            workResultList.add(workPath.clone());
            if (workPath.length() < smallestMatchingPathLength) {
                smallestMatchingPathLength = workPath.length();
            }
            return;
        }

        if (!possiblePathElements.contains(fromFile)) {
            return;
        }

        for (IIndexInclude curInclude : includeStore.getIncludes(fromFile, index)) {
            if (!existsIncludedFile(curInclude)) {
                continue;
            }
            if (fileAlreadyContainedInPath(curInclude)) {
                return; // cyclic include. Abort.
            }
            try {
                IIndexFile newFromFile = FileHelper.getIndexFile(curInclude.getIncludesLocation(), index);
                if (shouldBreak(newFromFile)) {
                    continue;
                }
                try {
                    workPath.addPathElement(curInclude);
                    recursiveFindTarget(newFromFile, toFile, possiblePathElements);
                } finally {
                    workPath.removeLastElement();
                }
            } catch (Exception e) {
                // Happens when include points to inexisting file. Abort
                continue;
            }
        }
    }

    private boolean existsIncludedFile(IIndexInclude curInclude) throws CoreException {
        return curInclude.getIncludesLocation() != null;
    }

    private boolean equals(IIndexFile first, IIndexFile second) throws CoreException {
        String firstPath = FileHelper.uriToStringPath(first.getLocation().getURI());
        String secondPath = FileHelper.uriToStringPath(second.getLocation().getURI());
        return firstPath.equals(secondPath);
    }

    private boolean shouldBreak(IIndexFile newFromFile) throws CoreException {

        return (isTargetPartOfProject && !FileHelper.isPartOfProject(newFromFile.getLocation().getURI(), startingFile.getProject())) ||
               ((workResultList.size() != 0) && (smallestMatchingPathLength + 5 < workPath.length()));
    }

    private boolean fileAlreadyContainedInPath(IIndexInclude includeToCheck) throws CoreException {
        String pathToCheck = FileHelper.uriToStringPath(includeToCheck.getIncludesLocation().getURI());
        for (IIndexInclude curInclude : workPath.getAllIncludes()) {
            if (FileHelper.uriToStringPath(curInclude.getIncludesLocation().getURI()).equals(pathToCheck)) {
                return true;
            }
        }
        return false;
    }
}
