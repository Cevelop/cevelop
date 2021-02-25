/******************************************************************************
 * Copyright (c) 2015 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Peter Sommerlad peter.sommerlad@hsr.ch
 ******************************************************************************/
package com.cevelop.namespactor.refactoring.td2a;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.ASTNodeFactoryFactory;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTNodeSelector;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTAliasDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.internal.core.model.ASTCache.ASTRunnable;
import org.eclipse.cdt.internal.ui.refactoring.Container;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.namespactor.astutil.DeclarationHelper;
import com.cevelop.namespactor.astutil.NSNodeHelper;
import com.cevelop.namespactor.astutil.NSSelectionHelper;
import com.cevelop.namespactor.refactoring.RefactoringBase;
import com.cevelop.namespactor.refactoring.rewrite.ASTRewriteStore;
import com.cevelop.namespactor.resources.Labels;


/**
 * @author peter.sommerlad@hsr.ch
 */
@SuppressWarnings("restriction")
public class TD2ARefactoring extends RefactoringBase {

    public static class FindEnclosingTypedefAndRegion implements ASTRunnable {

        private final Region          textSelection;
        private Region                selectedTypeDefRegion;
        private IASTSimpleDeclaration theTypedefNode;

        public IASTSimpleDeclaration getTheTypedefNode() {
            return theTypedefNode;
        }

        public FindEnclosingTypedefAndRegion(Region textSelection) {
            this.textSelection = textSelection;
        }

        public Region getSelectedTypeDefRegion() {
            return selectedTypeDefRegion;
        }

        @Override
        public IStatus runOnAST(ILanguage lang, IASTTranslationUnit astRoot) throws CoreException {
            IASTNodeSelector selector = astRoot.getNodeSelector(null);
            IASTNode node = selector.findEnclosingNode(textSelection.getOffset(), textSelection.getLength());
            if (node == null) {
                return Status.CANCEL_STATUS;
            }
            // find enclosing declaration and if it is a typedef, adjust region.
            if ((node = NSNodeHelper.nodeIsInTypedefSimpleDeclaration(node)) != null) {
                selectedTypeDefRegion = NSSelectionHelper.getNodeSpan(node);
                theTypedefNode = (IASTSimpleDeclaration) node;
            }
            return Status.OK_STATUS;
        }
    }

    private IASTSimpleDeclaration typedef2Replace;
    private IASTDeclSpecifier     typedefDeclSpecifier;

    public TD2ARefactoring(ICElement element, Optional<ITextSelection> selection) {
        super(element, selection);
        if (selection.map(ITextSelection::getOffset).orElse(-1) < 0) {
            System.err.println("TD2ARefactoring text selection invalid from marker resolution");
        }
    }

    public static IASTNode getDefinedTypename(final Region textSelection, IASTTranslationUnit tu) {

        FindEnclosingTypedefAndRegion runner = new FindEnclosingTypedefAndRegion(textSelection);
        try {
            runner.runOnAST(null, tu);
        } catch (CoreException e) {}
        if (runner.getTheTypedefNode() != null) {
            return runner.getTheTypedefNode();
        }
        // now search using a visitor, if shortcut above doesn't succeed.
        final Container<IASTNode> container = new Container<>();
        final Region selection = runner.getSelectedTypeDefRegion() != null ? runner.getSelectedTypeDefRegion() : textSelection;

        tu.accept(new ASTVisitor() {

            {
                shouldVisitNames = true;
            }

            @Override
            public int leave(IASTName name) {
                // find enclosing simple declaration and then check if selection is here
                // and find the typedef SimpleDeclaration if it actually is one
                IASTNode node = name;
                if (container.getObject() == null && NSSelectionHelper.isSelectionOnExpression(selection, name) && (node = NSNodeHelper
                        .nodeIsInTypedefSimpleDeclaration(node)) != null) {
                    container.setObject(node);
                    return ASTVisitor.PROCESS_ABORT; // done container is filled
                }
                return super.leave(name);
            }

            @Override
            public int visit(IASTName name) {
                return super.visit(name);
            }
        });

        return container.getObject();
    }

