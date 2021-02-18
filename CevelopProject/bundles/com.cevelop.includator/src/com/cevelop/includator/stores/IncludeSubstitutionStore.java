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

import com.cevelop.includator.includesubstituion.BoostSubstitutionLaoder;
import com.cevelop.includator.includesubstituion.IncludeSubstitutionLoader;
import com.cevelop.includator.includesubstituion.StdLibIncludeSubstitutionLoader;
import com.cevelop.includator.includesubstituion.WeightedObject;
import com.cevelop.includator.resources.IncludatorProject;


public class IncludeSubstitutionStore {

    private final HashMap<String, WeightedObject<String>> replacements;
    boolean                                               stitutionsLoaded;

    public IncludeSubstitutionStore() {
        replacements = new HashMap<>();
        stitutionsLoaded = false;
    }

    /**
     * In the case of the std-library it is important that instead of {@code <bits/std_vector>}, {@code <vector>} gets included even if the declaration of the type
     * {@code vector} is in {@code <bits/std_vector>}. when calling this method with "/path/to/bits/std_vector" the return value will be "/path/to/vector". If
     * there is nothing to substitute, the original path will be returned.
     *
     * @param originalFilePath
     * file path which might get substituted
     * @param project
     * a {@link IncludatorProject}
     * @return A substitution file path as {@link String}
     */
    public String getSubstitutionFilePath(String originalFilePath, IncludatorProject project) {
        if (!stitutionsLoaded) {
            initSubstitutions(project);
        }
        WeightedObject<String> replacementPath = replacements.get(originalFilePath);
        return (replacementPath != null) ? replacementPath.object : originalFilePath;
    }

    private void initSubstitutions(IncludatorProject project) {
        loadSubstitutions(new StdLibIncludeSubstitutionLoader(project));
        BoostSubstitutionLaoder boostSubstitutions = new BoostSubstitutionLaoder(project);
        if (boostSubstitutions.isBoostPresent()) {
            loadSubstitutions(boostSubstitutions);
        }
        stitutionsLoaded = true;
    }

    public void loadSubstitutions(IncludeSubstitutionLoader loader) {
        replacements.putAll(loader.getSubstitutions());
        replacements.putAll(loader.getExceptions());
    }

    public void clear() {
        if (replacements != null) {
            replacements.clear();
        }
        stitutionsLoaded = false;
    }
}
