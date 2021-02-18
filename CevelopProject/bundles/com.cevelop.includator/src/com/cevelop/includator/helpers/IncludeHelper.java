/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndexInclude;
import org.eclipse.cdt.core.model.IIncludeEntry;
import org.eclipse.cdt.core.model.IIncludeReference;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.preferences.IncludatorPropertyManager;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.resources.IncludatorProject;


public class IncludeHelper {

    public static IncludatorFile findIncludedFile(IASTPreprocessorIncludeStatement include, IncludatorProject project) {
        if (!include.isResolved()) {
            String location = FileHelper.getSmartFilePath(include.getContainingFilename(), project);
            throw new IncludatorException("Include directive \"" + include + "\" points to an inexisting file.", location);
        }
        String includeFileName = include.getPath();
        IncludatorFile includedFile = project.getFile(includeFileName);
        return includedFile;
    }

    public static IncludatorFile findIncludedFile(IIndexInclude include, IncludatorProject project) throws CoreException {
        if (!include.isResolved()) {
            String location = FileHelper.getSmartFilePath(FileHelper.uriToStringPath(include.getIncludedByLocation().getURI()), project);
            throw new IncludatorException("Include directive \"" + include + "\" points to an inexisting file.", location);
        }
        IncludatorFile includedFile = project.getFile(include.getIncludesLocation().getURI());
        return includedFile;
    }

    public static List<IASTPreprocessorIncludeStatement> findIncludes(IncludatorFile file) {
        ArrayList<IASTPreprocessorIncludeStatement> includes = new ArrayList<>();
        IASTTranslationUnit tu = file.getTranslationUnit();
        if (tu == null) {
            return includes;
        }
        for (IASTPreprocessorIncludeStatement curPreprocessorStatement : tu.getIncludeDirectives()) {
            if (curPreprocessorStatement.isActive()) {
                includes.add(curPreprocessorStatement);
            }
        }
        return includes;
    }

    public static IASTPreprocessorIncludeStatement findIncludeForLocation(IncludatorFile file, int offset) {
        for (IASTPreprocessorIncludeStatement curInclude : file.getIncludes()) {
            int nodeOffset = curInclude.getFileLocation().getNodeOffset();
            if ((nodeOffset < offset) && (nodeOffset + curInclude.getFileLocation().getNodeLength() > offset)) {
                return curInclude;
            }
        }
        return null;
    }

    public static void initIncludeRawSignature(IncludeInfo includeToAdd, String filePath, IncludatorProject project) {
        try {
            initIsSystemInclude(includeToAdd, filePath, project);
            initIncludeRawSignature(includeToAdd);
        } catch (Exception e) {
            initFallbackIncludeRawSignature(includeToAdd, filePath);
        }
    }

    private static void initIsSystemInclude(IncludeInfo includeToAdd, String filePath, IncludatorProject project) {
        IPath fileAbsolutePath = new Path(filePath);
        int minPathLength = includeToAdd.getRelativePath().segmentCount();
        for (IIncludeReference curReference : project.getIncludeReferences()) {
            IPath relativePath = fileAbsolutePath.makeRelativeTo(curReference.getPath());
            if (relativePath.segmentCount() < minPathLength) {
                minPathLength = relativePath.segmentCount();
                includeToAdd.setRelativePath(relativePath);
                IIncludeEntry includeReferenceEntry = curReference.getIncludeEntry();
                if (!includeReferenceEntry.getBasePath().isEmpty()) {
                    // base path is not empty in the case of a workspace path
                    String propertyName = IncludatorPropertyManager.ADD_INCLUDES_TO_OTHER_PROJECTS_AS_SYSTEM_INCLUDE;
                    boolean addIncludesToOtherProjectsAsSystemInclude = IncludatorPropertyManager.getBooleanProperty(project, propertyName);
                    includeToAdd.setIsSystemInclude(addIncludesToOtherProjectsAsSystemInclude);
                } else {
                    includeToAdd.setIsSystemInclude(includeReferenceEntry.isSystemInclude());
                }
            }
            if (minPathLength == 1) {
                break; // minPathLength will never get smaller than 1
            }
        }
    }

