/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.refactorings.create.type;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTemplateDeclaration;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import com.cevelop.tdd.helpers.TddHelper;
import com.cevelop.tdd.infos.TypeInfo;

import ch.hsr.ifs.iltis.core.functional.OptionalUtil;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.SelectionRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringContext;
import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;


public class CreateTypeRefactoring extends SelectionRefactoring<TypeInfo> {

    public CreateTypeRefactoring(final ICElement element, final TypeInfo info) {
        super(element, info);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return Messages.CreateType_name;
    }

    @Override
    protected RefactoringDescriptor getRefactoringDescriptor() {
        String comment = selectedRegion != null ? Messages.CreateType_descWithSection : Messages.CreateType_descWoSection;
        return new CreateTypeRefactoringDescriptor(project.getProject().getName(), Messages.CreateType_name, comment, info);
    }

    @Override
    protected void collectModifications(IProgressMonitor pm, ModificationCollector collector) throws CoreException, OperationCanceledException {
        Optional<IASTName> nameNearSelection = OptionalUtil.of(findFirstEnclosingName(selection)).orElse(findFirstEnclosedName(selection)).get();
        if (!nameNearSelection.isPresent()) return;
        IASTNode newType = createTemplatedType(info.typeName);
        IASTNode insertionPoint = getInsertionPoint(getAST(tu, null), nameNearSelection.get(), refactoringContext);

        if (insertionPoint instanceof CPPASTCompositeTypeSpecifier || insertionPoint instanceof ICPPASTNamespaceDefinition) {
            TddHelper.writeDefinitionTo(collector, insertionPoint, newType);
        } else {
            ASTRewrite rewrite = collector.rewriterForTranslationUnit(getAST(tu, null));
            rewrite.insertBefore(insertionPoint.getParent(), insertionPoint, newType, null);
        }
    }

    public static IASTNode getInsertionPoint(IASTTranslationUnit localunit, IASTName namenearselection, CRefactoringContext context) {
        IASTNode insertionPoint = null;
        if (namenearselection instanceof ICPPASTQualifiedName) {
            insertionPoint = TddHelper.getNestedInsertionPoint(localunit, (ICPPASTQualifiedName) namenearselection, context);
        } else {
            IASTNode parent = namenearselection.getParent();
            if (parent instanceof ICPPASTQualifiedName) {
                insertionPoint = TddHelper.getNestedInsertionPoint(localunit, (ICPPASTQualifiedName) parent, context);
            }
        }
        if (insertionPoint == null) {
            insertionPoint = getFunctionDefinition(namenearselection);
            if (insertionPoint == null) {
                insertionPoint = localunit.getDeclarations()[0];
            }
        }
        return insertionPoint;
    }

    private IASTNode createTemplatedType(String className) {
        IASTSimpleDeclaration simpleddec = createType(className);
        IASTNode result = simpleddec;
        CPPASTTemplateDeclaration templdecl;
        if (info.isTemplateSituation()) {
            templdecl = new CPPASTTemplateDeclaration(simpleddec);
            result = templdecl;
            for (ICPPASTSimpleTypeTemplateParameter templparam : getRefactoringInfo().getTemplateArguments()) {
                templdecl.addTemplateParameter(templparam);
            }
        }
        return result;
    }

    private IASTSimpleDeclaration createType(String className) {
        ICPPASTCompositeTypeSpecifier struct = new CPPASTCompositeTypeSpecifier();
        IASTSimpleDeclaration decl = new CPPASTSimpleDeclaration(struct);
        struct.setKey(1);
        struct.setName(new CPPASTName(className.toCharArray()));
        struct.setParent(decl);
        return decl;
    }

    public static IASTNode getFunctionDefinition(IASTName declaratorName) {
        IASTNode func = TddHelper.getAncestorOfType(declaratorName, ICPPASTFunctionDefinition.class);
        if (func == null) {
            func = TddHelper.getAncestorOfType(declaratorName, ICPPASTCompositeTypeSpecifier.class);
            return TddHelper.getAncestorOfType(func, IASTSimpleDeclaration.class);
        }
        IASTNode template = TddHelper.getLastOfSameAncestor(func.getParent(), ICPPASTTemplateDeclaration.class);
        if (template != null) {
            func = template;
        }
        return func;
    }
}
