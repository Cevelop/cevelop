/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.includesubstituion;

import java.util.HashMap;
import java.util.Map;

import com.cevelop.includator.resources.IncludatorProject;


public abstract class IncludeSubstitutionLoader {

    protected IncludatorProject project;

    public IncludeSubstitutionLoader(IncludatorProject project) {
        this.project = project;
    }

    public abstract Map<String, WeightedObject<String>> getSubstitutions();

    public Map<String, WeightedObject<String>> getExceptions() {
        return new HashMap<>();
    }
}
