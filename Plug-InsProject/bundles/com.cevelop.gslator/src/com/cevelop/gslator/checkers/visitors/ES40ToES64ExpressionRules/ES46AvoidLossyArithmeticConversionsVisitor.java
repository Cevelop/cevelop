package com.cevelop.gslator.checkers.visitors.ES40ToES64ExpressionRules;

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
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.model.ITranslationUnit;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.ES40ToES64ExpressionRules.ES46AvoidLossyArithmeticConversionsChecker;
import com.cevelop.gslator.checkers.visitors.BaseVisitor;
import com.cevelop.gslator.checkers.visitors.ES40ToES64ExpressionRules.utils.ES46LossyType;
import com.cevelop.gslator.utils.ASTHelper;


public class ES46AvoidLossyArithmeticConversionsVisitor extends BaseVisitor {

    Map<ITranslationUnit, IASTTranslationUnit>         astCache = null;
    private ES46AvoidLossyArithmeticConversionsChecker es46checker;

    public ES46AvoidLossyArithmeticConversionsVisitor(BaseChecker checker) {
        super(checker);
        es46checker = (ES46AvoidLossyArithmeticConversionsChecker) checker;
        astCache = new HashMap<>();
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarators = true;
        shouldVisitExpressions = true;
    }

    @Override
    public int visit(final IASTExpression expression) {
        if (!nodeHasNoIgnoreAttribute(this, expression.getParent())) {
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
            IASTFunctionDeclarator functionDeclarator = ASTHelper.getFunctionDeclaratorFromName(name, astCache);

            if (functionDeclarator != null) {
                IASTInitializerClause[] params = functionCall.getArguments();
                Map<Integer, IType> paramsspec = ASTHelper.getFunctionArguments(functionDeclarator, true);
                int i = 0;
                for (IASTInitializerClause iastInitializerClause : params) {
                    List<String> intermediateTypes = new ArrayList<>();
                    String from = getTypeStringFromExpressionElement(iastInitializerClause, intermediateTypes);
                    String to = "";
                    if (paramsspec.get(i) != null) {
                        to = paramsspec.get(i).toString();
                    }
                    analyseLossy(from, to, iastInitializerClause, true, intermediateTypes);
                    i++;
                }
            }
        }
        return super.visit(expression);
    }

    @Override
    public int visit(final IASTDeclarator declarator) {
        if (!nodeHasNoIgnoreAttribute(this, declarator.getParent())) {
            return PROCESS_CONTINUE;
        }
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
        return PROCESS_CONTINUE;
    }

    private String getTypeStringFromExpressionElement(IASTNode node) {
        return getTypeStringFromExpressionElement(node, null);
    }

    private String getTypeStringFromExpressionElement(IASTNode node, List<String> intermediateTypes) {
        IType itype = ASTHelper.getTypeFromExpressionElement(node, true, intermediateTypes);
        return itype != null ? itype.toString() : "";
    }

    @SuppressWarnings("unused")
    private void analyseLossy(String from, String to, IASTNode reportsubject) {
        analyseLossy(from, to, reportsubject, false, null);
    }

    @SuppressWarnings("unused")
    private void analyseLossy(String from, String to, IASTNode reportsubject, boolean functionArgumentVariant) {
        analyseLossy(from, to, reportsubject, functionArgumentVariant, null);
    }

    private void analyseLossy(String from, String to, IASTNode reportsubject, List<String> intermediateTypes) {
        analyseLossy(from, to, reportsubject, false, intermediateTypes);
    }

    private void analyseLossy(String from, String to, IASTNode reportsubject, boolean functionArgumentVariant, List<String> intermediateTypes) {
        ES46LossyType lossytype = ES46LossyType.lossyType(from, to, functionArgumentVariant);
        boolean signedToUnsignedFound = false;
        if (lossytype != ES46LossyType.UNKNOWN) {
            es46checker.reportProblem(lossytype, reportsubject);
        } else if (intermediateTypes != null && intermediateTypes.size() > 0) {
            signedToUnsignedFound = checkSubCasts(reportsubject, functionArgumentVariant, intermediateTypes, from, to);
        }
        if (signedToUnsignedFound || ES46LossyType.isSignedToUnsigned(from, to)) {
            if (functionArgumentVariant) {
                es46checker.reportProblem(ES46LossyType.ToUnsignedFunc, reportsubject);
            } else {
                es46checker.reportProblem(ES46LossyType.ToUnsigned, reportsubject);
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
            ES46LossyType sublossyType = ES46LossyType.lossyType(subFrom, subTo, functionArgumentVariant);
            if (sublossyType != ES46LossyType.UNKNOWN) {
                es46checker.reportProblem(sublossyType, reportsubject);
            }

            if (!signedToUnsignedFound) {
                signedToUnsignedFound = ES46LossyType.isSignedToUnsigned(subFrom, subTo);
            }
        }
        return signedToUnsignedFound;
    }
}
