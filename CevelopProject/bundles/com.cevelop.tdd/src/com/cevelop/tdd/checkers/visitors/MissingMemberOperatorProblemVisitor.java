package com.cevelop.tdd.checkers.visitors;

import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IArrayType;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IEnumeration;
import org.eclipse.cdt.core.dom.ast.IFunctionType;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.IProblemType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTUnaryExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPClassType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPUnknownType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.OverloadableOperator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

import ch.hsr.ifs.iltis.core.functional.functions.Consumer3;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;

import com.cevelop.tdd.helpers.IdHelper.ProblemId;
import com.cevelop.tdd.infos.MemberOperatorInfo;


public class MissingMemberOperatorProblemVisitor extends ASTVisitor {

    {
        shouldVisitExpressions = true;
    }

    private Consumer3<IProblemId<ProblemId>, IASTExpression, MemberOperatorInfo> problemReporter;

    public MissingMemberOperatorProblemVisitor(Consumer3<IProblemId<ProblemId>, IASTExpression, MemberOperatorInfo> problemReporter) {
        this.problemReporter = problemReporter;
    }

    @Override
    public int visit(IASTExpression expression) {
        if (expression instanceof ICPPASTUnaryExpression) {
            CPPASTUnaryExpression uexpr = ((CPPASTUnaryExpression) expression);
            if (uexpr == null || uexpr.getOperand() == null) {
                return PROCESS_CONTINUE;
            }
            String typename = ASTTypeUtil.getType(uexpr.getOperand().getExpressionType(), true);
            return handleUnaryOperator(expression, typename, uexpr);
        } else if (expression instanceof ICPPASTBinaryExpression) {
            ICPPASTBinaryExpression binex = (ICPPASTBinaryExpression) expression;
            String typename = ASTTypeUtil.getType(binex.getOperand1().getExpressionType(), true);
            return handleBinaryOperator(typename, binex);
        }
        return PROCESS_CONTINUE;
    }

    private int handleBinaryOperator(String typename, ICPPASTBinaryExpression binex) {
        ICPPFunction overload = binex.getOverload();
        if (overload == null && hasTypeOperand(binex) && hasKnownTypes(binex)) {
            OverloadableOperator operator = OverloadableOperator.fromBinaryExpression(binex.getOperator());
            if (operator != null) {
                reportMissingOperator(typename, binex, operator, binex.getOperand1().getExpressionType());
                return PROCESS_SKIP;
            }
        }
        return PROCESS_CONTINUE;
    }

    private int handleUnaryOperator(IASTExpression expression, String typename, ICPPASTUnaryExpression uexpr) {
        if (uexpr.getOverload() == null && operandDefined(uexpr) && hasNonPrimitiveType(uexpr.getOperand()) && hasKnownType(uexpr.getOperand()) &&
            !isAddressOfOperator(uexpr)) {
            OverloadableOperator operator = OverloadableOperator.fromUnaryExpression(uexpr.getOperator());
            if (operator != null) {
                reportMissingOperator(typename, expression, operator, uexpr.getOperand().getExpressionType());
                return PROCESS_SKIP;
            }
        }
        return PROCESS_CONTINUE;
    }

    private void reportMissingOperator(String typename, IASTExpression expr, OverloadableOperator operator, IType type) {
        String operatorname = new String(operator.toCharArray()).replaceAll("operator ", "");
        MemberOperatorInfo info = new MemberOperatorInfo();
        info.operatorName = operatorname;
        info.typeName = typename;
        if (type instanceof CPPClassType) {
            IASTFileLocation fileLocation = ((CPPClassType) type).getCompositeTypeSpecifier().getFileLocation();
            info.hostTypeStart = fileLocation.getNodeOffset();
            info.hostTypeLength = fileLocation.getNodeLength();
        }
        problemReporter.accept(ProblemId.MISSING_MEMBER_OPERATOR, expr, info);
    }

    private boolean operandDefined(ICPPASTUnaryExpression uexpr) {
        IASTExpression operand = uexpr.getOperand();
        if (operand instanceof IASTIdExpression) {
            return !hasProblemBinding((IASTIdExpression) operand);
        }
        return false;
    }

    private boolean hasProblemBinding(IASTIdExpression operand) {
        return operand.getName().getBinding() instanceof IProblemBinding;
    }

    private boolean hasNonPrimitiveType(IASTExpression operand) {
        return !hasPrimitiveType(operand);
    }

    private boolean hasPrimitiveType(IASTExpression operand) {
        IType type = operand.getExpressionType();
        type = SemanticUtil.getUltimateTypeUptoPointers(type);
        return type instanceof IBasicType || type instanceof IEnumeration || type instanceof IFunctionType || type instanceof IPointerType ||
               type instanceof IArrayType;
    }

    private boolean hasTypeOperand(IASTBinaryExpression expression) {
        return hasNonPrimitiveType(expression.getOperand1()) || hasNonPrimitiveType(expression.getOperand2());
    }

    private boolean hasKnownTypes(IASTBinaryExpression expression) {
        return hasKnownType(expression.getOperand1()) && hasKnownType(expression.getOperand2());
    }

    private boolean hasKnownType(IASTExpression operand) {
        IType type = operand.getExpressionType();
        type = SemanticUtil.getNestedType(type, SemanticUtil.TDEF | SemanticUtil.ALLCVQ);
        return !(type instanceof ICPPUnknownType || type instanceof IProblemType || type instanceof IProblemBinding);
    }

    private boolean isAddressOfOperator(ICPPASTUnaryExpression uexpr) {
        return uexpr.getOperator() == IASTUnaryExpression.op_amper;
    }
}
