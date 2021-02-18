/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICElementVisitor;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.IIncludeReference;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.helpers.IncludeHelper;
import com.cevelop.includator.helpers.tuprovider.TranslationUnitProvider;
import com.cevelop.includator.preferences.IncludatorPropertyManager;


public class IncludatorProject {

    private Map<String, IncludatorFile> files;
    private ICProject                   cproject;
    /**
     * Contains (when initialized) all referenced projects including the current one.
     */
    private ArrayList<ICProject>        referencedCProjects;
    private boolean                     allFilesAdded;
    private IIndex                      index;
    private int                         indexLockCount = 0;

    public IncludatorProject(ICProject cproject) {
        this.cproject = cproject;
        files = new LinkedHashMap<>();
        allFilesAdded = false;
    }

    public IncludatorFile getFile(URI fileUri) {
        String key = FileHelper.uriToStringPath(fileUri);
        if (!files.containsKey(key)) {
            IPath path = FileHelper.stringToPath(key);
            String fileName = path.lastSegment();
            if (FileHelper.isCLikeFile(cproject.getProject(), fileName)) {
                TranslationUnitProvider tuProvider = new TranslationUnitProvider(fileUri, cproject);
                files.put(key, new IncludatorFile(fileUri, tuProvider, this));
            } else if (FileHelper.getLanguageForFile(cproject.getProject(), fileName) == null) {
                throwUnknownFileTypeException(key, path, fileName);
            }

        }
        return files.get(key);
    }

    public IncludatorFile getFile(IPath path) {
        return getFile(URIUtil.toURI(path.makeAbsolute()));
    }

    private void throwUnknownFileTypeException(String key, IPath path, String fileName) {
        String pathStr = FileHelper.getSmartFilePath(key, cproject);
        if (pathStr.equals(key)) {
            String includeRefRelativePath = IncludeHelper.getIncludeReferenceRelativePath(key, this);
            if (!includeRefRelativePath.startsWith("..")) {
                pathStr = includeRefRelativePath;
            }
        }
        String toAdd = path.getFileExtension() == null ? fileName : "*." + path.getFileExtension();
        String msg = "CDT does not consider the file '" + pathStr + "' as a C or C++ source or header file. " + "Please add '" + toAdd +
                     "' to the 'File Types' list under 'C/C++ General->File Types' in 'Eclipse Preferences' or the project's 'Project Properties'.";
        throw new IncludatorException(msg, pathStr);
    }

    public IncludatorFile getFile(String fileName) {
        return getFile(FileHelper.stringToUri(fileName));
    }

    public IncludatorFile getFile(IFile file) {
        return getFile(file.getLocationURI());
    }

    @Override
    public String toString() {
        return getPath().toOSString();
    }

    public IPath getPath() {
        return cproject.getProject().getLocation();
    }

    public ICProject getCProject() {
        return cproject;
    }

    private List<IncludatorFile> getAllFiles() {
        if (!allFilesAdded) {
            addAllFiles();
            allFilesAdded = true;
        }
        return new ArrayList<>(files.values());
    }

    public List<IncludatorFile> getAffectedFiles() {
        List<IncludatorFile> allFiles = getAllFiles();
        filter(allFiles);
        return allFiles;
    }

    private void addAllFiles() {
        ICElementVisitor cElementVisitor = element -> {
            // since neither element.getResource().isHidden() nor element.getResource.getResourceAttributes().isHidden()
            // work, I do the check manually...
            if (element.getResource().getName().startsWith(".")) {
                return false;
            }
            switch (element.getElementType()) {
            case ICElement.C_PROJECT:
            case ICElement.C_CCONTAINER:
                return true;
            case ICElement.C_UNIT:
                // getFile caches the file as side effect. So calling getFile has an effect here.
                getFile(element.getLocationURI());
            }
            return false;
        };
        try {
            cproject.accept(cElementVisitor);
        } catch (CoreException e) {
            throw new IncludatorException(e);
        }
    }

    public void clear() {
        for (IncludatorFile curFile : files.values()) {
            curFile.clear();
        }
        files.clear();
        files = null;
        cproject = null;
        allFilesAdded = false;
    }

    public void purge() {
        for (IncludatorFile curFile : files.values()) {
            curFile.purge();
        }
    }

    public Map<String, IncludatorFile> getAllFilesMap() {
        if (!allFilesAdded) {
            addAllFiles();
            allFilesAdded = true;
        }
        return new LinkedHashMap<>(files);
    }

    public IIncludeReference[] getIncludeReferences() {
        try {
            return cproject.getIncludeReferences();
        } catch (CModelException e) {
            throw new IncludatorException(e);
        }
    }

    public IIndex getIndex() {
        if (index == null) {
            throw new IncludatorException("Access to index without first locking it.", toString());
        }
        return index;
    }

    public void acquireIndexReadLock() {
        try {
            if (index == null) {
                initRelatedCProjects();
                index = CCorePlugin.getIndexManager().getIndex(referencedCProjects.toArray(new ICProject[referencedCProjects.size()]));
            }
            index.acquireReadLock();
            indexLockCount++;
        } catch (Exception e) {
            throw new IncludatorException("Error while getting CDT Index.", e);
        }
    }

    private void initRelatedCProjects() {
        if (referencedCProjects != null) {
            return;
        }
        referencedCProjects = new ArrayList<>();
        referencedCProjects.add(cproject);
        try {
            List<IProject> referencedProjects = Arrays.asList(cproject.getProject().getReferencedProjects());
            for (ICProject candidate : cproject.getCModel().getCProjects()) {
                if (referencedProjects.contains(candidate.getProject())) {
                    referencedCProjects.add(candidate);
                }
            }
        } catch (CoreException e) {
            throw new IncludatorException("Error while finding related projects to " + cproject + ".", e);
        }
    }

    public void releaseIndexReadLock() {
        if (index != null && indexLockCount > 0) {
            index.releaseReadLock();
            if (--indexLockCount == 0) {
                index = null;
            }
        } else {
            throw new IncludatorException("Trying to release lock on index which has not been aquired previously.", toString());
        }
    }

    private void filter(Collection<IncludatorFile> allFiles) {
        List<String> excludedResourcesList = IncludatorPropertyManager.getExcludedResources(this);
        final Iterator<IncludatorFile> it = allFiles.iterator();
        while (it.hasNext()) {
            if (isExcluded(it.next(), excludedResourcesList)) {
                it.remove();
            }
        }
    }

    private boolean isExcluded(IncludatorFile next, List<String> excludedResourcesList) {
        for (String exclusion : excludedResourcesList) {
            if (next.getProjectRelativePath().startsWith(exclusion)) {
                return true;
            }
        }
        return false;
    }
}
