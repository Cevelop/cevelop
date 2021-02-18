/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.helpers.tuprovider;

import java.net.URI;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IFile;

import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorException;


public class TranslationUnitProvider {

    public static final int AST_STYLE = ITranslationUnit.AST_CONFIGURE_USING_SOURCE_CONTEXT | ITranslationUnit.AST_SKIP_INDEXED_HEADERS;

    protected ITranslationUnit translationUnit;
    private final ICProject    project;
    private final IFile        file;
    private final URI          uri;

    public TranslationUnitProvider(URI uri, ICProject project) {
        this.uri = uri;
        this.file = FileHelper.getIFile(uri);
        this.project = project;
    }

    public TranslationUnitProvider(IFile file) {
        this.uri = file.getLocationURI();
        this.file = file;
        this.project = CoreModel.getDefault().create(file.getProject());
    }

    public IASTTranslationUnit getASTTranslationUnit(IIndex index) {
        try {
            initTranslationUnit();
            if (translationUnit == null) {
                return null;
            }
            return translationUnit.getAST(index, AST_STYLE);
        } catch (Exception e) {
            throw new IncludatorException("Error wihle retriving AST for file '" + uri + "'.", e);
        }
    }

    public boolean isHeaderFile() {
        initTranslationUnit();
        if (translationUnit == null) {
            return false;
        }
        return translationUnit.isHeaderUnit();
    }

    protected void initTranslationUnit() {
        if (translationUnit == null) {
            translationUnit = CoreModelUtil.findTranslationUnit(file);
            if (translationUnit == null) {
                translationUnit = CoreModel.getDefault().createTranslationUnitFrom(project, uri);
            }
        }
    }

    public void purge() {
        translationUnit = null;
    }

    public boolean hasCOrCPPLanguage() {
        return FileHelper.isCLikeFile(project.getProject(), FileHelper.uriToPath(uri).lastSegment());
    }

    @Override
    public String toString() {
        return getClass().getName() + " for " + uri.getPath();
    }
}
