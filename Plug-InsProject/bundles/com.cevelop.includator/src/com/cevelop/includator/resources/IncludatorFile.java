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
import java.util.Collection;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.core.resources.IFile;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.helpers.DeclarationReferenceHelper;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorStatus;
import com.cevelop.includator.helpers.IncludeHelper;
import com.cevelop.includator.helpers.tuprovider.TranslationUnitProvider;


public class IncludatorFile {

    private final IncludatorProject                project;
    private Collection<DeclarationReference>       declarationReferences;
    private IASTTranslationUnit                    translationUnit;
    private TranslationUnitProvider                tuProvider;
    private List<IASTPreprocessorIncludeStatement> includes;

    private final IFile  iFile;
    private boolean      problemsWhileResolvingIncludes;
    private final String fileName;

    public IncludatorFile(URI fileUri, TranslationUnitProvider tuProvider, IncludatorProject project) {
        this.fileName = FileHelper.uriToStringPath(fileUri);
        this.tuProvider = tuProvider;
        this.project = project;
        this.iFile = FileHelper.getIFile(fileUri);
        problemsWhileResolvingIncludes = false;
    }

    public IncludatorFile(IFile file, TranslationUnitProvider tuProvider, IncludatorProject project) {
        this.iFile = file;
        this.fileName = FileHelper.uriToStringPath(file.getLocationURI());
        this.tuProvider = tuProvider;
        this.project = project;
        problemsWhileResolvingIncludes = false;
    }

    public Collection<DeclarationReference> getDeclarationReferences() {
        if (declarationReferences == null) {
            IASTTranslationUnit tu = getTranslationUnit();
            if (tu != null) {
                declarationReferences = DeclarationReferenceHelper.findDeclReferences(tu, this);
            }
        }
        return declarationReferences;
    }

    public List<IASTPreprocessorIncludeStatement> getIncludes() {
        if (includes == null) {
            try {
                includes = IncludeHelper.findIncludes(this);
            } catch (Exception e) {
                problemsWhileResolvingIncludes = true;
                includes = new ArrayList<>();
            }
        }
        return includes;
    }

    public IncludatorProject getProject() {
        return project;
    }

    public String getProjectRelativePath() {
        if (iFile != null) {
            return iFile.getProjectRelativePath().toOSString();
        }
        return FileHelper.makeProjectRelativePath(fileName, project);
    }

    public boolean hadProblemsWhileResolvingIncludes() {
        boolean whereIncludesResolved = includes != null;
        return whereIncludesResolved && problemsWhileResolvingIncludes;
    }

    public void clear() {
        purge();
        tuProvider = null;
    }

    public void purge() {
        if (declarationReferences != null) {
            for (DeclarationReference curRef : declarationReferences) {
                curRef.clear();
            }
            declarationReferences.clear();
            declarationReferences = null;
        }
        if (includes != null) {
            includes.clear();
            includes = null;
        }
        if (tuProvider != null) {
            tuProvider.purge();
        }
        translationUnit = null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IncludatorFile)) {
            return false;
        }
        IncludatorFile other = (IncludatorFile) obj;
        return fileName.equals(other.fileName);
    }

    public IFile getIFile() {
        return iFile;
    }

    public boolean isPartOfProject() {
        if (iFile != null) {
            return FileHelper.isPartOfProject(iFile, project);
        }
        return false;
    }

    @Override
    public String toString() {
        return getFilePath();
    }

    public IASTTranslationUnit getTranslationUnit() {
        if (translationUnit == null) {
            translationUnit = tuProvider.getASTTranslationUnit(project.getIndex());
            if (translationUnit == null) {
                IncludatorPlugin.logStatus(new IncludatorStatus("Failed to load translation for file '" + fileName + "'."), this);
            }
        }
        return translationUnit;
    }

    @Override
    public int hashCode() {
        return fileName.hashCode();
    }

    public String getFilePath() {
        return fileName;
    }

    public boolean isHeaderFile() {
        return tuProvider.isHeaderFile();
    }

    /**
     * @return Either project-relative path it inside project or absolute path if outside project.
     */
    public String getSmartPath() {
        return FileHelper.getSmartFilePath(fileName, getProjectRelativePath());
    }
}
