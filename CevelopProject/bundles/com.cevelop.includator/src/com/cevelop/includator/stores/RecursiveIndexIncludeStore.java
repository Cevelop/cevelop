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

import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFile;
import org.eclipse.cdt.core.index.IIndexInclude;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorStatus;
import com.cevelop.includator.resources.IncludatorFile;


public class RecursiveIndexIncludeStore {

    private final HashMap<String, ArrayList<IIndexInclude>> includes;

    public RecursiveIndexIncludeStore() {
        includes = new HashMap<>();
    }

    public ArrayList<IIndexInclude> getRecursiveIncludeRelations(IIndexFile file, IIndex index) throws CoreException {
        String key = FileHelper.uriToStringPath(file.getLocation().getURI());
        if (!includes.containsKey(key)) {
            ArrayList<IIndexInclude> includesToAdd = new ArrayList<>();
            IIndexInclude[] allIncludes = index.findIncludes(file, IIndex.DEPTH_INFINITE);
            for (IIndexInclude curInclude : allIncludes) {
                if (curInclude.isActive()) {
                    includesToAdd.add(curInclude);
                }
            }
            includes.put(key, includesToAdd);
            return includesToAdd;
        }
        return includes.get(key);
    }

    public ArrayList<IIndexInclude> getRecursiveIncludeRelations(IncludatorFile file) throws CoreException {
        IIndexFile indexFile = FileHelper.getIndexFile(file);
        if (indexFile == null) {
            String path = file.getSmartPath();
            IncludatorPlugin.logStatus(new IncludatorStatus("No index file found for \"" + path + "\". Please re-index the current project."), path);
            return new ArrayList<>();
        }
        return getRecursiveIncludeRelations(indexFile, file.getProject().getIndex());
    }

    public void clear() {
        includes.clear();
    }
}
