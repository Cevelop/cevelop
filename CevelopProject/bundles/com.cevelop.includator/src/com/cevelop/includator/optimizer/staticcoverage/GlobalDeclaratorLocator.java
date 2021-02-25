/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.optimizer.staticcoverage;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;

import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.resources.IncludatorProject;


public class GlobalDeclaratorLocator extends ASTVisitor {

    private final IncludatorProject project;
    private ArrayList<IASTNode>     result;

    public GlobalDeclaratorLocator(IncludatorProject project) {
        super(true);
        this.project = project;
    }

    public Collection<IASTNode> getGlobalDeclarations() {
        result = new ArrayList<>();
        for (IncludatorFile curFile : project.getAffectedFiles()) {
            IASTTranslationUnit tu = curFile.getTranslationUnit();
            if (tu != null) {
                tu.accept(this);
            }
        }
        return result;
    }

    @Override
    public int visit(IASTDeclarator declarator) {
        if (declarator instanceof IASTFunctionDeclarator) {
            return super.visit(declarator);
        }
        IASTNode parent = declarator.getParent();
        if (parent instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) parent;
            IASTNode declarationParent = simpleDeclaration.getParent();
            if (simpleDeclaration.getDeclSpecifier() instanceof ICPPASTCompositeTypeSpecifier) {
                result.add(declarator);
            }

            if (isGlobal(declarationParent)) {
                result.add(simpleDeclaration);
            } else if (isStatic(simpleDeclaration)) {
                result.add(simpleDeclaration);
            }
        }
        return super.visit(declarator);
    }

    private boolean isGlobal(IASTNode declarationParent) {
        return (declarationParent instanceof IASTTranslationUnit) || (declarationParent instanceof ICPPASTNamespaceDefinition);
    }

    private boolean isStatic(IASTSimpleDeclaration simpleDeclaration) {
        return simpleDeclaration.getDeclSpecifier().getStorageClass() == IASTDeclSpecifier.sc_static;
    }

    @Override
    public int visit(ICPPASTBaseSpecifier baseSpecifier) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(ICPPASTTemplateParameter templateParameter) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(IASTName name) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(IASTInitializer initializer) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(IASTParameterDeclaration parameterDeclaration) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(IASTArrayModifier arrayModifier) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(IASTPointerOperator ptrOperator) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(IASTExpression expression) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(IASTStatement statement) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(IASTTypeId typeId) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(IASTEnumerator enumerator) {
        return PROCESS_SKIP;
    }

    @Override
    public int visit(IASTProblem problem) {
        return PROCESS_SKIP;
    }
}
