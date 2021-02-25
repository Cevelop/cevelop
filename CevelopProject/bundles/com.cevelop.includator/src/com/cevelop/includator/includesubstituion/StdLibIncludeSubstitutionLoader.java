/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.includesubstituion;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.cdt.core.model.IIncludeReference;

import com.cevelop.includator.resources.IncludatorProject;


public class StdLibIncludeSubstitutionLoader extends FolderIncludeSubstitutionLoader {

    public StdLibIncludeSubstitutionLoader(IncludatorProject project) {
        super(project);
        for (IIncludeReference curIncludeFolder : project.getIncludeReferences()) {
            File candidateFile = new File(curIncludeFolder.getPath().append("bits").toOSString());
            if (candidateFile.exists() && candidateFile.isDirectory()) {
                addFolderName(curIncludeFolder.getPath().toOSString());
            }
        }
    }

    @Override
    public Map<String, WeightedObject<String>> getExceptions() {
        LinkedHashMap<String, WeightedObject<String>> result = new LinkedHashMap<>();
        Object cppStdIncludeFolderPath = getCppStdIncludeFolderPath();
        if (cppStdIncludeFolderPath != null) {
            String stringfwdPath = cppStdIncludeFolderPath + File.separator + "bits" + File.separator + "stringfwd.h";
            String stringPath = cppStdIncludeFolderPath + File.separator + "string";
            result.put(stringfwdPath, new WeightedObject<>(0, stringPath));
        }
        return result;
    }

    private Object getCppStdIncludeFolderPath() {
        if (project == null) {
            return null;
        }
        for (IIncludeReference curIncludeFolder : project.getIncludeReferences()) {
            File testFile = new File(curIncludeFolder.getPath().append("vector").toOSString());
            if (testFile.exists()) {
                return curIncludeFolder.getPath().toOSString();
            }
        }
        return null;
    }
}
