package com.cevelop.codeanalysator.core.visitor.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.model.ITranslationUnit;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.util.ASTHelper;
import com.cevelop.codeanalysator.core.util.LossyType;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class AvoidLossyArithmeticConversionsVisitor extends CodeAnalysatorVisitor {

    Map<ITranslationUnit, IASTTranslationUnit> astCache = null;

    public AvoidLossyArithmeticConversionsVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
        astCache = new HashMap<>();
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarators = true;
        shouldVisitExpressions = true;
    }

    @Override
    public int visit(final IASTExpression expression) {
        if (!isHighestPriorityRuleForNode(expression)) {
            return PROCESS_CONTINUE;
        }
        if (expression instanceof IASTBinaryExpression) {
            IASTBinaryExpression binexp = (IASTBinaryExpression) expression;
            int operator = binexp.getOperator();
            if (operator == IASTBinaryExpression.op_assign || operator == IASTBinaryExpression.op_multiplyAssign ||
                operator == IASTBinaryExpression.op_divideAssign || operator == IASTBinaryExpression.op_moduloAssign ||
                operator == IASTBinaryExpression.op_plusAssign || operator == IASTBinaryExpression.op_minusAssign ||
                operator == IASTBinaryExpression.op_shiftLeftAssign || operator == IASTBinaryExpression.op_shiftRightAssign ||
                operator == IASTBinaryExpression.op_binaryAndAssign || operator == IASTBinaryExpression.op_binaryXorAssign ||
                operator == IASTBinaryExpression.op_binaryOrAssign) {

                List<String> intermediateTypes = new ArrayList<>();
                String from = getTypeStringFromExpressionElement(binexp.getChildren()[1], intermediateTypes);
                String to = getTypeStringFromExpressionElement(binexp.getChildren()[0]);
                analyseLossy(from, to, expression, intermediateTypes);
            }
        }
        if (expression instanceof IASTFunctionCallExpression) {
            IASTFunctionCallExpression functionCall = (IASTFunctionCallExpression) expression;
            IASTName name = null;
            if (functionCall.getChildren()[0] instanceof IASTIdExpression) {
                name = ((IASTIdExpression) (functionCall.getChildren()[0])).getName();
            } else if (functionCall.getChildren()[0] instanceof IASTFieldReference) {
                name = ((IASTFieldReference) (functionCall.getChildren()[0])).getFieldName();
            }
            if (name == null) {
                return super.visit(expression);
            }
            IBinding binding = name.resolveBinding();
            if (binding instanceof IFunction) {
                IFunction function = (IFunction) binding;

                IType[] parameterTypes = function.getType().getParameterTypes();
                IASTInitializerClause[] arguments = functionCall.getArguments();
                for (int i = 0; i < parameterTypes.length && i < arguments.length; i++) {
                    String from = getTypeStringFromExpressionElement(arguments[i]);
                    String to = parameterTypes[i].toString();
                    analyseLossy(from, to, expression);
                }
            }
        }
        return super.visit(expression);
    }

    @Override
    public int visit(final IASTDeclarator declarator) {
        for (IASTNode declaratorChild : declarator.getChildren()) {
            if (declaratorChild instanceof IASTEqualsInitializer) {
                IASTEqualsInitializer eqinit = (IASTEqualsInitializer) declaratorChild;

                IType typeTo = ASTHelper.getTypeFromBinding(declarator.getName().resolveBinding(), true);
                if (typeTo != null) {
                    String to = typeTo.toString();
                    for (IASTNode equalsInitializerChild : eqinit.getChildren()) {
                        if (equalsInitializerChild instanceof IASTIdExpression || equalsInitializerChild instanceof IASTCastExpression) {
                            List<String> intermediateTypes = new ArrayList<>();
                            String from = getTypeStringFromExpressionElement(equalsInitializerChild, intermediateTypes);
                            analyseLossy(from, to, declarator, intermediateTypes);
                        }
                    }
                }
            }
        }
        return super.visit(declarator);
    }

    private String getTypeStringFromExpressionElement(IASTNode node) {
        return getTypeStringFromExpressionElement(node, null);
    }

    private String getTypeStringFromExpressionElement(IASTNode node, List<String> intermediateTypes) {
        IType itype = ASTHelper.getTypeFromExpressionElement(node, true, intermediateTypes);
        return itype != null ? itype.toString() : "";
    }

    private void analyseLossy(String from, String to, IASTNode reportsubject) {
        analyseLossy(from, to, reportsubject, false, null);
    }

    private void analyseLossy(String from, String to, IASTNode reportsubject, List<String> intermediateTypes) {
        analyseLossy(from, to, reportsubject, false, intermediateTypes);
    }

    private void analyseLossy(String from, String to, IASTNode reportsubject, boolean functionArgumentVariant, List<String> intermediateTypes) {
        LossyType lossytype = LossyType.lossyType(from, to, functionArgumentVariant);
        boolean signedToUnsignedFound = false;
        if (lossytype != LossyType.UNKNOWN) {
            reportProblemForType(lossytype, reportsubject);
            return;
        } else if (intermediateTypes != null && intermediateTypes.size() > 0) {
            signedToUnsignedFound = checkSubCasts(reportsubject, functionArgumentVariant, intermediateTypes, from, to);
        }
        if (signedToUnsignedFound || LossyType.isSignedToUnsigned(from, to)) {
            if (functionArgumentVariant) {
                reportProblemForType(LossyType.ToUnsignedFunc, reportsubject);
            } else {
                reportProblemForType(LossyType.ToUnsigned, reportsubject);
            }
        }
    }

    private boolean checkSubCasts(IASTNode reportsubject, boolean functionArgumentVariant, List<String> intermediateTypes, String from, String to) {
        boolean signedToUnsignedFound = false;
        String subFrom = "";
        String subTo = "";
        for (int i = 0; i <= intermediateTypes.size(); i++) {
            if (i == 0) {
                subTo = to;
            } else {
                subTo = intermediateTypes.get(i - 1) != null ? intermediateTypes.get(i - 1).toString() : "";
            }
            if (i == (intermediateTypes.size())) {
                subFrom = from;
            } else {
                subFrom = intermediateTypes.get(i) != null ? intermediateTypes.get(i).toString() : "";
            }
            LossyType sublossyType = LossyType.lossyType(subFrom, subTo, functionArgumentVariant);
            if (sublossyType != LossyType.UNKNOWN) {
                reportProblemForType(sublossyType, reportsubject);
            }

            if (!signedToUnsignedFound) {
                signedToUnsignedFound = LossyType.isSignedToUnsigned(subFrom, subTo);
            }
        }
        return signedToUnsignedFound;
    }

    public void reportProblemForType(LossyType type, IASTNode node) {
        reportRuleForNode(node, type.getMessage());
    }

    /* END GSLATOR */

}
