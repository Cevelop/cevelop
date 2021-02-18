/******************************************************************************
 * Copyright (c) 2012 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ueli Kunz <kunz@ideadapt.net>, Jules Weder <julesweder@gmail.com> - initial API and implementation
 ******************************************************************************/
package com.cevelop.namespactor.refactoring.iu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.dom.ILinkage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFile;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.index.IIndexInclude;
import org.eclipse.cdt.core.index.IndexLocationFactory;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.utils.UNCPathConverter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.URIUtil;

import com.cevelop.namespactor.Activator;


/**
 * @author kunz@ideadapt.net
 */
public class IncludeDependencyAnalyser {

    private final Map<Integer, Boolean> fileDependencies = new HashMap<>();
    private IIndex                      index            = null;

    public IncludeDependencyAnalyser(IIndex index) {
        this.index = index;
    }

    public boolean areFilesIncludeDependent(IIndexFile file, String originFileName) throws CoreException {
        String namesFileName = URIUtil.toFile(file.getLocation().getURI()).getAbsolutePath();

        if (namesFileName.equals(originFileName)) {
            return true;
        }

        Integer filePairId = (namesFileName + originFileName).hashCode();
        Boolean dependencyCachedValue = fileDependencies.get(filePairId);

        if (dependencyCachedValue != null) {
            return dependencyCachedValue;
        }

        IIndexInclude[] includedBy = index.findIncludedBy(file, IIndex.DEPTH_INFINITE);
        for (IIndexInclude include : includedBy) {
            String includedByFileName = URIUtil.toFile(include.getIncludedByLocation().getURI()).getAbsolutePath();

            if (originFileName.equals(includedByFileName)) {
                fileDependencies.put(filePairId, true);
                return true;
            }
        }

        IIndexInclude[] includes = index.findIncludes(file, IIndex.DEPTH_INFINITE);
        for (IIndexInclude include : includes) {
            IIndexFileLocation includesLocation = include.getIncludesLocation();
            if (includesLocation == null) continue;
            String includedFileName = URIUtil.toFile(includesLocation.getURI()).getAbsolutePath();

            if (originFileName.equals(includedFileName)) {
                fileDependencies.put(filePairId, true);
                return true;
            }
        }

        fileDependencies.put(filePairId, false);
        return false;
    }

    public List<IPath> getIncludeDependentPathsOf(ITranslationUnit tu) {

        List<IPath> dependentFiles = new ArrayList<>();

        try {
            IIndexFileLocation ifl = IndexLocationFactory.getIFL(tu);
            IIndexFile[] files = index.getFiles(ILinkage.CPP_LINKAGE_ID, ifl);
            if (files.length <= 0) return dependentFiles;
            IIndexFile iFile = files[0];

            // h file is only in the indexer if its included by another file (h or cpp)
            // cpp file always in indexer
            if (iFile == null) {
                return dependentFiles;
            }
            IPath wsRootPath = CoreModel.getDefault().getCModel().getWorkspace().getRoot().getLocation();

            IIndexInclude[] includedBy = index.findIncludedBy(iFile, IIndex.DEPTH_INFINITE);
            for (IIndexInclude include : includedBy) {
                if (include.isResolved() == true) {
                    IPath includePath = UNCPathConverter.toPath(include.getIncludesLocation().getURI());
                    if (wsRootPath.isPrefixOf(includePath)) {
                        dependentFiles.add(UNCPathConverter.toPath(include.getIncludedByLocation().getURI()));
                    }
                }
            }

            IIndexInclude[] includes = index.findIncludes(iFile, IIndex.DEPTH_INFINITE);
            for (IIndexInclude include : includes) {

                if (include.isResolved() == true) {
                    IPath includePath = UNCPathConverter.toPath(include.getIncludesLocation().getURI());
                    if (wsRootPath.isPrefixOf(includePath)) {
                        dependentFiles.add(UNCPathConverter.toPath(include.getIncludesLocation().getURI()));
                    }
                }
            }
        } catch (CoreException e) {
            Activator.log("Exception while getting include dependent paths of " + tu.getElementName(), e);
        }

        return dependentFiles;
    }
}
