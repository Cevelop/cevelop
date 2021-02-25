package com.cevelop.tdd.refactorings.create.function.member;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.SelectionRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringContext;
import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;

import com.cevelop.tdd.helpers.FunctionCreationHelper;
import com.cevelop.tdd.helpers.ParameterHelper;
import com.cevelop.tdd.helpers.TddHelper;
import com.cevelop.tdd.helpers.TypeHelper;
import com.cevelop.tdd.infos.MemberFunctionInfo;


public class CreateMemberFunctionRefactoring extends SelectionRefactoring<MemberFunctionInfo> {

    public CreateMemberFunctionRefactoring(final ICElement element, final MemberFunctionInfo info) {
        super(element, info);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return Messages.CreateMemberFunction_name;
    }

    @Override
    protected RefactoringDescriptor getRefactoringDescriptor() {
        String comment = selectedRegion != null ? Messages.CreateMemberFunction_descWithSection : Messages.CreateMemberFunction_descWoSection;
        return new CreateMemberFunctionRefactoringDescriptor(project.getProject().getName(), Messages.CreateMemberFunction_name, comment, info);
    }

    @Override
    protected void collectModifications(IProgressMonitor pm, ModificationCollector collector) throws CoreException, OperationCanceledException {
        if (!selection.isPresent()) {
            return;
        }
        IASTTranslationUnit localunit = refactoringContext.getAST(tu, pm);
        IASTName selectedNode = FunctionCreationHelper.getMostCloseSelectedNodeName(localunit, selection.get());
        if (selectedNode == null) {
            return;
        }
        ICPPASTCompositeTypeSpecifier type = getDefinitionScopeForName(localunit, selectedNode, refactoringContext);
        if (type == null) {
            IASTNode node = localunit.getNodeSelector(null).findEnclosingNodeInExpansion(selection.get().getOffset(), selection.get().getLength());
            if (node instanceof ICPPASTCompositeTypeSpecifier) {
                type = (ICPPASTCompositeTypeSpecifier) node;
            }
        }
        ICPPASTFunctionDefinition newFunction = getFunctionDefinition(localunit, selectedNode, info.name, selection.get());
        if (newFunction == null) {
            return;
        }
        if (type == null) {
            final IASTNode parent = selectedNode.getParent();
            IASTNode insertionPoint;
            if (parent instanceof ICPPASTQualifiedName) {
                insertionPoint = TddHelper.getNestedInsertionPoint(localunit, (ICPPASTQualifiedName) parent, refactoringContext);
            } else {
                insertionPoint = localunit;
            }
            TddHelper.writeDefinitionTo(collector, insertionPoint, newFunction);
        } else {
            TddHelper.writeDefinitionTo(collector, type, newFunction);
        }
    }

    private ICPPASTFunctionDefinition getFunctionDefinition(IASTTranslationUnit localunit, IASTNode selectedName, String name, ISelection selection) {
        ICPPASTFunctionDeclarator dec = new CPPASTFunctionDeclarator(new CPPASTName(name.toCharArray()));
        IASTFunctionCallExpression caller = TddHelper.getAncestorOfType(selectedName, IASTFunctionCallExpression.class);
        if (caller != null) {
            ParameterHelper.addTo(caller, dec);
        }
        ICPPASTFunctionDefinition newFunctionDefinition = FunctionCreationHelper.createNewFunction(localunit, selection, dec);
        if (!FunctionCreationHelper.isVoid(newFunctionDefinition)) {
            dec.setConst(true);
        }
        if (info.mustBeStatic) {
            newFunctionDefinition.getDeclSpecifier().setStorageClass(IASTDeclSpecifier.sc_static);
        }
        return newFunctionDefinition;
    }

    private ICPPASTCompositeTypeSpecifier getDefinitionScopeForName(IASTTranslationUnit unit, IASTName name, CRefactoringContext context) {
        return TypeHelper.getTypeDefinitionOfMember(unit, name, context);
    }
}
