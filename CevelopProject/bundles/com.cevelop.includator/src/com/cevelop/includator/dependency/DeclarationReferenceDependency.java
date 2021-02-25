/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.dependency;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.cxxelement.Declaration;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.cxxelement.SpecialMemberFunctionDeclarationReference;
import com.cevelop.includator.helpers.IncludatorException;


public class DeclarationReferenceDependency {

    private Declaration                       declaration;
    private DeclarationReference              declarationReference;
    private ArrayList<FullIncludePath>        includePaths;
    private List<FirstLastElementIncludePath> firstLastElementIncludePaths;
    private boolean                           problemsWhileResolvingFullPaths;
    private boolean                           problemsWhileResolvingFirstLastElementPaths;

    public DeclarationReferenceDependency(DeclarationReference declarationReference, Declaration declaration) {
        this.declarationReference = declarationReference;
        this.declaration = declaration;
        problemsWhileResolvingFullPaths = false;
        problemsWhileResolvingFirstLastElementPaths = false;
    }

    public boolean isLocalDependency() {
        String declFileName = declaration.getFileLocation().getFileName();
        String declRefFileName = declarationReference.getFileLocation().getFileName();
        return declFileName.equals(declRefFileName);
    }

    public Declaration getDeclaration() {
        return declaration;
    }

    public List<FullIncludePath> getIncludePaths() {
        if (includePaths == null) {
            try {
                includePaths = IncludatorPlugin.getIncludePathStore().getIncludePaths(declarationReference.getFile(), declaration.getFile());
            } catch (Exception e) {
                includePaths = new ArrayList<>();
                problemsWhileResolvingFullPaths = true;
            }
        }
        return includePaths;
    }

    @Override
    public String toString() {
        return "Declaration dependency from:\n" + declarationReference + "\n" + "to:\n" + declaration;
    }

    public boolean hadProblemWhileReslovingPaths() {
        boolean wasResolved = (includePaths != null) && !includePaths.isEmpty();
        return !wasResolved || problemsWhileResolvingFullPaths;
    }

    public boolean hadProblemWhileReslovingFirstLastElementPaths() {
        boolean wasResolved = (firstLastElementIncludePaths != null) && !firstLastElementIncludePaths.isEmpty();
        return !wasResolved || problemsWhileResolvingFirstLastElementPaths;

    }

    public DeclarationReference getDeclarationReference() {
        return declarationReference;
    }

    public void clear() {
        declarationReference = null;
        if (declaration != null) {
            declaration.clear();
            declaration = null;
        }
        includePaths = null;
    }

    public List<FirstLastElementIncludePath> getFirstLastElementIncludePaths() {
        if (firstLastElementIncludePaths == null) {
            try {
                firstLastElementIncludePaths = IncludatorPlugin.getFirstLastElementIncludePathStore().getIncludePaths(declarationReference.getFile(),
                        declaration.getFile());
            } catch (Exception e) {
                firstLastElementIncludePaths = new ArrayList<>();
                problemsWhileResolvingFirstLastElementPaths = true;
            }
        }
        return firstLastElementIncludePaths;
    }

    public boolean isSelfDependency() {
        return (declarationReference.getFileLocation().getNodeOffset() == declaration.getFileLocation().getNodeOffset()) && declarationReference
                .getASTNode().toString().equals(declaration.getName().toString()) && isLocalDependency();
    }

    public boolean resolvesToImplicitDeclaration() {
        if (declarationReference instanceof SpecialMemberFunctionDeclarationReference) {
            try {
                final IIndex index = declaration.getFile().getProject().getIndex();
                IBinding declarationBinding = index.findBinding(declaration.getName());
                final IBinding binding = declarationReference.getBinding();
                final IIndexBinding adaptedBinding = index.adaptBinding(binding);
                if (adaptedBinding == null) {
                    return true;
                }
                return !adaptedBinding.equals(declarationBinding);
            } catch (CoreException e) {
                throw new IncludatorException(e);
            }
        }

        return false;
    }
}
