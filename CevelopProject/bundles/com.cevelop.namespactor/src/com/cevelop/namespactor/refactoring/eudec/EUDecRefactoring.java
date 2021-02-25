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
package com.cevelop.namespactor.refactoring.eudec;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNamespace;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.jface.text.ITextSelection;

import com.cevelop.namespactor.astutil.ASTNodeFactory;
import com.cevelop.namespactor.astutil.NSNameHelper;
import com.cevelop.namespactor.astutil.NSNodeHelper;
import com.cevelop.namespactor.refactoring.TemplateIdFactory;
import com.cevelop.namespactor.refactoring.eu.EURefactoring;
import com.cevelop.namespactor.refactoring.eu.EURefactoringContext;
import com.cevelop.namespactor.refactoring.eu.EUReplaceVisitor;
import com.cevelop.namespactor.resources.Labels;


/**
 * @author Jules Weder
 */
@SuppressWarnings("restriction")
public class EUDecRefactoring extends EURefactoring {

    public EUDecRefactoring(ICElement element, Optional<ITextSelection> selection, ICProject project) {
        super(element, selection);
    }

    @Override
    protected EUReplaceVisitor getReplaceVisitor() {
        return new EUDecReplaceVisitor(context);
    }

    @Override
    protected IASTNode prepareInsertStatement() {
        ICPPASTUsingDeclaration newUsingDeclaration = ASTNodeFactory.getDefault().newUsingDeclaration(context.qualifiedUsingName);
        if (!(scopeNode instanceof ICPPASTCompositeTypeSpecifier || scopeNode instanceof ICPPASTNamespaceDefinition)) {
            return ASTNodeFactory.getDefault().newDeclarationStatement(newUsingDeclaration);
        }
        return newUsingDeclaration;
    }

    @Override
    protected ICPPASTQualifiedName buildUsingNameFrom(ICPPASTQualifiedName qualifiedName) {
        IASTName lastNameOfqName = qualifiedName.getLastName();
        String[] names = null;
        IBinding lastNameToAddBinding;
        boolean inCompositeDeclaration = NSNodeHelper.isInOrIsCompositeDeclaration(lastNameOfqName);
        if (inCompositeDeclaration) {
            if (TemplateIdFactory.isOrContainsTemplateId(qualifiedName)) {
                return qualifiedName.copy(CopyStyle.withLocations);//buildNameWithTemplate(qualifiedName);
            }
            names = NSNameHelper.getQualifiedUDECNameInTypeDecl(lastNameOfqName.resolveBinding());
            lastNameToAddBinding = lastNameOfqName.resolveBinding();
        } else {
            names = NSNameHelper.getQualifiedUsingName(lastNameOfqName.resolveBinding());
            lastNameToAddBinding = NSNameHelper.findOutermostClassTypeOfName(lastNameOfqName.resolveBinding());
        }

        names = addLastName(lastNameOfqName, names, lastNameToAddBinding);

        if (names.length > 0) {
            ICPPASTQualifiedName qname = ASTNodeFactory.getDefault().newQualifiedNameNode(names);
            // Bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=381032
            qname.setFullyQualified(context.selectedQualifiedName.isFullyQualified());
            return qname;
        }
        return null;
    }

    @Override
    protected void findStartingNames(EURefactoringContext context) {
        IBinding outermostTypeBinding;
        if (NSNodeHelper.isInOrIsCompositeDeclaration(context.selectedQualifiedName)) {
            outermostTypeBinding = context.selectedQualifiedName.getLastName().resolveBinding();
        } else {
            outermostTypeBinding = NSNameHelper.findOutermostClassTypeOfName(context.selectedQualifiedName.getLastName().resolveBinding());
        }

        ICPPASTNameSpecifier[] names = context.selectedQualifiedName.getAllSegments();
        if (outermostTypeBinding != null) {
            ICPPASTNameSpecifier precedingName = null;
            for (ICPPASTNameSpecifier iastName : names) {
                if (iastName.resolveBinding().equals(outermostTypeBinding)) {
                    context.startingTypeName = (IASTName) iastName; // check if startingTypeName should be ICPPASTNameSpecifier
                    context.startingNamespaceName = (IASTName) precedingName;
                    return;
                }
                precedingName = iastName;
            }
        }

        IBinding binding;
        for (int i = names.length - 1; i >= 0; i--) {
            binding = names[i].resolveBinding();
            if (binding instanceof ICPPNamespace) {
                context.startingNamespaceName = (IASTName) names[i];
                return;
            }
        }
    }

    private static String[] addLastName(IASTName lastNameOfqName, String[] names, IBinding lastNameToAddBinding) {
        String lastNameToAdd = null;
        if (lastNameToAddBinding != null) {
            lastNameToAdd = lastNameToAddBinding.getName();
        }
        if (lastNameToAdd == null) {
            lastNameToAdd = lastNameOfqName.resolveBinding().getName();
        }
        names = ArrayUtil.append(names, lastNameToAdd);
        names = ArrayUtil.trim(names);
        return names;
    }

    @Override
    public IASTNode findTypeScope() {
        IASTNode scopeNode = null;
        if (context.selectedLastName.resolveBinding() instanceof ICPPClassType) {
            if (context.selectedQualifiedName.getQualifier().length > 0) {
                ICPPASTNameSpecifier typeName = context.selectedQualifiedName.getQualifier()[context.selectedQualifiedName.getQualifier().length - 1];
                scopeNode = NSNodeHelper.findAncestorOf(context.selectedQualifiedName, ICPPASTCompositeTypeSpecifier.class);
                if (scopeNode != null) {
                    ICPPASTBaseSpecifier[] baseSpecifiers = ((ICPPASTCompositeTypeSpecifier) scopeNode).getBaseSpecifiers();
                    boolean foundBaseType = false;
                    for (ICPPASTBaseSpecifier baseSpec : baseSpecifiers) {
                        if (baseSpec.getNameSpecifier().resolveBinding().equals(typeName.resolveBinding())) {
                            foundBaseType = true;
                            break;
                        }
                    }
                    if (!foundBaseType) {
                        scopeNode = null;
                        initStatus.addFatalError(Labels.No_ExtractInTypeWithoutCorrectInheritance);
                    }
                }
            } else {
                initStatus.addFatalError(Labels.No_QName_Selected);
            }
        }
        return scopeNode;
    }
}
