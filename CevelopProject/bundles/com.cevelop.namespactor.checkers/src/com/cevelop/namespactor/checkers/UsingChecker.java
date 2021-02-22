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
package com.cevelop.namespactor.checkers;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDirective;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor;

import ch.hsr.ifs.iltis.cpp.core.wrappers.AbstractIndexAstChecker;

import com.cevelop.namespactor.helpers.IdHelper.ProblemId;


/**
 * @author kunz@ideadapt.net
 */
@SuppressWarnings("restriction")
public class UsingChecker extends AbstractIndexAstChecker {

    @Override
    public void processAst(IASTTranslationUnit ast) {

        checkTypedefs(ast);

        if (ast.isHeaderUnit()) {
            checkUsingInHeader(ast);
        }
        checkUsingBeforeInclude(ast);
        checkUDIRWithUnqualifiedName(ast);
    }

    private void checkUDIRWithUnqualifiedName(IASTTranslationUnit ast) {

        for (IASTDeclaration decl : ast.getDeclarations()) {
            if (decl instanceof ICPPASTUsingDirective) {
                ICPPASTUsingDirective udir = (ICPPASTUsingDirective) decl;
                IASTName udirName = udir.getQualifiedName();
                String[] qname = CPPVisitor.getQualifiedName(udirName.resolveBinding());

                if (!(udirName instanceof ICPPASTQualifiedName) && qname.length > 1) {
                    reportProblem(ProblemId.UDIR_UNQUALIFIED_PROBLEM_ID, decl);
                }
            }
        }
    }

    private void checkUsingBeforeInclude(IASTTranslationUnit ast) {
        IASTPreprocessorIncludeStatement[] includes = ast.getIncludeDirectives();
        if (includes.length > 0) {

            int lastIncludeOffset = includes[includes.length - 1].getFileLocation().getNodeOffset();

            for (IASTDeclaration decl : ast.getDeclarations()) {

                if (decl.getFileLocation().getNodeOffset() < lastIncludeOffset) {

                    if (decl instanceof ICPPASTUsingDirective) {
                        reportProblem(ProblemId.UDIR_BEFORE_INCLUDE_PROBLEM_ID, decl);

                    } else if (decl instanceof ICPPASTUsingDeclaration) {
                        reportProblem(ProblemId.UDEC_BEFORE_INCLUDE_PROBLEM_ID, decl);
                    }
                } else {
                    break;
                }
            }
        }
    }

    private void checkUsingInHeader(final IASTTranslationUnit ast) {

        ast.accept(new ASTVisitor() {

            {
                shouldVisitDeclarations = true;
            }

            @Override
            public int visit(IASTDeclaration decl) {
                if (isInGlobalScope(decl)) {
                    if (decl instanceof ICPPASTUsingDirective) {
                        reportProblem(ProblemId.UDIR_IN_HEADER_PROBLEM_ID, decl);
                    }
                    if (decl instanceof ICPPASTUsingDeclaration) {
                        if (isInGlobalScope(decl)) {
                            reportProblem(ProblemId.UDEC_IN_HEADER_PROBLEM_ID, decl);
                        }
                    }
                }
                return super.visit(decl);
            }

            private boolean isInGlobalScope(IASTDeclaration decl) {
                return decl.getParent() == ast;
            }
        });
    }

    private void checkTypedefs(final IASTTranslationUnit ast) {

        ast.accept(new ASTVisitor() {

            {
                shouldVisitDeclarations = true;
            }

            @Override
            public int visit(IASTDeclaration decl) {
                if (decl instanceof IASTSimpleDeclaration) {
                    IASTSimpleDeclaration sd = (IASTSimpleDeclaration) decl;
                    if (sd.getDeclSpecifier().getStorageClass() == IASTDeclSpecifier.sc_typedef) {
                        reportProblem(ProblemId.TYPEDEF_SHOULD_BE_ALIAS, decl);
                    }

                }
                return super.visit(decl);
            }
        });
    }

}
