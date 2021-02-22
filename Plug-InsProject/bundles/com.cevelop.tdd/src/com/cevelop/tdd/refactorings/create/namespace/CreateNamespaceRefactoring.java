/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.refactorings.create.namespace;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamespaceDefinition;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTQualifiedName;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.SelectionRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;

import com.cevelop.tdd.helpers.TddHelper;
import com.cevelop.tdd.infos.NamespaceInfo;
import com.cevelop.tdd.refactorings.create.type.CreateTypeRefactoring;


public class CreateNamespaceRefactoring extends SelectionRefactoring<NamespaceInfo> {

    public CreateNamespaceRefactoring(final ICElement element, final NamespaceInfo info) {
        super(element, info);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return Messages.CreateNamespace_name;
    }

    @Override
    protected RefactoringDescriptor getRefactoringDescriptor() {
        String comment = selectedRegion != null ? Messages.CreateNamespace_descWithSection : Messages.CreateNamespace_descWoSection;
        return new CreateNamespaceRefactoringDescriptor(project.getProject().getName(), Messages.CreateNamespace_name, comment, info);
    }

    @Override
    protected void collectModifications(IProgressMonitor pm, ModificationCollector collector) throws CoreException, OperationCanceledException {
        IASTTranslationUnit localunit = getAST(tu, pm);
        Optional<IASTName> maybeNode = findFirstEnclosingName(selection);
        if (!maybeNode.isPresent()) {
            return;
        }
        IASTNode selectedNode = maybeNode.get();
        IASTName selectedNodeName = TddHelper.getAncestorOfType(selectedNode, CPPASTName.class);
        ICPPASTQualifiedName qname = TddHelper.getAncestorOfType(selectedNode, CPPASTQualifiedName.class);
        if (selectedNode instanceof IASTName && qname != null && selectedNodeName != null) {
            ICPPASTNamespaceDefinition ns = new CPPASTNamespaceDefinition(selectedNodeName.copy());
            IASTNode insertionPoint = CreateTypeRefactoring.getInsertionPoint(localunit, selectedNodeName, refactoringContext);

            // TODO: Remvoe duplicated crom createclass
            if (insertionPoint instanceof CPPASTCompositeTypeSpecifier || insertionPoint instanceof CPPASTNamespaceDefinition) {
                TddHelper.writeDefinitionTo(collector, insertionPoint, ns);
            } else {
                ASTRewrite rewrite = collector.rewriterForTranslationUnit(localunit);
                rewrite.insertBefore(insertionPoint.getParent(), insertionPoint, ns, null);
            }
        }
    }

}
