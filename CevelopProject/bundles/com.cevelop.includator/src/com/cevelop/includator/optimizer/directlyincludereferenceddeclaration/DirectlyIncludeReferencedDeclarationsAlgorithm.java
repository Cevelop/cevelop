/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.optimizer.directlyincludereferenceddeclaration;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludeHelper;
import com.cevelop.includator.helpers.IncludeInfo;
import com.cevelop.includator.helpers.comparators.SourceTargetFileComparator;
import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.resources.IncludatorFile;


public class DirectlyIncludeReferencedDeclarationsAlgorithm extends Algorithm {

    private Collection<IASTPreprocessorIncludeStatement> existingIncludes;
    private IncludatorFile                               file;

    @Override
    public void run() {
        runAlgorithm();
    }

    private void runAlgorithm() {
        file = startingPoint.getFile();
        existingIncludes = getAllIncludeDependencies();
        monitorWorked(0.35);
        Collection<DeclarationReferenceDependency> dependencies = getAllDependencies(file.getDeclarationReferences());
        monitorWorked(0.2);
        monitorWorked(0.35);

        addRequiredIncludes(dependencies);
        monitorWorked(0.1);
    }

    private Collection<IASTPreprocessorIncludeStatement> getAllIncludeDependencies() {

        List<IASTPreprocessorIncludeStatement> result = new ArrayList<>();
        addIncludeDependencies(result, file);
        IncludatorFile header = FileHelper.getNameCorrelatingHeaderFile(file);
        if (header != null) {
            addIncludeDependencies(result, header);
        }
        return result;
    }

    private static void addIncludeDependencies(List<IASTPreprocessorIncludeStatement> result, IncludatorFile file) {
        for (IASTPreprocessorIncludeStatement curIncludeStmt : file.getIncludes()) {
            result.add(curIncludeStmt);
        }
    }

    private Collection<DeclarationReferenceDependency> getAllDependencies(Collection<DeclarationReference> references) {
        TreeSet<DeclarationReferenceDependency> results = new TreeSet<>(new SourceTargetFileComparator());
        for (DeclarationReference curReference : references) {
            for (DeclarationReferenceDependency curDependency : curReference.getRequiredDependencies()) {
                if (!curDependency.isLocalDependency()) {
                    results.add(curDependency);
                }
            }
        }
        return results;
    }

    private boolean isAllreadyIncluded(IncludatorFile file) {
        final String filePathToInclude = file.getFilePath();
        final String substitutionFilePath = IncludatorPlugin.getIncludeSubstitutionStore().getSubstitutionFilePath(filePathToInclude, startingPoint
                .getProject());
        final URI fileUri = FileHelper.stringToUri(substitutionFilePath);

        for (IASTPreprocessorIncludeStatement curInclude : existingIncludes) {
            if (FileHelper.stringToUri(curInclude.getPath()).equals(fileUri)) {
                return true;
            }
        }
        return false;
    }

    private void addRequiredIncludes(Collection<DeclarationReferenceDependency> dependencies) {
        for (DeclarationReferenceDependency curDependency : dependencies) {
            if (!curDependency.isLocalDependency() && !isAllreadyIncluded(curDependency.getDeclaration().getFile())) {
                IncludatorFile targetFile = curDependency.getDeclaration().getFile();
                IncludatorFile includingFile = curDependency.getDeclarationReference().getFile();
                IncludeInfo includeToAdd = IncludeHelper.getIncludeDependencyToAdd(targetFile, includingFile);
                if (IncludeHelper.shouldConsiderInclude(includeToAdd, file, curDependency)) {
                    addSuggestion(new AddIncludeSuggestion(includeToAdd, file, DirectlyIncludeReferencedDeclarationsAlgorithm.class));
                }
            }
        }
    }

    @Override
    public String getInitialProgressMonitorMessage(String resourceName) {
        return "Finding directly includable declarations in " + resourceName;
    }

    @Override
    public Set<Class<? extends Algorithm>> getInvolvedAlgorithmTypes() {
        HashSet<Class<? extends Algorithm>> involvedAlgorithms = new HashSet<>();
        involvedAlgorithms.add(DirectlyIncludeReferencedDeclarationsAlgorithm.class);
        return involvedAlgorithms;
    }

    @Override
    public AlgorithmScope getScope() {
        return AlgorithmScope.EDITOR_SCOPE;
    }

    @Override
    public void reset() {
        existingIncludes = null;
        file = null;
        super.reset();
    }
}
