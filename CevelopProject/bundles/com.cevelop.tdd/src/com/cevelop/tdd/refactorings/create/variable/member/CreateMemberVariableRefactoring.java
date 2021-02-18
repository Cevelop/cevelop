/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.refactorings.create.variable.member;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerList;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTArrayDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTArrayModifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTLiteralExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamedTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import com.cevelop.tdd.helpers.FunctionCreationHelper;
import com.cevelop.tdd.helpers.TddHelper;
import com.cevelop.tdd.helpers.TypeHelper;
import com.cevelop.tdd.infos.MemberVariableInfo;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.SelectionRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;


public class CreateMemberVariableRefactoring extends SelectionRefactoring<MemberVariableInfo> {

    public CreateMemberVariableRefactoring(final ICElement element, final MemberVariableInfo info) {
        super(element, info);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return Messages.CreateMemberVariable_name;
    }

    @Override
    protected RefactoringDescriptor getRefactoringDescriptor() {
        String comment = selectedRegion != null ? Messages.CreateMemberVariable_descWithSection : Messages.CreateMemberVariable_descWoSection;
        return new CreateMemberVariableRefactoringDescriptor(project.getProject().getName(), Messages.CreateMemberVariable_name, comment, info);
    }

    private boolean               isArray = false;
    private IASTInitializerClause initClause;

    @Override
    protected void collectModifications(IProgressMonitor pm, ModificationCollector collector) throws CoreException, OperationCanceledException {
        if (!selection.isPresent()) return;
        IASTTranslationUnit localunit = refactoringContext.getAST(tu, pm);
        IASTName selectedNode = FunctionCreationHelper.getMostCloseSelectedNodeName(localunit, selection.get());
        if (selectedNode == null) return;
        IASTNode memberOwner = TypeHelper.getMemberOwner(localunit, selectedNode, refactoringContext);
        IASTDeclaration newMember = getMemberVariableDeclaration(selectedNode, memberOwner);
        boolean isPrivate = isPartOf(selectedNode, memberOwner) && memberOwner instanceof ICPPASTCompositeTypeSpecifier;
        if (isPrivate) {
            TddHelper.writePrivateDefinitionTo(collector, (ICPPASTCompositeTypeSpecifier) memberOwner, newMember);
        } else {
            TddHelper.writeDefinitionTo(collector, memberOwner, newMember);
        }
        //      setLinkedModeInformation(localunit, memberOwner, newMember);
    }

    private IASTDeclaration getMemberVariableDeclaration(IASTName variableName, IASTNode parent) {
        IASTDeclSpecifier declspec = getDeclSpec(variableName);
        IASTSimpleDeclaration newDeclaration = new CPPASTSimpleDeclaration(declspec);
        ICPPASTDeclarator newDeclarator;
        if (isArray) {
            assert (initClause instanceof IASTInitializerList);
            char[] size = Integer.toString(((IASTInitializerList) initClause).getSize()).toCharArray();
            IASTExpression sizeExpression = new CPPASTLiteralExpression(IASTLiteralExpression.lk_integer_constant, size);
            ICPPASTArrayDeclarator array = new CPPASTArrayDeclarator(variableName.copy());
            array.addArrayModifier(new CPPASTArrayModifier(sizeExpression));
            newDeclarator = array;
        } else {
            newDeclarator = new CPPASTDeclarator(variableName.copy());
        }
        addPointerOperators(newDeclarator, variableName);
        newDeclaration.addDeclarator(newDeclarator);
        newDeclaration.setParent(parent);
        return newDeclaration;
    }

    private void addPointerOperators(ICPPASTDeclarator declarator, IASTName variableName) {
        IASTNode parent = variableName.getParent();
        if (parent instanceof ICPPASTFieldReference) {
            IASTBinaryExpression binExpr = TddHelper.getAncestorOfType(variableName, IASTBinaryExpression.class);
            if (binExpr == null) {
                return;
            }
            IASTExpression expr;
            IType type;
            boolean isString = false;
            IASTInitializerClause initializerClause = binExpr.getInitOperand2();
            IASTNode node = initializerClause;
            if (initializerClause != null) {
                isString = TypeHelper.isString(initializerClause);
                type = TypeHelper.getTypeOf(initializerClause);
            } else {
                expr = binExpr.getOperand2();
                node = expr;
                type = expr.getExpressionType();
            }
            if (isPartOf(variableName, node)) {
                type = binExpr.getOperand1().getExpressionType();
            }
            List<IASTPointerOperator> pointerOperators = TypeHelper.windDownAndCollectPointerOperators(type);
            if (isString && !pointerOperators.isEmpty()) {
                pointerOperators.remove(pointerOperators.size() - 1);
            }
            for (IASTPointerOperator ptrOper : pointerOperators) {
                declarator.addPointerOperator(ptrOper);
            }
        }
    }

