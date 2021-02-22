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

import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFile;
import org.eclipse.cdt.core.index.IIndexInclude;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.dependency.FirstLastElementIncludePath;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.helpers.IncludatorStatus;
import com.cevelop.includator.resources.IncludatorFile;


public class FastIndexIncludePathStrategy {

    private IIndex                                 index;
    private ArrayList<FirstLastElementIncludePath> workResultList;
    private IndexIncludeStore                      includeStore;

    public ArrayList<FirstLastElementIncludePath> findAllIncludePaths(IncludatorFile fromFile, IncludatorFile toFile) {
        workResultList = new ArrayList<>(1);
        index = fromFile.getProject().getIndex();
        includeStore = IncludatorPlugin.getIndexIncludeStore();
        try {
            IIndexFile indexFileFrom = FileHelper.getIndexFile(fromFile);
            IIndexFile indexFileTo = FileHelper.getIndexFile(toFile);
            findTargets(indexFileFrom, indexFileTo);
        } catch (CoreException e) {
            throw new IncludatorException(e);
        }
        return workResultList;
    }

    private void findTargets(IIndexFile indexFileFrom, IIndexFile targetFile) throws CoreException {
        String targetName = FileHelper.uriToStringPath(targetFile.getLocation().getURI());
        for (IIndexInclude curInclude : includeStore.getIncludes(indexFileFrom, index)) {

            if (curInclude.getIncludesLocation() == null) {
                continue; // unresolved include, inactive include
            }
            String curIncludePath = FileHelper.uriToStringPath(curInclude.getIncludesLocation().getURI());
            if (curIncludePath.equals(targetName)) {
                workResultList.add(new FirstLastElementIncludePath(curInclude, curInclude));
                continue;
            }

            IIndexFile includedFile = FileHelper.getIndexFile(curInclude.getIncludesLocation(), index);
            if (includedFile != null) {
                ArrayList<IIndexInclude> related = IncludatorPlugin.getRecursiveIndexIncludeStore().getRecursiveIncludeRelations(includedFile, index);
                addPathIfAmongRelated(targetName, related, curInclude);
            } else {
                IncludatorPlugin.logStatus(new IncludatorStatus("No index file found for \"" + curIncludePath +
                                                                "\". Please re-index the current project."), curIncludePath);
                continue;
            }
        }
    }

    private void addPathIfAmongRelated(String targetName, ArrayList<IIndexInclude> related, IIndexInclude startingInclude) throws CoreException {
        for (IIndexInclude curInclude : related) {
            if (!curInclude.isResolved()) {
                continue; // unresolved include, inactive include
            }
            if (FileHelper.uriToStringPath(curInclude.getIncludesLocation().getURI()).equals(targetName)) {
                workResultList.add(new FirstLastElementIncludePath(startingInclude, curInclude));
                return;
            }
        }
    }
}
