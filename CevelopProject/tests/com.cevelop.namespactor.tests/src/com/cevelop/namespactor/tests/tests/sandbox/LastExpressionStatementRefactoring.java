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
package com.cevelop.namespactor.tests.tests.sandbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeclarationStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTUsingDirective;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;


@SuppressWarnings("restriction")
public class LastExpressionStatementRefactoring extends CRefactoring {

    List<ICPPASTQualifiedName> qnames = new ArrayList<>();

    public LastExpressionStatementRefactoring(ICElement element, Optional<ITextSelection> selection, ICProject project) {
        super(element, selection);
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {

        qnames = getQNames(this.selectedRegion, getAST(tu, pm));
        Assert.isTrue(qnames.size() == 2);

        return super.checkInitialConditions(pm);
    }

    @Override
    protected RefactoringStatus checkFinalConditions(IProgressMonitor pm, CheckConditionsContext checkContext) throws CoreException,
            OperationCanceledException {
        return new RefactoringStatus();
    }

    @Override
    protected void collectModifications(IProgressMonitor pm, ModificationCollector collector) throws CoreException, OperationCanceledException {
        // insert using directive before first(qnames)
        IASTNode insertionPoint = qnames.get(0).getParent().getParent().getParent();
        IASTNode enclosingCompound = ((ICPPASTFunctionDefinition) insertionPoint.getParent().getParent()).getBody();
        ASTRewrite rUDIR = collector.rewriterForTranslationUnit(getAST(tu, pm));
        IASTDeclarationStatement newUDIR = new CPPASTDeclarationStatement(new CPPASTUsingDirective(new CPPASTName("A".toCharArray())));

        rUDIR.insertBefore(enclosingCompound, insertionPoint, newUDIR, null);

        // replace.each(qnames, q).by(unqualified(q))
        for (ICPPASTQualifiedName qname : qnames) {
            ASTRewrite rUQName = collector.rewriterForTranslationUnit(getAST(tu, pm));
            IASTName newName = new CPPASTName(qname.getLastName().toCharArray());
            rUQName.replace(qname, newName, null);
        }
    }

    @Override
    protected RefactoringDescriptor getRefactoringDescriptor() {
        return null;
    }

    private static List<ICPPASTQualifiedName> getQNames(final Region textSelection, IASTTranslationUnit tu) {
        final List<ICPPASTQualifiedName> qnames = new ArrayList<>();

        if (textSelection.getLength() > 0) {

            tu.accept(new ASTVisitor() {

                {
                    shouldVisitNames = true;
                }

                @Override
                public int visit(IASTName name) {
                    if (name instanceof ICPPASTQualifiedName) {
                        qnames.add((ICPPASTQualifiedName) name);
                    }
                    return super.visit(name);
                }
            });
        }

        return qnames;
    }
}
