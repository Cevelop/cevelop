/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.stores;

import java.util.HashMap;

import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFile;
import org.eclipse.cdt.core.index.IIndexInclude;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludeHelper;


public class IndexIncludeStore {

    private final HashMap<String, IIndexInclude[]> includes;

    public IndexIncludeStore() {
        includes = new HashMap<>();
    }

    public IIndexInclude[] getIncludes(IIndexFile file, IIndex index) throws CoreException {
        String key = FileHelper.uriToStringPath(file.getLocation().getURI());
        if (!includes.containsKey(key)) {
            IIndexInclude[] includesToAdd = index.findIncludes(file);
            includesToAdd = IncludeHelper.getActiveIncludes(includesToAdd);

            includes.put(key, includesToAdd);
            return includesToAdd;
        }
        return includes.get(key);
    }

    public void clear() {
        includes.clear();
    }
}
