/******************************************************************************
 * Copyright (c) 2012-2013 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ueli Kunz <kunz@ideadapt.net>, Jules Weder <julesweder@gmail.com> - initial API and implementation
 * Peter Sommerlad - introduction of hybrid approach
 ******************************************************************************/
package com.cevelop.namespactor.refactoring.iudec;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.cdt.codan.core.cxx.CxxAstUtils;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPUsingDeclaration;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPUsingDeclaration;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.namespactor.Activator;
import com.cevelop.namespactor.astutil.ASTNodeFactory;
import com.cevelop.namespactor.astutil.NSNameHelper;
import com.cevelop.namespactor.astutil.NSNodeHelper;
import com.cevelop.namespactor.astutil.NSSelectionHelper;
import com.cevelop.namespactor.refactoring.NodeDefinitionNotInWorkspaceException;
import com.cevelop.namespactor.refactoring.TemplateIdFactory;
import com.cevelop.namespactor.refactoring.iu.IncludeDependencyAnalyser;
import com.cevelop.namespactor.refactoring.iu.InlineRefactoringBase;
import com.cevelop.namespactor.refactoring.iu.InlineRefactoringContext;
import com.cevelop.namespactor.refactoring.iu.NamespaceInlineContext;
import com.cevelop.namespactor.refactoring.rewrite.ASTRewriteStore;
import com.cevelop.namespactor.resources.Labels;


/**
 * @author kunz@ideadapt.net
 */
@SuppressWarnings("restriction")
public class IUDecRefactoring extends InlineRefactoringBase {

    private List<IASTName> targets = null;

    public IUDecRefactoring(ICElement element, Optional<ITextSelection> selection) {
        super(element, selection);
    }

    @Override
    protected void collectModifications(ASTRewriteStore store) {
        store.addRemoveChange(ctx.selectedUsing);
        super.collectModifications(store);
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        SubMonitor sm = SubMonitor.convert(pm, 10);

        super.checkInitialConditions(sm.newChild(6));

        if (initStatus.hasFatalError()) {
            sm.done();
            return initStatus;
        }

        ICPPASTUsingDeclaration selectedUDEC = NSSelectionHelper.getSelectedUsingDeclaration(selectedRegion, getAST(tu, pm));

        if (selectedUDEC == null) {
            initStatus.addFatalError(Labels.IUDEC_NoUDECSelected);
        } else {
            ctx.selectedUsing = selectedUDEC;
            ctx.selectedName = selectedUDEC.getName();
            ctx.enclosingCompound = NSNodeHelper.findCompoundStatementInAncestors(selectedUDEC);
            //			ctx.templateIdsToIgnore = new HashSet<ICPPASTTemplateId>();

            includeDepAnalyser = new IncludeDependencyAnalyser(getIndex());
            targets = new ArrayList<>();

            IBinding decl = ctx.selectedName.getLastName().resolveBinding();
            IBinding targetDeclarationBinding = ((ICPPUsingDeclaration) decl).getDelegates()[0]; // OK?
            targetDeclarationBinding = getIndex().adaptBinding(targetDeclarationBinding);
            decl = getIndex().adaptBinding(decl);
            try {
                initContext(sm, ctx.selectedName);
            } catch (NodeDefinitionNotInWorkspaceException e) {
                Activator.log("Exception while checking initial conditions.", e);
            }

            findTargetsInScope(ctx.enclosingCompound, targetDeclarationBinding);
            try {
                processTargets(sm);
            } catch (NodeDefinitionNotInWorkspaceException e) {
                Activator.log("Exception while getting include dependent paths of " + tu.getElementName(), e);
            }
        }
        sm.done();
        return initStatus;
    }

    private void processTargets(SubMonitor sm) throws CoreException, NodeDefinitionNotInWorkspaceException {

        for (IASTName name : targets) {
            nodesToReplace.put(name, ctx.selectedName);
        }
    }

    private void initContext(IProgressMonitor pm, IASTName refNode) throws CoreException, NodeDefinitionNotInWorkspaceException {

        IBinding selectedNameBinding = ctx.selectedName.getLastName().resolveBinding();
        ICPPUsingDeclaration selectedDeclaration = (ICPPUsingDeclaration) selectedNameBinding;

        IIndexName[] declNames = getIndex().findDeclarations(selectedNameBinding);
        IASTName declNode = null;
        ICPPASTNamespaceDefinition nsDefNode = null;
        ICPPASTCompositeTypeSpecifier classDefNode = null; // sorry, no common baseclass, getName() is required for nsDefNode
        if (declNames.length > 0) {
            declNode = getNodeOf(declNames[0], pm);
            nsDefNode = NSNodeHelper.findAncestorOf(declNode, ICPPASTNamespaceDefinition.class);
            classDefNode = NSNodeHelper.findAncestorOf(declNode, ICPPASTCompositeTypeSpecifier.class);
        }
        ICPPASTQualifiedName newQName = ASTNodeFactory.getDefault().newQualifiedName(null);

        if (ctx.selectedName instanceof ICPPASTQualifiedName) {
            for (ICPPASTNameSpecifier n : ((ICPPASTQualifiedName) ctx.selectedName).getAllSegments()) {
                NSNameHelper.addNameOrNameSpecifier(newQName, n);
            }
        } else {
            newQName.addName(ctx.selectedName.getLastName().copy());
        }

        ctx.enclosingNSContext = new NamespaceInlineContext();
        ctx.enclosingNSContext.namespaceDefNode = nsDefNode;
        ctx.enclosingNSContext.classDefNode = classDefNode;
        ctx.enclosingNSContext.usingName = NSNameHelper.copyQualifers(newQName);
        ctx.enclosingNSContext.namespaceDefBinding = selectedDeclaration.getDelegates()[0].getOwner();

    }

