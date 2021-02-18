/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.refactorings.visibility;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMember;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.ui.refactoring.NodeContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import com.cevelop.tdd.helpers.TddHelper;
import com.cevelop.tdd.helpers.TypeHelper;
import com.cevelop.tdd.infos.VisibilityInfo;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.SelectionRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;


public class VisibilityRefactoring extends SelectionRefactoring<VisibilityInfo> {

    public VisibilityRefactoring(final ICElement element, final VisibilityInfo info) {
        super(element, info);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return Messages.ChangeVisibility_name;
    }

    @Override
    protected RefactoringDescriptor getRefactoringDescriptor() {
        String comment = selectedRegion != null ? Messages.ChangeVisibility_descWithSection : Messages.ChangeVisibility_descWoSection;
        return new VisibilityRefactoringDescriptor(project.getProject().getName(), Messages.ChangeVisibility_name, comment, info);
    }

    @Override
    protected void collectModifications(IProgressMonitor pm, ModificationCollector collector) throws CoreException, OperationCanceledException {
        if (!selection.isPresent()) return;
        IASTTranslationUnit localunit = refactoringContext.getAST(tu, pm);
        IASTNode selectedNode = localunit.getNodeSelector(null).findEnclosingNode(selection.get().getOffset(), selection.get().getLength());
        if (!(selectedNode instanceof IASTName)) return;
        ICPPASTCompositeTypeSpecifier typeSpec = TypeHelper.getTypeOfMember(localunit, (IASTName) selectedNode, refactoringContext);
        MethodFindVisitor memberfinder = new MethodFindVisitor(info.memberName);
        typeSpec.accept(memberfinder);
        IASTNode function = memberfinder.getFoundNode();
        if (function == null) {
            TddHelper.showErrorOnStatusLine(Messages.ChangeVisibilityRefactoring_0);
            return;
        }

        ASTRewrite rewrite = collector.rewriterForTranslationUnit(typeSpec.getTranslationUnit());
        rewrite.remove(function, null);

        TddHelper.insertMember(function, typeSpec, rewrite);
    }

    class MethodFindVisitor extends ASTVisitor {

        private final NodeContainer container;
        private final String        nameToSearch;
        {
            shouldVisitNames = true;
        }

        public MethodFindVisitor(String nameToSearch) {
            this.container = new NodeContainer();
            this.nameToSearch = nameToSearch;
        }

        @Override
        public int visit(IASTName name) {
            IBinding binding = name.resolveBinding();
            if (binding instanceof ICPPMember) {
                String bindingname = binding.getName().replaceAll("\\(\\w*\\)", "");
                if (bindingname.equals(nameToSearch)) {
                    ICPPASTCompositeTypeSpecifier typeSpec = TddHelper.getAncestorOfType(name, ICPPASTCompositeTypeSpecifier.class);
                    if (typeSpec != null) {
                        IASTNode function = TddHelper.getAncestorOfType(name, ICPPASTFunctionDefinition.class);
                        if (function == null) {
                            function = TddHelper.getAncestorOfType(name, IASTSimpleDeclaration.class);
                        }
                        container.add(function);
                        return PROCESS_ABORT;
                    }
                }
            }
            return PROCESS_CONTINUE;
        }

        public IASTNode getFoundNode() {
            if (container.size() > 0) {
                return container.getNodesToWrite().get(0);
            }
            return null;
        }
    }
}
