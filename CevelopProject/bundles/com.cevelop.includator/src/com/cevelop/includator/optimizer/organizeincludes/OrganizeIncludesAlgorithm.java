/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.optimizer.organizeincludes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.index.IIndexInclude;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorStatus;
import com.cevelop.includator.helpers.IncludeHelper;
import com.cevelop.includator.helpers.IncludeInfo;
import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.optimizer.directlyincludereferenceddeclaration.AddIncludeSuggestion;
import com.cevelop.includator.optimizer.findunusedincludes.FindUnusedIncludesAlgorithm;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.stores.RecursiveIndexIncludeStore;


public class OrganizeIncludesAlgorithm extends FindUnusedIncludesAlgorithm {

    protected final ArrayList<OtherFileCoverageData> addedSuggestionsCoverageData;

    public OrganizeIncludesAlgorithm() {
        addedSuggestionsCoverageData = new ArrayList<>();
    }

    @Override
    public String getInitialProgressMonitorMessage(String resourceName) {
        return "Finding organize include suggestions in " + resourceName;
    }

    @Override
    protected void handleUnincludedDeclRefDependency(DeclarationReferenceDependency dependency) {
        IncludatorFile declarationFile = dependency.getDeclaration().getFile();
        IncludatorFile includingFile = dependency.getDeclarationReference().getFile();
        IncludeInfo includeToAdd = IncludeHelper.getIncludeDependencyToAdd(declarationFile, includingFile);
        if (IncludeHelper.shouldConsiderInclude(includeToAdd, file, dependency)) {
            AddIncludeSuggestion suggestion = new AddIncludeSuggestion(includeToAdd, file, getClass());
            IncludatorFile fileToInclude = file.getProject().getFile(includeToAdd.getAbsolutePath());
            addedSuggestionsCoverageData.add(new OtherFileCoverageData(suggestion, fileToInclude, declarationFile));
            addSuggestion(suggestion);
        }
    }

    @Override
    public Set<Class<? extends Algorithm>> getInvolvedAlgorithmTypes() {
        HashSet<Class<? extends Algorithm>> involvedAlgorithms = new HashSet<>();
        involvedAlgorithms.add(OrganizeIncludesAlgorithm.class);
        involvedAlgorithms.addAll(super.getInvolvedAlgorithmTypes());
        return involvedAlgorithms;
    }

    @Override
    protected void postRun() {
        initCoverageInfo();
        Collections.sort(addedSuggestionsCoverageData, new IncludeCoverageDataComperator());
        while (!addedSuggestionsCoverageData.isEmpty()) {
            pickFirst();
        }
    }

    protected void initCoverageInfo() {
        for (OtherFileCoverageData coverageData : addedSuggestionsCoverageData) {
            initCoveredIncludes(coverageData);
        }
    }

    private void pickFirst() {
        OtherFileCoverageData first = addedSuggestionsCoverageData.get(0);
        List<OtherFileCoverageData> coveredSuggestions = first.getCoveredSuggestions();
        addedSuggestionsCoverageData.removeAll(coveredSuggestions);
        for (OtherFileCoverageData curCoveredSuggestion : coveredSuggestions) {
            removeSuggestion(curCoveredSuggestion.getSuggestion());
        }
        addedSuggestionsCoverageData.remove(first);
    }

    private void initCoveredIncludes(OtherFileCoverageData coverageData) {
        try {
            RecursiveIndexIncludeStore recursiveIndexIncludeStore = IncludatorPlugin.getRecursiveIndexIncludeStore();
            ArrayList<IIndexInclude> included = recursiveIndexIncludeStore.getRecursiveIncludeRelations(coverageData.getFileToInclude());
            for (OtherFileCoverageData curData : addedSuggestionsCoverageData) {
                if (curData.equals(coverageData)) {
                    continue;
                }
                addIfCovered(curData, included, coverageData);
            }
        } catch (CoreException e) {
            return;
        }
    }

    private void addIfCovered(OtherFileCoverageData dataToMatch, ArrayList<IIndexInclude> included, OtherFileCoverageData coverageDataToAddTo) {
        String pathToMatch = dataToMatch.getRequiredDeclarationFile().getFilePath();
        for (IIndexInclude curInclude : included) {
            IIndexFileLocation includesLocation;
            try {
                includesLocation = curInclude.getIncludesLocation();
                if (includesLocation == null) {
                    // should not happen if indexer is "well".
                    IncludatorPlugin.logStatus(new IncludatorStatus("Failed to get including location of include " + curInclude.getFullName() + "."),
                            file);
                    continue;
                }
            } catch (CoreException e) {
                continue;
            }
            if (FileHelper.uriToStringPath(includesLocation.getURI()).equals(pathToMatch)) {
                coverageDataToAddTo.addCovered(dataToMatch);
            }
        }
    }

    @Override
    public void reset() {
        addedSuggestionsCoverageData.clear();
        super.reset();
    }
}
