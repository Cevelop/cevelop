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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.index.IIndexFile;
import org.eclipse.cdt.core.index.IIndexInclude;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.resources.IncludatorProject;


public class FolderIncludeSubstitutionLoader extends IncludeSubstitutionLoader {

    private final List<String> folderNames;

    public FolderIncludeSubstitutionLoader(IncludatorProject project) {
        super(project);
        folderNames = new ArrayList<>();
    }

    public void addFolderName(String folderName) {
        folderNames.add(folderName);
    }

    @Override
    public Map<String, WeightedObject<String>> getSubstitutions() {
        Map<String, WeightedObject<String>> result = new LinkedHashMap<>();
        for (String curFolderPath : folderNames) {
            try {
                addSubstitutionsForFolder(curFolderPath, result);
            } catch (CoreException e) {
                throw new IncludatorException(e);
            }
        }
        return result;
    }

    private void addSubstitutionsForFolder(String folderStr, Map<String, WeightedObject<String>> listToAddTo) throws CoreException {
        File folder = new File(folderStr);
        if (!folder.exists() || !folder.isDirectory()) {
            return;
        }
        IPath folderPath = new Path(folderStr);
        for (File curFile : folder.listFiles()) {
            if (curFile.exists() && !curFile.isDirectory()) {
                // '0' prevents substitution of "top level" headers.
                listToAddTo.put(curFile.getAbsolutePath(), new WeightedObject<>(0, curFile.getAbsolutePath()));
                addSubstitutionsForFile(curFile.getAbsolutePath(), listToAddTo, folderPath);
            }
        }
    }

    private void addSubstitutionsForFile(String absoluteFilePath, Map<String, WeightedObject<String>> listToAddTo, IPath prefixPath)
            throws CoreException {
        IIndexFile indexFile = FileHelper.getIndexFile(absoluteFilePath, project);
        if (indexFile == null) {
            return;
        }
        ArrayList<IIndexInclude> includedInFile = IncludatorPlugin.getRecursiveIndexIncludeStore().getRecursiveIncludeRelations(indexFile, project
                .getIndex());
        long weight = FileHelper.getWeight(absoluteFilePath, project);
        for (IIndexInclude curIncluded : includedInFile) {
            if (curIncluded.isResolved()) {
                String key = FileHelper.uriToStringPath(curIncluded.getIncludesLocation().getURI());
                WeightedObject<String> currentEntry = listToAddTo.get(key);
                if (currentEntry == null || currentEntry.weight > weight) {
                    listToAddTo.put(key, new WeightedObject<>(weight, absoluteFilePath));
                }
            }
        }
    }
}
