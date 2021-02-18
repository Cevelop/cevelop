/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.helpers;

import java.util.HashMap;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTStandardFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNamespace;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTBaseDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPNodeFactory;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;


public class FunctionCreationHelper {

    public static IASTName getMostCloseSelectedNodeName(IASTTranslationUnit localunit, ITextSelection selection) {
        IASTNode selectedNode = localunit.getNodeSelector(null).findEnclosingNodeInExpansion(selection.getOffset(), selection.getLength());
        if (selectedNode instanceof CPPASTName) {
            return (IASTName) selectedNode;
        }
        IASTName nameAround = TddHelper.getAncestorOfType(selectedNode, CPPASTName.class);
        if (nameAround == null) {
            nameAround = TddHelper.getChildofType(selectedNode, CPPASTName.class);
            if (nameAround != null) {
                IBinding nameBinding = nameAround.resolveBinding();
                if (nameBinding instanceof ICPPNamespace || nameBinding instanceof IType) {
                    IASTIdExpression idex = new LastIDExpressionFinder().getLastIDExpression(selectedNode);
                    if (idex != null) nameAround = idex.getName();
                }
            }
        }
        return nameAround;
    }

    public static void addParameterToOperator(IASTStandardFunctionDeclarator decl, IASTExpression op) {
        HashMap<String, Boolean> used = new HashMap<>();
        if (op instanceof IASTIdExpression) {
            IASTIdExpression op_expr = (IASTIdExpression) op;
            decl.addParameterDeclaration(ParameterHelper.createParamDeclFrom(op_expr, used));
        } else if (op instanceof IASTLiteralExpression) {
            IASTLiteralExpression litexpr = (IASTLiteralExpression) op;
            decl.addParameterDeclaration(ParameterHelper.createParamDeclFrom(litexpr, used));
        }
    }

    public static boolean isVoid(ICPPASTFunctionDefinition fdef) {
        IASTDeclSpecifier simpledecspec = fdef.getDeclSpecifier();
        if (simpledecspec instanceof ICPPASTSimpleDeclSpecifier) {
            return ((ICPPASTSimpleDeclSpecifier) simpledecspec).getType() == IASTSimpleDeclSpecifier.t_void;
        }
        return false;
    }

    public static boolean isConstOperator(IASTNode selectedNode) {
        IASTBinaryExpression binex = TddHelper.getAncestorOfType(selectedNode, IASTBinaryExpression.class);

        int optype;
        if (binex != null) {
            optype = binex.getOperator();
        } else {
            IASTUnaryExpression uexpr = TddHelper.getAncestorOfType(selectedNode, IASTUnaryExpression.class);
            optype = uexpr.getOperator();
        }
        switch (optype) {
        case IASTUnaryExpression.op_minus:
        case IASTUnaryExpression.op_postFixIncr:
        case IASTUnaryExpression.op_prefixIncr:
            return false;
        case IASTBinaryExpression.op_equals:
            return true;
        }
        return false;
    }

    public static boolean isPostfixOperator(IASTNode selectedNode) {
        IASTUnaryExpression uexpr = TddHelper.getAncestorOfType(selectedNode, IASTUnaryExpression.class);
        return uexpr != null && (uexpr.getOperator() == IASTUnaryExpression.op_postFixDecr || uexpr
                .getOperator() == IASTUnaryExpression.op_postFixIncr);
    }

    public static ICPPASTFunctionDefinition createNewFunction(IASTNode targetNode, ISelection selection, ICPPASTFunctionDeclarator dec) {
        CPPASTBaseDeclSpecifier spec = FunctionCreationHelper.calculateReturnType(targetNode, selection);
        IASTCompoundStatement body = CPPNodeFactory.getDefault().newCompoundStatement();
        ICPPASTFunctionDefinition newFunctionDefinition = CPPNodeFactory.getDefault().newFunctionDefinition(spec, dec, body);
        IASTReturnStatement returnstmt = TddHelper.getDefaultReturnValue(spec);
        if (returnstmt != null) {
            body.addStatement(returnstmt);
            returnstmt.setParent(newFunctionDefinition);
        }
        return newFunctionDefinition;
    }

    public static CPPASTBaseDeclSpecifier calculateReturnType(IASTNode targetNode, ISelection selection) {
        CPPASTBaseDeclSpecifier type = TypeHelper.findTypeInAst(targetNode, selection);
        if (type == null) {
            return TypeHelper.getDefaultReturnType();
        }
        return type;
    }

    public static boolean hasReturnStatement(ICPPASTFunctionDefinition function) {
        return function.getBody().getChildren().length > 0;
    }

    public static boolean setFileChanged(IASTTranslationUnit localunit, IASTNode owningType) {
        if (owningType != null && owningType.getTranslationUnit().equals(localunit)) {
            return true;
        }
        return false;
    }
}
