package com.cevelop.constificator.core.deciders.classmembers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTImplicitName;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPDeferredFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPField;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMember;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPVariable;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPUnknownBinding;

import com.cevelop.constificator.core.util.semantic.Expression;
import com.cevelop.constificator.core.util.semantic.Expression.OperatorSide;
import com.cevelop.constificator.core.util.semantic.Type;
import com.cevelop.constificator.core.util.structural.Node;
import com.cevelop.constificator.core.util.structural.Relation;
import com.cevelop.constificator.core.util.type.Arrays;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.core.util.type.ReferenceWrapper;


@SuppressWarnings("restriction")
public class FunctionBodyVisitor extends ASTVisitor {

    private final ICPPMethod[] memberFunctions;
    private final ICPPField[]  memberVariables;
    private boolean            modifiesDataMember = false;

    public FunctionBodyVisitor(ICPPField[] memberVariables, ICPPMethod[] memberFunctions) {
        this.memberVariables = memberVariables;
        this.memberFunctions = memberFunctions;
        shouldVisitStatements = true;
    }

    private List<ICPPFunction> getMemberFunctionsUsedOn(IASTStatement line) {
        List<ICPPASTName> names = Relation.getDescendendsOf(ICPPASTName.class, line);

        return names.stream().map(ICPPASTName::resolveBinding).filter(b -> b instanceof ICPPFunction).map(b -> {
            if (b instanceof ICPPDeferredFunction) {
                ICPPDeferredFunction db = (ICPPDeferredFunction) b;
                ICPPFunction[] candidates = db.getCandidates();
                return candidates == null ? new ArrayList<ICPPFunction>() : java.util.Arrays.asList(db.getCandidates());
            } else {
                return Collections.singletonList((ICPPFunction) b);
            }
        }).reduce(new ArrayList<>(), (acc, l) -> {
            acc.addAll(l);
            return acc;
        }).stream().filter(f -> Arrays.isAnyOf(f, memberFunctions)).collect(Collectors.toList());
    }

    private List<ICPPASTName> getMemberUsesIn(IASTStatement line) {
        List<ICPPASTName> names = Relation.getDescendendsOf(ICPPASTName.class, line);

        ICPPASTName current = null;
        for (Iterator<ICPPASTName> it = names.iterator(); it.hasNext();) {
            current = it.next();
            if (!Arrays.isAnyOf(current.resolveBinding(), memberVariables)) {
                it.remove();
            }
        }

        return names;
    }

    private void handleBinary(ICPPASTBinaryExpression binary, ICPPASTName name) {
        modifiesDataMember |= (Expression.isModifyingOperation(binary, OperatorSide.LeftHandSide) && Expression.isLeftHandOperandOf(name, binary)) ||
                              (Expression.isModifyingOperation(binary, OperatorSide.RightHandSide) && Expression.isRightHandOperandOf(name, binary));
    }

    private void handleFieldReference(ICPPASTFieldReference expression, ICPPASTName use) {

        IASTName fieldName = expression.getFieldName();
        ICPPASTName resultingName = Expression.getResultingName(expression.getFieldOwner());

        if (resultingName != null && resultingName.equals(use)) {
            IBinding fieldBinding = fieldName.getBinding();

            if (fieldBinding instanceof ICPPMember) {
                try {
                    modifiesDataMember |= !Type.isConst(((ICPPMember) fieldBinding).getType(), 0);
                } catch (DOMException e) {}
            }
        }

    }

    private void handleFunctionCall(ICPPASTFunctionCallExpression expression, ICPPASTName name) {
        ICPPFunction function = Node.getBindingForFunction(expression.getFunctionNameExpression());
        if (function == null) {
            return;
        }

        if (function instanceof ICPPUnknownBinding) {
            modifiesDataMember = true;
            return;
        }

        if (function instanceof ICPPMethod && !Type.isConst(function.getType(), 0)) {
            modifiesDataMember = true;
        }

        ICPPParameter[] parameters = function.getParameters();
        IASTInitializerClause[] arguments = expression.getArguments();

        if (parameters.length != arguments.length) {
            return;
        }

        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i < arguments.length; ++i) {
            ICPPASTInitializerClause arg = (ICPPASTInitializerClause) arguments[i];
            ICPPASTName fieldName = Expression.getResultingName(arg);

            if (fieldName != null && fieldName.resolveBinding() instanceof ICPPField) {
                indices.add(i);
            }
        }