    private IASTDeclSpecifier getDeclSpec(IASTName varName) {
        IASTNode parent = varName.getParent();
        if (parent instanceof ICPPASTFieldReference) {
            ICPPASTFieldReference ref = (ICPPASTFieldReference) parent;
            return createDeclSpecForFieldRef(varName, ref);
        } else if (parent instanceof ICPPASTConstructorChainInitializer) {
            ICPPASTConstructorChainInitializer chainInitializer = (ICPPASTConstructorChainInitializer) parent;
            IASTInitializerClause firstClause = getInitializerClause(chainInitializer);
            if (firstClause != null) {
                return getDeclSpecOfType(firstClause);
            }
        } else if (parent instanceof ICPPASTQualifiedName) {
            return createDeclSpecForQualifiedName(varName);
        }
        // any other cases? e.g. type->member or (*type).member, etc...
        return createDefaultDeclSpec();
    }

    private IASTDeclSpecifier createDeclSpecForQualifiedName(IASTName varName) {
        IASTDeclSpecifier declSpec;
        IASTBinaryExpression ascendingBinEx = TddHelper.getAncestorOfType(varName, IASTBinaryExpression.class);
        if (ascendingBinEx != null) {
            declSpec = createDeclSpecForBinaryExpression(varName, ascendingBinEx);
        } else {
            declSpec = createDefaultDeclSpec();
        }
        declSpec.setStorageClass(IASTDeclSpecifier.sc_static);
        return declSpec;
    }

    private IASTDeclSpecifier createDeclSpecForFieldRef(IASTName varName, ICPPASTFieldReference ref) {
        IASTBinaryExpression ascendingBinEx = TddHelper.getAncestorOfType(varName, IASTBinaryExpression.class);
        if (ascendingBinEx != null) {
            return createDeclSpecForBinaryExpression(ref, ascendingBinEx);
        }
        if (!isThisKeyword(ref)) {
            IASTSimpleDeclaration decl = TddHelper.getAncestorOfType(varName, IASTSimpleDeclaration.class);
            if (decl != null) {
                return decl.getDeclSpecifier().copy();
            }
        }
        return createDefaultDeclSpec();
    }

    private IASTDeclSpecifier createDeclSpecForBinaryExpression(IASTNode selectedNode, IASTBinaryExpression ascendingBinEx) {
        if (isPartOf(selectedNode, ascendingBinEx.getOperand1())) {
            IASTInitializerClause rSide = ascendingBinEx.getInitOperand2();
            return getDeclSpecOfType(rSide);
        } else {
            IASTExpression lSide = ascendingBinEx.getOperand1();
            return getDeclSpecOfType(lSide);
        }
    }

    private boolean isPartOf(IASTNode potentialSubpart, IASTNode nodeForSubtree) {
        IASTNode nodeToCheck = potentialSubpart;
        while (nodeToCheck != null) {
            if (nodeToCheck == nodeForSubtree) {
                return true;
            }
            nodeToCheck = nodeToCheck.getParent();
        }
        return false;
    }

    private boolean isThisKeyword(ICPPASTFieldReference fieldRef) {
        ICPPASTExpression owner = fieldRef.getFieldOwner();
        return owner instanceof ICPPASTLiteralExpression && ((ICPPASTLiteralExpression) owner).getKind() == ICPPASTLiteralExpression.lk_this;
    }

    private IASTInitializerClause getInitializerClause(ICPPASTConstructorChainInitializer chain) {
        IASTInitializerClause[] clauses = null;
        if (chain.getInitializer() instanceof ICPPASTConstructorInitializer) {
            ICPPASTConstructorInitializer init = (ICPPASTConstructorInitializer) chain.getInitializer();
            clauses = init.getArguments();
        } else if (chain.getInitializer() instanceof ICPPASTInitializerList) {
            ICPPASTInitializerList initList = (ICPPASTInitializerList) chain.getInitializer();
            clauses = initList.getClauses();
        }
        // Constructors with multiple arguments are not handled properly. The type of the member will be void.
        if (clauses.length == 1) {
            return clauses[0];
        }
        return null;
    }

    private IASTDeclSpecifier getDeclSpecOfType(IASTInitializerClause clause) {
        if (TypeHelper.isString(clause)) {
            return createStringDeclSpec();
        }
        if (clause instanceof IASTInitializerList) {
            isArray = true;
            initClause = clause;
        }
        IType type = TypeHelper.getTypeOf(clause);
        type = TypeHelper.windDownToRealType(type, true, isArray);
        return TypeHelper.getDeclarationSpecifierOfType(type);
    }

    private CPPASTNamedTypeSpecifier createStringDeclSpec() {
        return new CPPASTNamedTypeSpecifier(new CPPASTName("std::string".toCharArray()));
    }

    public static IASTDeclSpecifier createDefaultDeclSpec() {
        IASTSimpleDeclSpecifier simple = new CPPASTSimpleDeclSpecifier();
        simple.setType(Kind.eInt);
        return simple;
    }
}
