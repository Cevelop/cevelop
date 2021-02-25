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
package com.cevelop.namespactor.refactoring;

import java.util.HashMap;
import java.util.Optional;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ISourceReference;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.corext.util.CModelUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;

import com.cevelop.namespactor.Activator;
import com.cevelop.namespactor.NSAssert;
import com.cevelop.namespactor.refactoring.rewrite.ASTRewriteStore;


/**
 * @author Ueli Kunz, Jules Weder
 */
@SuppressWarnings("restriction")
public abstract class RefactoringBase extends CRefactoring {

    HashMap<String, ITranslationUnit> tuCache = new HashMap<>();

    public RefactoringBase(ICElement element, Optional<ITextSelection> selection) {
        super(element, selection);
    }

    protected IASTTranslationUnit getASTOf(IName ref, IProgressMonitor pm) {
        String fileName = ref.getFileLocation().getFileName();
        ITranslationUnit tu = getTuForFilename(fileName);
        if (tu == null) {
            return null;
        }
        try {
            return getAST(tu, pm);
        } catch (OperationCanceledException e) {
            Activator.log("Exception in getASTOf().", e);
        } catch (CoreException e) {
            Activator.log("Exception in getASTOf().", e);
        }
        return null;
    }

    public ITranslationUnit getTuForFilename(String fileName) {
        ITranslationUnit tu = tuCache.get(fileName);

        if (tu == null) {
            try {
                tu = getTUOf(project.findElement(new Path(fileName)));
            } catch (CModelException e) { // will fail for external files, i.e. system headers
            }
            if (tu == null) {
                try {
                    tu = CoreModelUtil.findTranslationUnitForLocation(new Path(fileName), project);
                } catch (CModelException e) {
                    Activator.log("Exception in getTuForFilename().", e);
                }
            }
        }
        return tu;
    }

    public static ITranslationUnit getTUOf(ICElement element) {
        ITranslationUnit tu;
        ISourceReference sourceRef = (ISourceReference) element;
        tu = CModelUtil.toWorkingCopy(sourceRef.getTranslationUnit());
        return tu;
    }

    protected String toStringDebug(IASTNode node) {
        return String.format("--%nline: %s, class: %s#%s %nraw:%n%s %nparent: %s %n%n", node.getFileLocation().getStartingLineNumber(), node
                .getClass().getName(), node.hashCode(), node.getRawSignature(), node.getParent().toString());
    }

    @Override
    protected RefactoringDescriptor getRefactoringDescriptor() {
        return null;
    }

    protected void collectModifications(ASTRewriteStore store) {
        store.performChanges();
    }

    protected IASTName getNodeOf(IName name, IProgressMonitor pm) throws CoreException, NodeDefinitionNotInWorkspaceException {

        IASTTranslationUnit astOf = getASTOf(name, pm);
        if (astOf == null) {
            throw new NodeDefinitionNotInWorkspaceException();
        }
        IASTNode childRefNode = astOf.getNodeSelector(name.getFileLocation().getFileName()).findNode(name.getFileLocation().getNodeOffset(), name
                .getFileLocation().getNodeLength());
        NSAssert.isInstanceOf(IASTName.class, childRefNode);
        return (IASTName) childRefNode;
    }

    @Override
    public void collectModifications(IProgressMonitor pm, ModificationCollector collector) throws CoreException, OperationCanceledException {
        collectModifications(new ASTRewriteStore(collector));
    }

}
