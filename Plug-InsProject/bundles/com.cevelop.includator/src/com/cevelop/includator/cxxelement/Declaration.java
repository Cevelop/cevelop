/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.cxxelement;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.helpers.IndexToASTNameHelper;
import com.cevelop.includator.resources.IncludatorFile;


public class Declaration {

    private IName          declarationName;
    private IASTName       declarationNameNode;
    private IncludatorFile file;

    public Declaration(IName declarationNode, IncludatorFile file) {
        this.declarationName = declarationNode;
        this.file = file;
    }

    public IASTFileLocation getFileLocation() {
        return FileHelper.getFileLocation(declarationName);
    }

    public IncludatorFile getFile() {
        return file;
    }

    @Override
    public String toString() {
        return declarationName + " in " + getLocationStr();
    }

    public String getLocationStr() {
        return FileHelper.getLocationStrForIName(declarationName, file.getProject());
    }

    public IName getName() {
        return declarationName;
    }

    public void clear() {
        declarationName = null;
        declarationNameNode = null;
        file = null;
    }

    public boolean isDefinition() {
        return declarationName.isDefinition();
    }

    public IASTName getASTName() {
        if (declarationNameNode == null) {
            try {
                IASTTranslationUnit tu = file.getTranslationUnit();
                if (tu != null) {
                    declarationNameNode = IndexToASTNameHelper.findMatchingASTName(tu, declarationName, file.getProject().getIndex());
                }
            } catch (CoreException e) {
                throw new IncludatorException(e);
            }
        }
        return declarationNameNode;
    }
}
