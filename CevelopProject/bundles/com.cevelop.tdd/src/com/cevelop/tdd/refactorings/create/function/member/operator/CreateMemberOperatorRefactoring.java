package com.cevelop.tdd.refactorings.create.function.member.operator;

import org.eclipse.cdt.core.dom.ast.ASTNodeFactoryFactory;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTReferenceOperator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import com.cevelop.tdd.helpers.FunctionCreationHelper;
import com.cevelop.tdd.helpers.ParameterHelper;
import com.cevelop.tdd.helpers.TddHelper;
import com.cevelop.tdd.helpers.TypeHelper;
import com.cevelop.tdd.infos.MemberOperatorInfo;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.SelectionRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringContext;
import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;


public class CreateMemberOperatorRefactoring extends SelectionRefactoring<MemberOperatorInfo> {

    public CreateMemberOperatorRefactoring(final ICElement element, final MemberOperatorInfo info) {
        super(element, info);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return Messages.CreateMemberOperator_name;
    }

    @Override
    protected RefactoringDescriptor getRefactoringDescriptor() {
        String comment = selectedRegion != null ? Messages.CreateMemberOperator_descWithSection : Messages.CreateMemberOperator_descWoSection;
        return new CreateMemberOperatorRefactoringDescriptor(project.getProject().getName(), Messages.CreateMemberOperator_name, comment, info);
    }

    @Override
    protected void collectModifications(IProgressMonitor pm, ModificationCollector collector) throws CoreException, OperationCanceledException {
        if (!selection.isPresent()) return;
        IASTTranslationUnit localAST = refactoringContext.getAST(tu, pm);
        IASTName selectedNode = FunctionCreationHelper.getMostCloseSelectedNodeName(localAST, selection.get());
        if (selectedNode == null) return;
        ICPPASTCompositeTypeSpecifier hostType = null;
        if (info.hostTypeStart == -1 || info.hostTypeLength == -1) {
            hostType = getDefinitionScopeForName(localAST, selectedNode, refactoringContext);
        } else {
            IASTNode hostNode = localAST.getNodeSelector(null).findEnclosingNodeInExpansion(info.hostTypeStart, info.hostTypeLength);
            if (hostNode instanceof ICPPASTCompositeTypeSpecifier) {
                hostType = (ICPPASTCompositeTypeSpecifier) hostNode;
            }
        }
        // TODO(tstauber - Sep 19, 2018) Fix this shitty selection
        ICPPASTFunctionDefinition newFunction = getFunctionDefinition(localAST, selectedNode, info.operatorName, new TextSelection(selection.get()
                .getOffset(), 0));
        if (newFunction == null) return;
        if (hostType == null) {
            final IASTNode parent = selectedNode.getParent();
            IASTNode insertionPoint;
            if (parent instanceof ICPPASTQualifiedName) {
                insertionPoint = TddHelper.getNestedInsertionPoint(localAST, (ICPPASTQualifiedName) parent, refactoringContext);
            } else {
                insertionPoint = localAST;
            }
            TddHelper.writeDefinitionTo(collector, insertionPoint, newFunction);
        } else {
            TddHelper.writeDefinitionTo(collector, hostType, newFunction);
        }
    }

    private ICPPASTCompositeTypeSpecifier getDefinitionScopeForName(IASTTranslationUnit unit, IASTName name, CRefactoringContext context) {
        return TypeHelper.getTypeDefinitionOfVariable(unit, name, context);
    }

    private ICPPASTFunctionDefinition createFunctionDefinition(IASTTranslationUnit localunit, IASTNode selectedName, ISelection selection,
            ICPPASTFunctionDeclarator decl) {
        ICPPASTFunctionDefinition fdef = FunctionCreationHelper.createNewFunction(localunit, selection, decl);
        if (!FunctionCreationHelper.isVoid(fdef) && FunctionCreationHelper.isConstOperator(selectedName)) {
            decl.setConst(true);
        }
        if (FunctionCreationHelper.isPostfixOperator(selectedName)) {
            ParameterHelper.addEmptyIntParameter(decl);
        } else if (!decl.isConst() && !FunctionCreationHelper.isVoid(fdef)) {
            decl.addPointerOperator(new CPPASTReferenceOperator(false));
        }
        return fdef;
    }

    private ICPPASTFunctionDefinition getFunctionDefinition(IASTTranslationUnit localunit, IASTNode selectedName, String name, ISelection selection) {
        ICPPNodeFactory nodeFactory = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();
        ICPPASTFunctionDeclarator decl = nodeFactory.newFunctionDeclarator(nodeFactory.newName(("operator" + name)));
        IASTBinaryExpression binex = TddHelper.getAncestorOfType(selectedName, IASTBinaryExpression.class);
        if (binex != null) {
            IASTExpression op = binex.getOperand2();
            FunctionCreationHelper.addParameterToOperator(decl, op);
        }
        return createFunctionDefinition(localunit, selectedName, selection, decl);
    }
}
