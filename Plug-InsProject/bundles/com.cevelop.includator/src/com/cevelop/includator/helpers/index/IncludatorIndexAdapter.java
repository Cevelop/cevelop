/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.helpers.index;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexManager;
import org.eclipse.cdt.core.index.IndexLocationFactory;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.IIncludeReference;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.core.pdom.PDOMManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IShouldRun;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.helpers.IncludatorStatus;
import com.cevelop.includator.helpers.IncludeHelper;
import com.cevelop.includator.preferences.IncludatorPropertyManager;
import com.cevelop.includator.resources.IncludatorProject;


@SuppressWarnings("restriction")
public class IncludatorIndexAdapter {

    private ICProject         cProject;
    private IProject          project;
    private boolean           lockAquiredBeforeRun;
    private IncludatorProject includatorProject;
    private IShouldRun        iShouldRun;
    private IIndex            index;

    public IncludatorIndexAdapter() {
        initDummyConfirmator(); // run without is default.
    }

    private void initDummyConfirmator() {
        iShouldRun = () -> true;
    }

    public void setConfirator(IShouldRun confirmator) {
        iShouldRun = confirmator;
    }

    public void run(IProgressMonitor monitor, IncludatorProject project) {
        this.includatorProject = project;
        cProject = includatorProject.getCProject();
        this.project = cProject.getProject();
        monitor.beginTask("Adapting index. Waiting for Indexer to complete...", 1000);
        initIndexLockStatus();
        try {
            Set<String> fileList = computeFilesToIndexList();
            List<ITranslationUnit> tusToAdd = computeTusToAdd(fileList);
            if (lockAquiredBeforeRun) {
                includatorProject.releaseIndexReadLock();
            }
            monitor.worked(100);
            int skipedWork = 200;
            if (!tusToAdd.isEmpty() && iShouldRun.shouldRun()) {
                skipedWork -= 100;
                monitor.worked(100);
                adaptIndex(tusToAdd);
                skipedWork -= 100;
                monitor.worked(100);
            }
            monitor.worked(skipedWork);
        } catch (CModelException e) {
            throw new IncludatorException("Adapting CDT index failed.", e);
        } catch (CoreException e) {
            throw new IncludatorException("Adapting CDT index failed.", e);
        } finally {
            joinIndex();
            monitor.worked(700);
            monitor.done();
            if (lockAquiredBeforeRun) {
                includatorProject.acquireIndexReadLock();
            }
        }
    }

    private void adaptIndex(List<ITranslationUnit> tusToAdd) {
        IIndexManager indexManager = CCorePlugin.getIndexManager();
        if (!(indexManager instanceof PDOMManager)) {
            String msg = "IndexManager is not instanceof PDOMManager (as expected) but of " + indexManager.getClass().getName() +
                         ". Adapting index thus failed. Please report an Includator bug with this message.";
            IncludatorPlugin.logStatus(new IncludatorStatus(msg), includatorProject.getPath().toOSString());
        }
        PDOMManager pdomManager = (PDOMManager) indexManager;
        try {
            Class<ITranslationUnit[]> tuClass = ITranslationUnit[].class;
            Method chageProjectMethod = PDOMManager.class.getDeclaredMethod("changeProject", ICProject.class, tuClass, tuClass, tuClass);
            chageProjectMethod.setAccessible(true);
            ITranslationUnit[] emptyTuArray = new ITranslationUnit[0];
            chageProjectMethod.invoke(pdomManager, cProject, tusToAdd.toArray(new ITranslationUnit[tusToAdd.size()]), emptyTuArray, emptyTuArray);
        } catch (NoSuchMethodException e) {
            throw new IncludatorException(e);
        } catch (SecurityException e) {
            throw new IncludatorException(e);
        } catch (IllegalAccessException e) {
            throw new IncludatorException(e);
        } catch (IllegalArgumentException e) {
            throw new IncludatorException(e);
        } catch (InvocationTargetException e) {
            throw new IncludatorException(e);
        }
    }

    private List<ITranslationUnit> computeTusToAdd(Set<String> fileList) throws CoreException {
        try {
            if (!lockAquiredBeforeRun) {
                includatorProject.acquireIndexReadLock();
            }
            index = includatorProject.getIndex();
            List<ITranslationUnit> tusToAdd = new ArrayList<>();
            for (String curFilePath : fileList) {
                if (isContainedInIndex(curFilePath)) {
                    continue;
                }
                ITranslationUnit tu = CoreModel.getDefault().createTranslationUnitFrom(cProject, FileHelper.stringToUri(curFilePath));
                if (tu != null) {
                    tusToAdd.add(tu);
                }
            }
            return tusToAdd;
        } finally {
            if (!lockAquiredBeforeRun) {
                includatorProject.releaseIndexReadLock();
            }
            index = null;
        }
    }