    public static String getIncludeReferenceRelativePath(String filePath, IncludatorProject project) {
        int minPathLength = Integer.MAX_VALUE;
        IPath path = FileHelper.stringToPath(filePath);
        IPath curBestPath = null;
        for (IIncludeReference curReference : project.getIncludeReferences()) {
            IPath relativePath = path.makeRelativeTo(curReference.getPath());
            if (relativePath.segmentCount() < minPathLength) {
                minPathLength = relativePath.segmentCount();
                curBestPath = relativePath;
            }
            if (minPathLength == 1) {
                break; // minPathLength will never get smaller than 1
            }
        }
        return curBestPath == null ? filePath : curBestPath.toOSString();
    }

    private static void initFallbackIncludeRawSignature(IncludeInfo includeToAdd, String filePath) {
        includeToAdd.setRelativePath(new Path(new Path(filePath).lastSegment()));
        includeToAdd.setIsSystemInclude(false);
        initIncludeRawSignature(includeToAdd);
    }

    private static void initIncludeRawSignature(IncludeInfo includeToAdd) {
        char startNameDelimiter = (includeToAdd.isSystemInclude()) ? '<' : '"';
        char endNameDelimiter = (includeToAdd.isSystemInclude()) ? '>' : '"';
        includeToAdd.setIncludeStatementString("#include " + startNameDelimiter + includeToAdd.getRelativePath() + endNameDelimiter);
    }

    public static IncludeInfo getIncludeDependencyToAdd(IIndexInclude include, IncludatorFile includingFile) throws CoreException {
        String filePathToInclude = FileHelper.uriToStringPath(include.getIncludesLocation().getURI());
        return getIncludeDependencyToAdd(filePathToInclude, includingFile);
    }

    public static IncludeInfo getIncludeDependencyToAdd(String filePathToInclude, IncludatorFile includingFile) {
        IncludatorProject project = includingFile.getProject();
        String substitutionFilePath = IncludatorPlugin.getIncludeSubstitutionStore().getSubstitutionFilePath(filePathToInclude, project);
        String targetPath = FileHelper.getSmartFilePath(substitutionFilePath, project);
        IncludeInfo includeToAdd = new IncludeInfo(targetPath, substitutionFilePath);
        IPath includingFileFolderPath = FileHelper.stringToPath(includingFile.getFilePath()).removeLastSegments(1);
        includeToAdd.setRelativePath(new Path(substitutionFilePath).makeRelativeTo(includingFileFolderPath));
        initIncludeRawSignature(includeToAdd, substitutionFilePath, project);
        return includeToAdd;
    }

    public static IncludeInfo getIncludeDependencyToAdd(IncludatorFile fileToInclude, IncludatorFile includingFile) {
        return getIncludeDependencyToAdd(fileToInclude.getFilePath(), includingFile);
    }

    public static IASTPreprocessorIncludeStatement findAstIncludeStatement(IncludatorFile fileContainingInclude, IncludatorFile includedFile) {
        for (IASTPreprocessorIncludeStatement curInclude : fileContainingInclude.getIncludes()) {
            if (FileHelper.stringToUri(curInclude.getPath()).equals(FileHelper.stringToUri(includedFile.getFilePath()))) {
                return curInclude;
            }
        }
        return null;
    }

    public static boolean shouldConsiderInclude(IncludeInfo includeToAdd, IncludatorFile includingFile, DeclarationReferenceDependency dependency) {
        IncludatorFile declarationFile = dependency.getDeclaration().getFile();
        if (!declarationFile.isHeaderFile()) {
            String path = declarationFile.getSmartPath();
            DeclarationReference declarationReference = dependency.getDeclarationReference();
            String declStr = declarationReference.toString();
            String statusMsg = "Prevented adding include directive to source file " + path + " which is referenced by " + declStr + ".";
            IncludatorPlugin.logStatus(new IncludatorStatus(IStatus.WARNING, statusMsg), declarationReference.getFile());
            return false;
        }
        return true;
    }

    public static IIndexInclude[] getActiveIncludes(IIndexInclude[] all) {
        ArrayList<IIndexInclude> list = new ArrayList<>(Arrays.asList(all));
        Iterator<IIndexInclude> itr = list.iterator();
        while (itr.hasNext()) {
            try {
                if (!itr.next().isActive()) {
                    itr.remove();
                }
            } catch (CoreException e) {
                throw new IncludatorException(e);
            }
        }
        return list.toArray(new IIndexInclude[list.size()]);
    }
}