    @Override
    protected TemplateIdFactory getTemplateIdFactory(ICPPASTTemplateId templateId, InlineRefactoringContext ctx) {
        return new IUDecTemplateIdFactory(templateId, ctx);
    }

    private void findTargetsInScope(IASTNode enclosingCompound, final IBinding targetDeclarationBinding) throws OperationCanceledException,
            CoreException {

        final IIndex indexer = this.getIndex();
        final IASTName theName = ctx.selectedName;
        final CPPUsingDeclaration theNameBinding = (CPPUsingDeclaration) theName.resolveBinding();
        ASTVisitor v = new ASTVisitor() {

            private boolean metDeclNode = false;

            {
                shouldVisitNames = true;
            }

            @Override
            public int visit(IASTName name) {
                if (name.equals(theName)) {
                    metDeclNode = true;
                }
                if (!metDeclNode || !isCandidate(name)) {
                    return super.visit(name);
                }

                IBinding candidateBinding = name.resolveBinding(); // liefert bei template IDs nicht das richtige!
                IBinding[] delegates = theNameBinding.getDelegates();
                for (IBinding delegate : delegates) {
                    if (delegate.equals(candidateBinding)) {
                        targets.add(name);
                    } else if (candidateBinding instanceof ICPPSpecialization) {
                        ICPPSpecialization specialization = (ICPPSpecialization) candidateBinding;
                        IBinding newcandidate = specialization.getSpecializedBinding();
                        if (newcandidate.equals(delegate)) {
                            targets.add(name);
                        }
                    }
                }
                return super.visit(name);
            }

            private boolean isCandidate(IASTName name) {
                if (name != null && name.getTranslationUnit() != null && name.getFileLocation() != null && CxxAstUtils.isInMacro(name)) {
                    return false;
                }
                if (name.toString().equals("")) {
                    return false;
                }
                if (name instanceof ICPPASTQualifiedName) {
                    return false;
                }
                if (name instanceof ICPPASTTemplateId) {
                    return false;
                }
                if (!isFirstSpecifier(name)) {
                    return false;
                }
                IASTFileLocation pos = name.getFileLocation();
                IASTFileLocation declpos = ctx.selectedUsing.getFileLocation();
                if (pos.getNodeOffset() <= declpos.getNodeOffset() + declpos.getNodeLength()) {
                    return false;
                }
                if (ctx.enclosingCompound != null) {
                    if (!NSNodeHelper.isNodeEnclosedBy(ctx.enclosingCompound, name)) {
                        return false;
                    }
                }
                if (ctx.enclosingNSContext != null && ((ctx.enclosingNSContext.namespaceDefNode != null && NSNodeHelper
                        .isNodeEnclosedByScopeDefinedBy(name, ctx.enclosingNSContext.namespaceDefNode)) ||
                                                       (ctx.enclosingNSContext.classDefNode != null && NSNodeHelper.isNodeEnclosedByScopeDefinedBy(
                                                               name, ctx.enclosingNSContext.classDefNode)))) {
                    return false;
                }
                IBinding refBinding = name.resolveBinding();
                IIndexBinding adaptedRefBinding = indexer.adaptBinding(refBinding);

                if (targetDeclarationBinding.equals(adaptedRefBinding)) {
                    return true;
                }
                if (refBinding instanceof ICPPSpecialization) {
                    refBinding = ((ICPPSpecialization) refBinding).getSpecializedBinding();
                    adaptedRefBinding = indexer.adaptBinding(refBinding);
                } // check on original using declaration dependency is missing.
                return targetDeclarationBinding.equals(adaptedRefBinding);
            }

        };

        boolean isTUScope = enclosingCompound == null;
        if (isTUScope) {
            visitIncludeDependentTUs(v);
            enclosingCompound = getAST(tu, npm);
        }
        enclosingCompound.accept(v);
    }

    private void visitIncludeDependentTUs(ASTVisitor v) throws CoreException {
        IASTNode enclosingCompound;
        List<IPath> paths = includeDepAnalyser.getIncludeDependentPathsOf(tu);
        for (IPath filePath : paths) {
            IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(new File(filePath.toOSString()).toURI());
            if (files.length < 1) {
                continue;
            }
            ITranslationUnit tu = CoreModelUtil.findTranslationUnit(files[0]);
            enclosingCompound = getAST(tu, npm);
            enclosingCompound.accept(v);
        }
    }

}