    private boolean isContainedInIndex(String curFilePath) throws CoreException {
        return index.getFiles(IndexLocationFactory.getIFLExpensive(cProject, curFilePath)).length != 0;
    }

    private void initIndexLockStatus() {
        lockAquiredBeforeRun = true;
        try {
            includatorProject.getIndex();
        } catch (IncludatorException e) {
            lockAquiredBeforeRun = false;
        }
    }

    private void joinIndex() {
        final boolean joined = CCorePlugin.getIndexManager().joinIndexer(IIndexManager.FOREVER, new NullProgressMonitor());
        if (!joined) {
            throw new IncludatorException("Indexing operation timed out.", cProject.getPath().toOSString());
        }
    }

    private Set<String> computeFilesToIndexList() throws CModelException {
        Set<String> fileList = new LinkedHashSet<>();
        for (File curFolder : getFoldersToConsider()) {
            addAllFilesToList(fileList, curFolder);
        }
        return fileList;
    }

    private Set<File> getFoldersToConsider() throws CModelException {
        LinkedHashSet<File> folders = new LinkedHashSet<>();
        Set<String> subFoldersRelativePaths = IncludatorPropertyManager.getIncludePathSubDirs(cProject);

        for (IIncludeReference curIncludeFolder : cProject.getIncludeReferences()) {
            File folder = new File(curIncludeFolder.getPath().toOSString());
            if (folder.exists() && folder.isDirectory()) {
                folders.add(folder);
                addExistingSubFolders(folders, folder, subFoldersRelativePaths);
            }
        }
        return folders;
    }

    private void addExistingSubFolders(Set<File> listToAddTo, File currentBaseFolder, Set<String> subFolderNames) {
        for (String curSubFolder : subFolderNames) {
            File subFolder = new File(currentBaseFolder.getAbsolutePath() + File.separator + curSubFolder + File.separator);
            if (subFolder.exists() && subFolder.isDirectory()) {
                listToAddTo.add(subFolder);
            }
        }
    }

    private void addAllFilesToList(Set<String> fileList, File folder) {
        try {
            String canonicalPath = folder.getCanonicalPath();
            if (!canonicalPath.endsWith(File.separator)) {
                canonicalPath = canonicalPath + File.separator;
            }
            for (File curFile : folder.listFiles()) {
                if (shouldConsiderAsListEntry(curFile)) {
                    fileList.add(canonicalPath + curFile.getName());
                }
            }
        } catch (IOException e) {
            throw new IncludatorException(e);
        }
    }

    private boolean shouldConsiderAsListEntry(File curFile) {
        if (!curFile.exists() || curFile.isDirectory()) {
            return false;
        }
        if (FileHelper.isCLikeFile(project, curFile.getName())) {
            return true;
        }

        IPreferenceStore store = IncludatorPlugin.getDefault().getPreferenceStore();
        boolean shouldWarn = store.getBoolean(IncludatorPropertyManager.WARN_ABOUT_IGNORED_EXTENSION_LESS_FILES);
        if (shouldWarn && new Path(curFile.getName()).getFileExtension() == null) {
            if (!curFile.getName().equals("README")) { // never warn about README file (exists in g++ standard headers. is no c/c++ source of course.
                String path = FileHelper.makeProjectRelativePath(curFile.getAbsolutePath(), project);
                if (path.startsWith(".." + FileHelper.PATH_SEGMENT_SEPARATOR)) {
                    path = IncludeHelper.getIncludeReferenceRelativePath(curFile.getAbsolutePath(), includatorProject);
                }
                String msg = "File '" + path + "' has unknown file type and was thus ignored. " + "If it is a C / C++ file, please add '" + curFile
                        .getName() + "' to the 'File Types' list under " +
                             "'C/C++ General->File Types' in 'Eclipse Preferences' or in the project's 'Project Properties'.";
                IncludatorPlugin.logStatus(new IncludatorStatus(IStatus.WARNING, msg), project.getLocation().toOSString());
            }
        }
        return false;
    }
}