        for (int index : indices) {
            IType parameterType = parameters[index].getType();
            if (Type.isReference(parameterType) && !Type.isConst(parameterType, 1)) {
                modifiesDataMember = true;
                return;
            }

            ICPPASTName argumentName = Expression.getResultingName((ICPPASTInitializerClause) arguments[index]);
            ICPPField member = argumentName == null ? null : Cast.as(ICPPField.class, argumentName.resolveBinding());

            if (member != null) {
                final IType memberType = member.getType();
                if (Type.isArray(memberType)) {
                    modifiesDataMember = !Type.isMoreConst(parameterType, memberType);
                } else {
                    if (Type.pointerLevels(parameterType) == 1) {
                        modifiesDataMember = !Type.isConst(parameterType, 1);
                        continue;
                    }
                }
            }

            if (member != null) {}
        }
    }

    private void handleReturnStatement(IASTReturnStatement expression) {
        ICPPASTName name = Expression.getResultingName((ICPPASTExpression) expression.getReturnValue());

        if (name != null && Arrays.isAnyOf(name.resolveBinding(), memberVariables)) {
            ICPPASTFunctionDefinition functionDefinition;

            if ((functionDefinition = Relation.getAncestorOf(ICPPASTFunctionDefinition.class, name)) != null) {
                ICPPMethod function;
                if ((function = Cast.as(ICPPMethod.class, functionDefinition.getDeclarator().getName().resolveBinding())) != null) {
                    ICPPMember member = Cast.as(ICPPMember.class, name.getBinding());
                    IType returnType = function.getType().getReturnType();
                    int pointerLevels = Type.pointerLevels(returnType);

                    if (Type.isReference(returnType)) {
                        modifiesDataMember |= !Type.isConst(returnType, 1);
                    } else if (pointerLevels > 0) {
                        try {
                            IType memberType = member.getType();
                            if (member != null && pointerLevels > Type.pointerLevels(memberType)) {
                                modifiesDataMember |= !Type.isConst(returnType, Type.pointerLevels(memberType) + 1);
                            }
                        } catch (DOMException e) {}
                    }
                }

            }
        }
    }

    private void handleThis(ICPPASTLiteralExpression literal) {

        ReferenceWrapper<ICPPASTExpression> breaking = new ReferenceWrapper<>();
        if (Expression.isResolvedToInstance(literal, breaking)) {
            if (breaking.get() != null) {
                if (breaking.get() instanceof ICPPASTUnaryExpression) {
                    ICPPASTUnaryExpression unary = (ICPPASTUnaryExpression) breaking.get();
                    modifiesDataMember |= Expression.isModifyingOperation(unary);
                } else if (breaking.get() instanceof ICPPASTBinaryExpression) {
                    ICPPASTBinaryExpression binary = (ICPPASTBinaryExpression) breaking.get();
                    modifiesDataMember |= Expression.isModifyingOperation(binary, OperatorSide.LeftHandSide);
                }

                return;
            }
            IASTStatement statement = Relation.getAncestorOf(IASTStatement.class, literal);
            if (statement instanceof IASTReturnStatement) {
                ICPPASTFunctionDefinition function = Relation.getAncestorOf(ICPPASTFunctionDefinition.class, statement);
                ICPPASTName functionName = Cast.as(ICPPASTName.class, (function.getDeclarator().getName()));
                ICPPFunction functionBinding = Cast.as(ICPPFunction.class, functionName.resolveBinding());
                IType returnType = functionBinding.getType().getReturnType();
                modifiesDataMember |= !Type.isConst(returnType, Type.pointerLevels(returnType));
            }
        }

    }

    private void handleUnary(ICPPASTUnaryExpression unary, ICPPASTName name) {
        ICPPASTName operand = Relation.getDescendendOf(ICPPASTName.class, unary.getOperand());
        if (operand != null && operand.equals(name)) {
            ICPPField member = Cast.as(ICPPField.class, operand.getBinding());
            IType memberType = member == null ? null : member.getType();
            if (memberType == null || Type.isReference(memberType)) {
                return;
            }

            modifiesDataMember |= (Expression.isModifyingOperation(unary));
        }
    }

    private void handleArraySubscript(ICPPASTArraySubscriptExpression expression, ICPPASTName use) {
        IASTImplicitName[] operators = expression.getImplicitNames();
        if (operators.length == 0) {
            return;
        }

        ICPPMethod operator = Cast.as(ICPPMethod.class, operators[0].resolveBinding());
        if (operator == null) {
            return;
        }

        modifiesDataMember = !operator.getType().isConst();
    }

    public boolean modifiesDataMember() {
        return modifiesDataMember;
    }

    private void process(IASTStatement line) {
        List<ICPPASTExpression> expressions = Relation.getDescendendsOf(ICPPASTExpression.class, line);

        for (ICPPASTExpression expression : expressions) {
            List<ICPPASTName> memberUses = getMemberUsesIn(line);
            for (ICPPASTName use : memberUses) {
                ICPPVariable useBinding = Cast.as(ICPPVariable.class, use.resolveBinding());
                if (Type.pointerLevels(useBinding.getType()) > 0) {
                    if (!Expression.isDereferencedNTimes(use, 0)) {
                        continue;
                    }
                }

                if (expression instanceof ICPPASTBinaryExpression) {
                    handleBinary((ICPPASTBinaryExpression) expression, use);
                } else if (expression instanceof ICPPASTUnaryExpression) {
                    handleUnary((ICPPASTUnaryExpression) expression, use);
                } else if (expression instanceof ICPPASTFunctionCallExpression) {
                    handleFunctionCall((ICPPASTFunctionCallExpression) expression, use);
                } else if (expression instanceof ICPPASTFieldReference) {
                    handleFieldReference((ICPPASTFieldReference) expression, use);
                } else if (expression instanceof ICPPASTArraySubscriptExpression) {
                    handleArraySubscript((ICPPASTArraySubscriptExpression) expression, use);
                }

            }

            if (expression instanceof ICPPASTFunctionCallExpression) {
                getMemberFunctionsUsedOn(line).stream().forEach(f -> {
                    modifiesDataMember |= !Type.isConst(f.getType(), 0);
                });
            }

            List<ICPPASTLiteralExpression> literalExpressions = Relation.getDescendendsOf(ICPPASTLiteralExpression.class, expression);
            for (ICPPASTLiteralExpression literal : literalExpressions) {
                if (literal.getKind() == ICPPASTLiteralExpression.lk_this) {
                    handleThis(literal);
                }
            }

            if (modifiesDataMember) {
                break;
            }
        }

        if (line instanceof IASTReturnStatement) {
            handleReturnStatement((IASTReturnStatement) line);
        }
    }

    @Override
    public int visit(IASTStatement statement) {
        if (statement instanceof ICPPASTCompoundStatement) {
            for (IASTStatement line : ((ICPPASTCompoundStatement) statement).getStatements()) {
                if (line != null) {
                    process(line);
                }
            }
        }
        return PROCESS_CONTINUE;
    }

}