    protected static boolean nameIsParameter(IASTName name) {
        IASTNode parent = name.getParent();
        while (parent != null) {
            if (parent instanceof ICPPASTParameterDeclaration) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        SubMonitor sm = SubMonitor.convert(pm, 10);

        super.checkInitialConditions(sm.newChild(6));

        if (initStatus.hasFatalError()) {
            sm.done();
            return initStatus;
        }

        typedef2Replace = (IASTSimpleDeclaration) getDefinedTypename(selectedRegion, getAST(tu, pm));

        if (typedef2Replace == null) {
            initStatus.addFatalError(Labels.TD2A_NoNameSelected);
            //		} else if (selectedName.getParent() instanceof ICPPASTDeclarator) {
            //			initStatus.addFatalError(Labels.TD2A_DeclaratorNameSelected);
            //		} else if (selectedName.getParent() instanceof ICPPASTQualifiedName || selectedName instanceof ICPPASTQualifiedName) {
            //			initStatus.addFatalError(Labels.QUN_SelectedNameAlreadyQualified);
        } else {
            typedefDeclSpecifier = typedef2Replace.getDeclSpecifier();
            // look for IASTSimpleDeclaration parent that has a
            // ICPPASTSimpleDeclSpecifier with typedef storage class
            //IBinding selectedNameBinding = selectedName.resolveBinding();
            //			IASTNode node = selectedName.getParent();
            //			while(node != null && !(node instanceof CPPASTSimpleDeclaration) ){
            //				node = node.getParent();
            //			}
            //			if (node != null){
            //				declaration = (CPPASTSimpleDeclaration) node;
            //				declspecifier = declaration.getDeclSpecifier();
            if (typedefDeclSpecifier.getStorageClass() != IASTDeclSpecifier.sc_typedef) {
                initStatus.addFatalError(Labels.TD2A_NoTypedefSelected);
                sm.done();
                return initStatus;
            }
            //			}

        }

        sm.done();
        return initStatus;
    }

    private ICPPASTAliasDeclaration createAliasDeclaration(IASTDeclSpecifier declspecifier, IASTDeclarator declarator) {
        ICPPNodeFactory factory = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();
        IASTName name = DeclarationHelper.findNameOfDeclarator(declarator);
        if (name == null) {
            return null; // OOPS, no name found
        }
        IASTDeclarator newdeclarator = declarator.copy(CopyStyle.withLocations);
        // remove name selectedName from declarator
        DeclarationHelper.makeDeclaratorAbstract(newdeclarator, factory);
        newdeclarator = DeclarationHelper.optimizeDeclaratorNesting(newdeclarator);
        IASTDeclSpecifier newdeclspecifer = declspecifier.copy(CopyStyle.withLocations);
        newdeclspecifer.setStorageClass(IASTDeclSpecifier.sc_unspecified);
        ICPPASTTypeId aliasedType = factory.newTypeId(newdeclspecifer, newdeclarator);
        ICPPASTAliasDeclaration alias = factory.newAliasDeclaration(name.copy(), aliasedType);
        return alias;
    }

    @Override
    protected void collectModifications(ASTRewriteStore store) {
        IASTDeclarator[] declarators = typedef2Replace.getDeclarators();
        if (declarators.length > 1) {
            for (IASTDeclarator declarator : declarators) {
                if (declarator != null) {
                    ICPPASTAliasDeclaration alias = createAliasDeclaration(typedefDeclSpecifier, declarator);
                    // there is more than one, so do not replace, but
                    // insert and then remove
                    if (alias != null) {
                        store.addInsertChange(typedef2Replace.getParent(), alias, typedef2Replace);
                    }
                }
            }
            store.addRemoveChange(typedef2Replace);
        } else { // for single declarator case use replace change to keep modifications minimal
            IASTDeclarator declarator = declarators[0];
            if (declarator != null) {
                ICPPASTAliasDeclaration alias = createAliasDeclaration(typedefDeclSpecifier, declarator);
                // there might be more than one, so do not replace, but
                // insert and then remove
                if (alias != null) {
                    store.addReplaceChange(typedef2Replace, alias);
                }
            }
        }
        super.collectModifications(store);
    }

}
