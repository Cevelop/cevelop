/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.base;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.codan.core.CodanRuntime;
import org.eclipse.cdt.codan.core.model.ICheckersRegistry;
import org.eclipse.cdt.codan.core.model.IProblemWorkingCopy;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.checkers.OrganizeIncludesChecker;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.index.IncludatorIndexAdapter;
import com.cevelop.includator.includesubstituion.FolderIncludeSubstitutionLoader;
import com.cevelop.includator.preferences.IncludatorPropertyManager;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.resources.IncludatorProject;
import com.cevelop.includator.tests.helpers.UIHelper;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.base.CDTTestingUITest;


public abstract class IncludatorBaseTest extends CDTTestingUITest {

    private final Set<String>  includePathsSubDirs;
    private final List<String> includeSubstitutionLoaderFoldersPaths;
    private IncludatorProject  includatorProject;

    public IncludatorBaseTest() {
        includePathsSubDirs = new LinkedHashSet<>();
        includeSubstitutionLoaderFoldersPaths = new ArrayList<>();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        new IncludatorIndexAdapter().run(new NullProgressMonitor(), includatorProject);
        UIHelper.closeWelcomeScreen();
        setUpIncludeSubstitution();
        includatorProject.acquireIndexReadLock();
    }

    @Override
    protected void preSetupIndex() {
        deactivateCodanPlugin();
        setUpIncludatorPlugin();
        super.preSetupIndex();
    }

    private void setUpIncludatorPlugin() {
        initActiveWorkbenchWindow();
        IncludatorPlugin.initPreferredLinkageID(FileHelper.getPreferredLinkageID(getCurrentCProject()));
        IncludatorPlugin.initActiveIncludatorWorkspace();
        includatorProject = IncludatorPlugin.getWorkspace().getProject(getCurrentCProject());
        IncludatorPropertyManager.addIncludePathSubDirs(includePathsSubDirs, getCurrentCProject());
        IPreferenceStore store = IncludatorPlugin.getDefault().getPreferenceStore();
        store.setValue(IncludatorPropertyManager.NEVER_SHOW_SUGGESTION_DIALOG, true);
    }

    private void initActiveWorkbenchWindow() {
        IncludatorPlugin.initActiveWorkbenchWindow(getActiveWorkbenchWindow());
    }

    private void deactivateCodanPlugin() {
        ICheckersRegistry p = CodanRuntime.getInstance().getCheckersRegistry();
        IProblemWorkingCopy copy = (IProblemWorkingCopy) p.getWorkspaceProfile().findProblem(OrganizeIncludesChecker.PROBLEM_ID);
        copy.setEnabled(false);
    }

    private void setUpIncludeSubstitution() {
        try {
            includatorProject.acquireIndexReadLock();
            FolderIncludeSubstitutionLoader loader = new FolderIncludeSubstitutionLoader(getActiveProject());
            for (String curFolderPath : includeSubstitutionLoaderFoldersPaths) {
                loader.addFolderName(expectedProjectHolder.makeProjectAbsolutePath(curFolderPath).toOSString());
            }
            IncludatorPlugin.getIncludeSubstitutionStore().loadSubstitutions(loader);
        } finally {
            includatorProject.releaseIndexReadLock();
        }
    }

    @Override
    public void tearDown() throws Exception {
        FileHelper.clean();
        includatorProject.releaseIndexReadLock();
        IncludatorPlugin.getDefault().clean();
        IncludatorPlugin.resetActiveWorkbenchWindow();
        IncludatorPlugin.resetActiveIncludatorWorkspace();
        super.tearDown();
    }

    protected IncludatorProject getActiveProject() {
        return includatorProject;
    }

    protected IncludatorFile getActiveIncludatorFile() {
        return getIncludatorFile(getNameOfPrimaryTestFile());
    }

    protected IncludatorFile getIncludatorFile(String relativeFilePath) {
        IFile iFile = currentProjectHolder.getFile(relativeFilePath);
        return getIncludatorFile(iFile);
    }

    protected IncludatorFile getIncludatorFile(IFile iFile) {
        return getActiveProject().getFile(iFile);
    }

    protected void addIncludeSubstitutionFolder(String folderPath) {
        includeSubstitutionLoaderFoldersPaths.add(folderPath);
    }

    protected void addIncludePathsSubDir(String includePathsSubDir) {
        includePathsSubDirs.add(includePathsSubDir);
    }
}
