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
package com.cevelop.namespactor.refactoring.eudir;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDirective;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNamespace;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.jface.text.ITextSelection;

import com.cevelop.namespactor.astutil.ASTNodeFactory;
import com.cevelop.namespactor.astutil.NSNameHelper;
import com.cevelop.namespactor.refactoring.eu.EURefactoring;
import com.cevelop.namespactor.refactoring.eu.EURefactoringContext;
import com.cevelop.namespactor.refactoring.eu.EUReplaceVisitor;


/**
 * @author Jules Weder
 */
@SuppressWarnings("restriction")
public class EUDirRefactoring extends EURefactoring {

    public EUDirRefactoring(ICElement element, Optional<ITextSelection> selection) {
        super(element, selection);
    }

    @Override
    protected EUReplaceVisitor getReplaceVisitor() {
        return new EUDirReplaceVisitor(context);
    }

    @Override
    protected IASTNode prepareInsertStatement() {
        ICPPASTUsingDirective newUsingDirective = ASTNodeFactory.getDefault().newUsingDirective(context.qualifiedUsingName);
        if (!(scopeNode instanceof ICPPASTNamespaceDefinition)) {
            return ASTNodeFactory.getDefault().newDeclarationStatement(newUsingDirective);
        }
        return newUsingDirective;
    }

    @Override
    protected ICPPASTQualifiedName buildUsingNameFrom(ICPPASTQualifiedName qualifiedName) {
        IASTName lastName = qualifiedName.getLastName();
        String[] names = NSNameHelper.getQualifiedUsingName(lastName.resolveBinding());

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
        IBinding binding;
        ICPPASTNameSpecifier[] names = context.selectedQualifiedName.getQualifier();
        for (int i = names.length - 1; i >= 0; i--) {
            binding = names[i].resolveBinding();
            if (binding instanceof ICPPNamespace) {
                context.startingNamespaceName = (IASTName) names[i]; // must be a name
                return;
            }
        }
    }

    @Override
    protected IASTNode findTypeScope() {
        return null;
    }

}
