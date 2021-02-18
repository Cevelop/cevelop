/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.checkers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.helpers.MarkerHelper;
import com.cevelop.includator.optimizer.Optimizer;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.organizeincludes.OrganizeIncludesAlgorithm;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.startingpoints.FileStartingPoint;
import com.cevelop.includator.ui.Markers;

import ch.hsr.ifs.iltis.cpp.core.wrappers.AbstractIndexAstChecker;


public class OrganizeIncludesChecker extends AbstractIndexAstChecker {

    public static String ID         = "com.cevelop.includator.codan.checkers.OrganizeIncludesChecker";
    public static String PROBLEM_ID = "com.cevelop.includator.codan.OrganizeIncludeProblem";

    private ICProject cProject;

    private FileStartingPoint startingPoint;

    @Override
    public void processAst(IASTTranslationUnit ast) {
        if (!shouldRun(ast)) {
            return;
        }
        try {
            intitActiveWorkspace(ast);
            initStartingPoint(ast);
            try {
                startingPoint.getProject().acquireIndexReadLock();
                List<Suggestion<?>> allSuggestions = runOptimizer();
                cleanOldMarkersInCurrentFile(ast);
                addMarkers(allSuggestions);
            } finally {
                startingPoint.getProject().releaseIndexReadLock();
                IncludatorPlugin.getDefault().cleanWorkspace();
            }
        } catch (Exception e) {
            throw new IncludatorException("Error while performing static include analysis", e);
        }

    }

    private void intitActiveWorkspace(IASTTranslationUnit ast) {
        String fileName = ast.getFileLocation().getFileName();
        cProject = CoreModel.getDefault().create(new Path(fileName)).getCProject();
        IncludatorPlugin.initActiveIncludatorWorkspace();
    }

    private void cleanOldMarkersInCurrentFile(IASTTranslationUnit ast) throws CoreException {
        getFile().deleteMarkers(Markers.INCLUDATOR_ADD_INCLUDE_MARKER, true, IResource.DEPTH_ZERO);
        getFile().deleteMarkers(Markers.INCLUDATOR_UNUSED_INCLUDE_MARKER, true, IResource.DEPTH_ZERO);
        IncludatorPlugin.getSuggestionStore().cleanOldSuggestions();
    }

    private void addMarkers(List<Suggestion<?>> allSuggestions) throws CoreException {
        MarkerHelper.addMarkers(allSuggestions);
        IncludatorPlugin.getSuggestionStore().addSuggestions(allSuggestions);
    }

    private List<Suggestion<?>> runOptimizer() {
        Optimizer optimizer = new Optimizer(IncludatorPlugin.getWorkspace(), new OrganizeIncludesAlgorithm());
        optimizer.run(startingPoint, new NullProgressMonitor());
        List<Suggestion<?>> allSuggestions = new ArrayList<>(optimizer.getOptimizationSuggestions());
        return allSuggestions;
    }

    private void initStartingPoint(IASTTranslationUnit ast) {
        IncludatorFile activeFile = IncludatorPlugin.getWorkspace().getProject(cProject).getFile(ast.getFileLocation().getFileName());
        startingPoint = new FileStartingPoint(IncludatorPlugin.getActiveWorkbenchWindow(), activeFile);
        IncludatorPlugin.initPreferredLinkageID(FileHelper.getPreferredLinkageID(startingPoint.getProject().getCProject()));
    }

    private boolean shouldRun(IASTTranslationUnit ast) {
        if (ast.getFileLocation() == null) {
            return false;
        }
        try {
            IFile iFile = FileHelper.getIFile(FileHelper.stringToUri(ast.getFilePath()));
            if ((iFile == null) || !iFile.exists() || !iFile.isAccessible()) {
                return false;
            }
        } catch (IncludatorException e) {
            return false;
        }
        return true;
    }
}
