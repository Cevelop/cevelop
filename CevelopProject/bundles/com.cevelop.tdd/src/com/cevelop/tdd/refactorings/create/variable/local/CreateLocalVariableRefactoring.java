/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.refactorings.create.variable.local;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTMacroExpansionLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTBaseDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeclarationStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import com.cevelop.tdd.helpers.TddHelper;
import com.cevelop.tdd.helpers.TypeHelper;
import com.cevelop.tdd.infos.LocalVariableInfo;
import com.cevelop.tdd.refactorings.create.variable.member.Messages;

import ch.hsr.ifs.iltis.core.functional.OptionalUtil;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.SelectionRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;


public class CreateLocalVariableRefactoring extends SelectionRefactoring<LocalVariableInfo> {

    public CreateLocalVariableRefactoring(final ICElement element, final LocalVariableInfo info) {
        super(element, info);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return Messages.CreateMemberVariable_name;
    }

    @Override
    protected RefactoringDescriptor getRefactoringDescriptor() {
        String comment = selectedRegion != null ? Messages.CreateMemberVariable_descWithSection : Messages.CreateMemberVariable_descWoSection;
        return new CreateLocalVariableRefactoringDescriptor(project.getProject().getName(), Messages.CreateMemberVariable_name, comment, info);
    }

    @Override
    protected void collectModifications(IProgressMonitor pm, ModificationCollector collector) throws CoreException, OperationCanceledException {
        IASTTranslationUnit localunit = getAST(tu, pm);
        Optional<IASTName> maybeNode = OptionalUtil.of(findFirstEnclosedName(selection)).orElse(findFirstEnclosingName(selection)).get();
        if (!maybeNode.isPresent()) return;
        IASTNode selectedNode = maybeNode.get();

        IASTIdExpression owner = TddHelper.getAncestorOfType(selectedNode, IASTIdExpression.class);
        IASTName variableName = new CPPASTName(info.name.toCharArray());

        CPPASTBaseDeclSpecifier spec = CreateLocalVariableRefactoring.calculateType(localunit, selection.get());
        CPPASTSimpleDeclaration simpledec = new CPPASTSimpleDeclaration(spec);
        simpledec.addDeclarator(new CPPASTDeclarator(variableName));

        CPPASTDeclarationStatement decl = new CPPASTDeclarationStatement(simpledec);
        IASTFunctionDefinition outerFunction = TddHelper.getOuterFunctionDeclaration(localunit, selection.get());

        IASTNode insertionPoint = getInsertionPointFromMacro(owner);
        if (insertionPoint == null) {
            insertionPoint = getSimpleInsertionPoint(owner);
        }

        ASTRewrite rewrite = collector.rewriterForTranslationUnit(localunit);
        rewrite.insertBefore(outerFunction.getBody(), insertionPoint, decl, null);
    }

    private IASTNode getSimpleInsertionPoint(IASTIdExpression owner) {
        IASTNode result;
        result = TddHelper.getAncestorOfType(owner, IASTExpressionStatement.class);
        if (result == null) {
            result = TddHelper.getAncestorOfType(owner, CPPASTDeclarationStatement.class);
        }
        return result;
    }

    private IASTNode getInsertionPointFromMacro(IASTIdExpression owner) {
        IASTNode insertionPoint = null;
        IASTNode current = owner;
        while (current != null && current.getNodeLocations().length > 0 && current.getNodeLocations()[0] instanceof IASTMacroExpansionLocation) {
            insertionPoint = current;
            current = current.getParent();
        }
        return insertionPoint;
    }

    public static CPPASTBaseDeclSpecifier calculateType(IASTTranslationUnit localunit, ITextSelection selection) {
        CPPASTBaseDeclSpecifier type = TypeHelper.findTypeInAst(localunit, selection);
        if (type == null) {
            return TypeHelper.getDefaultType();
        }
        return type;
    }
}
