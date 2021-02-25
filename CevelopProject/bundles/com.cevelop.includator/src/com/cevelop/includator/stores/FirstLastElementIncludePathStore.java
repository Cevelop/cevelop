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
import java.util.List;

import com.cevelop.includator.dependency.FirstLastElementIncludePath;
import com.cevelop.includator.resources.IncludatorFile;


public class FirstLastElementIncludePathStore {

    private final HashMap<StringPair, ArrayList<FirstLastElementIncludePath>> includePaths;

    public FirstLastElementIncludePathStore() {
        includePaths = new HashMap<>();
    }

    public List<FirstLastElementIncludePath> getIncludePaths(IncludatorFile from, IncludatorFile to) {
        StringPair key = new StringPair(from.getFilePath(), to.getFilePath());
        if (!includePaths.containsKey(key)) {
            ArrayList<FirstLastElementIncludePath> pathsToAdd = new FastIndexIncludePathStrategy().findAllIncludePaths(from, to);

            includePaths.put(key, pathsToAdd);
            return pathsToAdd;
        }
        return includePaths.get(key);
    }

    public void clear() {
        for (ArrayList<FirstLastElementIncludePath> curList : includePaths.values()) {
            clear(curList);
        }
        includePaths.clear();
    }

    private void clear(ArrayList<FirstLastElementIncludePath> list) {
        for (FirstLastElementIncludePath curPath : list) {
            curPath.clear();
        }
    }
}
