/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.helpers.visitors;

import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTIfStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBasicType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamedTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPBasicType;
import org.eclipse.cdt.internal.ui.refactoring.NodeContainer;
import org.eclipse.cdt.internal.ui.refactoring.utils.SelectionHelper;
import org.eclipse.jface.viewers.ISelection;


public class PossibleReturnTypeFindVisitor extends ASTVisitor {

    private final ISelection    selection;
    private final NodeContainer container;

    {
        shouldVisitStatements = true;
        shouldVisitExpressions = true;
    }

    public PossibleReturnTypeFindVisitor(ISelection selection, NodeContainer c) {
        this.selection = selection;
        this.container = c;
    }

    @Override
    public int visit(IASTExpression expression) {
        if (SelectionHelper.doesNodeOverlapWithRegion(expression, SelectionHelper.getRegion(selection))) {
            if (expression instanceof ICPPASTUnaryExpression) {
                ICPPASTUnaryExpression uexpr = (ICPPASTUnaryExpression) expression;
                IASTExpression operand = uexpr.getOperand();
                if (operand instanceof ICPPASTLiteralExpression) {
                    IType type = operand.getExpressionType();
                    return handleType(type);
                } else if (operand instanceof ICPPASTFunctionCallExpression) {
                    return handleType(new CPPBasicType(Kind.eBoolean, 0));
                } else if (operand instanceof ICPPASTBinaryExpression) {
                    ICPPASTBinaryExpression binex = (ICPPASTBinaryExpression) operand;
                    if (binex.getOperand2() instanceof IASTFunctionCallExpression) {
                        operand = binex.getOperand1();
                    }
                    return handleType(operand.getExpressionType());
                }
                if (operand instanceof ICPPASTUnaryExpression) {
                    ICPPASTUnaryExpression inneruexpr = (ICPPASTUnaryExpression) uexpr.getOperand();
                    IType type = inneruexpr.getOperand().getExpressionType();
                    return handleType(type);
                }
            } else if (expression instanceof ICPPASTBinaryExpression) {
                ICPPASTBinaryExpression binex = (ICPPASTBinaryExpression) expression;
                boolean problemNodeInOperand1 = SelectionHelper.doesNodeOverlapWithRegion(binex.getOperand1(), SelectionHelper.getRegion(selection));
                IASTExpression operand = binex.getOperand1();
                if (problemNodeInOperand1) {
                    operand = binex.getOperand2();
                }
                if (operand != null) handleType(operand.getExpressionType());
            }
        }
        return PROCESS_CONTINUE;
    }

    @Override
    public int visit(IASTStatement stmt) {
        if (SelectionHelper.doesNodeOverlapWithRegion(stmt, SelectionHelper.getRegion(selection))) {
            if (stmt instanceof IASTDeclarationStatement) {
                IASTDeclarationStatement declstmt = (IASTDeclarationStatement) stmt;
                container.add(((CPPASTSimpleDeclaration) declstmt.getDeclaration()).getDeclSpecifier());
                return PROCESS_ABORT;
            }
            if (stmt instanceof IASTExpressionStatement) {
                IASTExpressionStatement expr = (IASTExpressionStatement) stmt;
                IASTExpression ex = expr.getExpression();
                if (ex instanceof ICPPASTBinaryExpression) {
                    ICPPASTBinaryExpression binex = (ICPPASTBinaryExpression) ex;
                    if (binex.getOperand1() instanceof ICPPASTBinaryExpression) {
                        binex = (ICPPASTBinaryExpression) binex.getOperand1();
                    }
                    binex.getExpressionType();
                    // give a typedef a change to insert it's name
                    if (binex.getOperand1() instanceof CPPASTIdExpression) {
                        CPPASTIdExpression idex = (CPPASTIdExpression) binex.getOperand1();
                        IASTName xname = idex.getName();
                        IBinding var = xname.resolveBinding();
                        if (var instanceof IVariable) {
                            ((IVariable) var).getType();
                        }
                    }
                    return handleType(ex.getExpressionType());
                } else if (ex instanceof ICPPASTUnaryExpression) {
                    ICPPASTUnaryExpression unex = (ICPPASTUnaryExpression) ex;
                    IType type = unex.getExpressionType();
                    if (unex.getOperand().getExpressionType().equals(ex.getExpressionType())) {
                        // give a typedef a change to insert it's name
                        if (unex.getOperand() instanceof CPPASTIdExpression) {
                            CPPASTIdExpression idex = (CPPASTIdExpression) unex.getOperand();
                            IASTName xname = idex.getName();
                            IBinding var = xname.resolveBinding();
                            if (var instanceof IVariable) {
                                type = ((IVariable) var).getType();
                                if (type instanceof ITypedef) {
                                    return handleType(type);
                                }
                            }
                        }
                    }
                    return handleType(ex.getExpressionType());
                }

            }
            if (stmt instanceof ICPPASTIfStatement) {
                handleType(new CPPBasicType(IBasicType.Kind.eBoolean, 0));
            }
        }
        return PROCESS_CONTINUE;
    }

    private int handleType(IType type) {
        if (type instanceof ICPPBasicType) {
            ICPPBasicType basictype = (ICPPBasicType) type;
            CPPASTSimpleDeclSpecifier simpletype = new CPPASTSimpleDeclSpecifier();
            Kind kind = basictype.getKind();
            if (kind.equals(Kind.eVoid)) {
                return PROCESS_CONTINUE;
            }
            simpletype.setType(basictype.getKind());
            if (container.size() > 0) {
                container.getNodesToWrite().remove(0);
            }
            container.add(simpletype);
            return PROCESS_ABORT;
        }
        if (type instanceof ICPPClassType) {
            ICPPClassType ctype = ((ICPPClassType) type);
            CPPASTNamedTypeSpecifier namedtype = new CPPASTNamedTypeSpecifier(new CPPASTName(ctype.getName().toCharArray()));
            if (container.size() > 0) {
                container.getNodesToWrite().remove(0);
            }
            container.add(namedtype);
            return PROCESS_ABORT;
        }
        if (type instanceof ITypedef) {
            CPPASTNamedTypeSpecifier namedtype = new CPPASTNamedTypeSpecifier(new CPPASTName(ASTTypeUtil.getQualifiedName((ICPPBinding) type)
                    .toCharArray()));
            if (container.size() > 0) {
                container.getNodesToWrite().remove(0);
            }
            container.add(namedtype);
            return PROCESS_ABORT;
        }
        return PROCESS_CONTINUE;
    }

    public boolean hasFound() {
        return (container.size() > 0);
    }

    public IASTNode getType() {
        return container.getNodesToWrite().get(0);
    }
}
