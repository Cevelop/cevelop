package com.cevelop.codeanalysator.autosar.visitor;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunctionType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPPointerToMemberType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

import com.cevelop.codeanalysator.autosar.util.AliasFinder;
import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.util.VirtualHelper;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


@SuppressWarnings("restriction")
public class OnlyComparePointerToVirtualMemberFunctionWithNullPointerVisitor extends CodeAnalysatorVisitor {

    public OnlyComparePointerToVirtualMemberFunctionWithNullPointerVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitExpressions = true;
    }

    @Override
    public int visit(IASTExpression expression) {
        if (isAddressOfVirtualMemberFunctionExpression(expression)) {
            AliasFinder aliasFinder = new AliasFinder(expression.getTranslationUnit());
            checkExpression(expression, aliasFinder);
        }
        return PROCESS_CONTINUE;
    }

    private boolean isAddressOfVirtualMemberFunctionExpression(IASTExpression expression) {
        IType type = expression.getExpressionType();
        if (isPointerToMemberFunction(type)) {
            if (expression instanceof IASTUnaryExpression) {
                IASTUnaryExpression unaryExpression = (IASTUnaryExpression) expression;
                if (unaryExpression.getOperator() == IASTUnaryExpression.op_amper) {
                    IASTExpression ampersandExpression = unaryExpression.getOperand();
                    return isVirtualMethodIdExpression(ampersandExpression);
                }
            }
        }
        return false;
    }

    private boolean isPointerToMemberFunction(IType type) {
        type = SemanticUtil.getNestedType(type, SemanticUtil.TDEF);
        if (type instanceof ICPPPointerToMemberType) {
            ICPPPointerToMemberType pointerToMemberType = (ICPPPointerToMemberType) type;
            IType memberType = pointerToMemberType.getType();
            if (memberType instanceof IFunctionType) {
                return true;
            }
        }
        return false;
    }

    private boolean isVirtualMethodIdExpression(IASTExpression ampersandExpression) {
        if (ampersandExpression instanceof IASTIdExpression) {
            IASTIdExpression idExpression = (IASTIdExpression) ampersandExpression;
            IBinding binding = idExpression.getName().resolveBinding();
            return isVirtualMethod(binding);
        }
        return false;
    }

    private boolean isVirtualMethod(IBinding binding) {
        if (binding instanceof ICPPMethod) {
            ICPPMethod method = (ICPPMethod) binding;
            if (method.isVirtual() || VirtualHelper.overridesVirtualMethod(method)) {
                return true;
            }
        }
        return false;
    }

    private void checkExpression(IASTExpression expression, AliasFinder aliasFinder) {
        if (isComparingWithNonNullPointer(expression)) {
            reportRuleForNode(expression);
        } else {
            aliasFinder.findAlias(expression) //
                    .filter(alias -> isPointerToMemberFunction(alias.getType())) //
                    .ifPresent(alias -> checkAlias(alias, aliasFinder)); //
        }
    }

    private boolean isComparingWithNonNullPointer(IASTExpression expression) {
        IASTNode parent = expression.getParent();
        if (parent instanceof IASTBinaryExpression) {
            IASTBinaryExpression binaryExpression = (IASTBinaryExpression) parent;
            int operator = binaryExpression.getOperator();
            if (operator == IASTBinaryExpression.op_equals || operator == IASTBinaryExpression.op_notequals) {
                IASTExpression otherOperand = getOtherOperand(binaryExpression, expression);
                return isNotNullPointerExpression(otherOperand);
            }
        }
        return false;
    }

    private IASTExpression getOtherOperand(IASTBinaryExpression binaryExpression, IASTExpression thisOperand) {
        IASTExpression operand1 = binaryExpression.getOperand1();
        IASTExpression operand2 = binaryExpression.getOperand2();
        if (operand1 == thisOperand) {
            return operand2;
        } else {
            return operand1;
        }
    }

    private boolean isNotNullPointerExpression(IASTExpression expression) {
        if (expression instanceof IASTLiteralExpression) {
            IASTLiteralExpression literalExpression = (IASTLiteralExpression) expression;
            return literalExpression.getKind() != IASTLiteralExpression.lk_nullptr;
        }
        return true;
    }

    private void checkAlias(IVariable alias, AliasFinder aliasFinder) {
        aliasFinder.getUsesOfAlias(alias) //
                .forEach(aliasUse -> checkExpression(aliasUse, aliasFinder));
    }
}
