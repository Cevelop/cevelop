/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.optimizer.findunusedfiles;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.cdt.core.index.IIndexFile;
import org.eclipse.cdt.core.index.IIndexInclude;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.resources.IncludatorFile;


public class FindUnusedFilesAlgorithm extends Algorithm {

    private final Map<String, IncludatorFile> unusedFiles = new HashMap<>();

    @Override
    public void run() {
        try {
            for (IncludatorFile file : startingPoint.getAffectedFiles()) {
                unusedFiles.put(file.getFilePath(), file);
            }

            removeUsedFileNames();
            createResult();
        } catch (CoreException e) {
            throw new IncludatorException("Error while finding unused files.", e);
        }
    }

    private void removeUsedFileNames() throws CoreException {
        for (IncludatorFile curFile : startingPoint.getProject().getAffectedFiles()) {
            if (!curFile.isHeaderFile()) {
                processSourceFile(curFile);
            }
        }
    }

    private void processSourceFile(IncludatorFile curFile) throws CoreException {
        unusedFiles.remove(curFile.getFilePath());
        IIndexFile indexFile = FileHelper.getIndexFile(curFile);
        if (indexFile == null) {
            return;
        }
        for (IIndexInclude curInclude : IncludatorPlugin.getRecursiveIndexIncludeStore().getRecursiveIncludeRelations(indexFile, curFile.getProject()
                .getIndex())) {
            if (curInclude.isResolved()) {
                unusedFiles.remove(FileHelper.uriToStringPath(curInclude.getIncludesLocation().getURI()));
            }
        }
    }

    private void createResult() {
        for (IncludatorFile curFile : unusedFiles.values()) {
            addSuggestion(new UnusedFileSuggestion(curFile, FindUnusedFilesAlgorithm.class));
        }
    }

    @Override
    public String getInitialProgressMonitorMessage(String resourceName) {
        return "Finding unused files in " + resourceName;
    }

    @Override
    public Set<Class<? extends Algorithm>> getInvolvedAlgorithmTypes() {
        HashSet<Class<? extends Algorithm>> involvedAlgorithms = new HashSet<>();
        involvedAlgorithms.add(FindUnusedFilesAlgorithm.class);
        return involvedAlgorithms;
    }

    @Override
    public AlgorithmScope getScope() {
        return AlgorithmScope.EDITOR_SCOPE;
    }

    @Override
    public void reset() {
        unusedFiles.clear();
        super.reset();
    }
}
