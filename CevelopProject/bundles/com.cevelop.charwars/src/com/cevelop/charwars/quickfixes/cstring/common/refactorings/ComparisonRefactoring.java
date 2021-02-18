package com.cevelop.charwars.quickfixes.cstring.common.refactorings;

import java.util.Arrays;
import java.util.EnumSet;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import com.cevelop.charwars.asttools.CheckAnalyzer;
import com.cevelop.charwars.constants.Function;
import com.cevelop.charwars.constants.Function.Sentinel;
import com.cevelop.charwars.quickfixes.cstring.common.refactorings.Context.Kind;
import com.cevelop.charwars.utils.ComplementaryNodeFactory;
import com.cevelop.charwars.utils.analyzers.BEAnalyzer;
import com.cevelop.charwars.utils.analyzers.BoolAnalyzer;
import com.cevelop.charwars.utils.analyzers.FunctionAnalyzer;
import com.cevelop.charwars.utils.analyzers.UEAnalyzer;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;


public class ComparisonRefactoring extends Refactoring {

    private static final String IS_EQUAL = "IS_EQUAL";
    private Function            inFunction;
    private Function            outFunction;
    private ArgMapping          argMapping;

    public ComparisonRefactoring(Function inFunction, Function outFunction, ArgMapping argMapping, EnumSet<Kind> contextKinds) {
        this.inFunction = inFunction;
        this.outFunction = outFunction;
        this.argMapping = argMapping;
        setContextKinds(contextKinds);
    }

    @Override
    protected void prepareConfiguration(IASTIdExpression idExpression, Context context) {
        if (canHandleOffsets() && (context.isOffset(idExpression) || FunctionAnalyzer.hasOffset(idExpression, inFunction))) {
            if (FunctionAnalyzer.isPartOfFunctionCallArg(idExpression, 0, inFunction)) {
                IASTNode functionCall = FunctionAnalyzer.getEnclosingFunctionCall(idExpression, inFunction);
                prepare(idExpression, functionCall, context);
            }
        } else if (!context.isOffset(idExpression)) {
            if (FunctionAnalyzer.isFunctionCallArg(idExpression, 0, inFunction)) {
                prepare(idExpression, idExpression.getParent(), context);
            }
        }
    }

    private void makeApplicable(IASTNode nodeToReplace, boolean isEqual) {
        super.makeApplicable(nodeToReplace);
        config.put(IS_EQUAL, isEqual);
    }

    private void prepare(IASTIdExpression idExpression, IASTNode node, Context context) {
        Sentinel inFunctionSentinel = inFunction.getSentinel();
        IASTNode nodeToReplace = BoolAnalyzer.getEnclosingBoolean(idExpression);

        IASTNode comparedNode = node;
        IASTNode parent = node.getParent();
        while (parent != null && !BoolAnalyzer.isAssignedToBool(node) && (BEAnalyzer.isAssignment(parent) || UEAnalyzer.isBracketExpression(
                parent))) {
            comparedNode = parent;
            parent = parent.getParent();
        }

        if (inFunctionSentinel == Sentinel.NULL) {
            if (CheckAnalyzer.isNodeComparedToNull(comparedNode, true)) {
                makeApplicable(nodeToReplace, true);
            } else if (CheckAnalyzer.isNodeComparedToNull(comparedNode, false)) {
                makeApplicable(nodeToReplace, false);
            }
        } else if (inFunctionSentinel == Sentinel.STRLEN) {
            if (CheckAnalyzer.isNodeComparedToStrlen(comparedNode, true)) {
                makeApplicable(nodeToReplace, true);
            } else if (CheckAnalyzer.isNodeComparedToStrlen(comparedNode, false)) {
                makeApplicable(nodeToReplace, false);
            }
        }
    }

    @Override
    protected IASTNode getReplacementNode(IASTIdExpression idExpression, Context context) {
        IASTExpression lhs = createLhs(idExpression, context);
        IASTExpression sentinel = createSentinel(idExpression, context);
        return ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newEqualityComparison(lhs, sentinel, (boolean) config.get(IS_EQUAL));
    }

    private IASTExpression createLhs(IASTIdExpression idExpression, Context context) {
        IASTNode nodeToReplace = (IASTNode) config.get(NODE_TO_REPLACE);
        IASTExpression functionCall = createOutFunctionCall(idExpression, context);

        if (BEAnalyzer.isComparison(nodeToReplace)) {
            IASTNode op1 = BEAnalyzer.getOperand1(nodeToReplace);
            IASTNode op2 = BEAnalyzer.getOperand2(nodeToReplace);
            if (!FunctionAnalyzer.isCallToFunction(op1, inFunction) && !FunctionAnalyzer.isCallToFunction(op2, inFunction)) {
                IASTNode oldAssignment = UEAnalyzer.isBracketExpression(op1) ? UEAnalyzer.getOperand(op1) : UEAnalyzer.getOperand(op2);
                return createNewAssignment(oldAssignment, functionCall);
            }
        } else if (BEAnalyzer.isAssignment(nodeToReplace)) {
            return createNewAssignment(nodeToReplace, functionCall);
        } else if (UEAnalyzer.isBracketExpression(nodeToReplace)) {
            IASTNode oldAssignment = UEAnalyzer.getOperand(nodeToReplace);
            return createNewAssignment(oldAssignment, functionCall);
        }
        return functionCall;
    }

    private IASTExpression createNewAssignment(IASTNode oldAssignment, IASTExpression functionCall) {
        IASTExpression lvalue = BEAnalyzer.getOperand1(oldAssignment);
        IASTExpression newAssignment = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newAssignment(lvalue.copy(), functionCall);
        return ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newBracketedExpression(newAssignment);
    }

    private IASTFunctionCallExpression createOutFunctionCall(IASTIdExpression idExpression, Context context) {
        String outFunctionName = outFunction.getName();
        IASTNode outArguments[] = getOutArguments(idExpression, context);

        if (outFunction.isMemberFunction()) {
            return ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newMemberFunctionCallExpression(context.createStringVarName(), outFunctionName,
                    Arrays.copyOf(outArguments, outArguments.length, IASTExpression[].class));
        } else {
            return ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newFunctionCallExpression(outFunctionName, Arrays.copyOf(outArguments,
                    outArguments.length, IASTExpression[].class));
        }
    }

    private IASTExpression createSentinel(IASTIdExpression idExpression, Context context) {
        Sentinel outFunctionSentinel = outFunction.getSentinel();
        IASTExpression sentinel = null;

        if (outFunctionSentinel == Sentinel.NPOS) {
            sentinel = ComplementaryNodeFactory.newNposExpression(context.getStringType());
        } else if (outFunctionSentinel == Sentinel.END) {
            IASTNode outArguments[] = getOutArguments(idExpression, context);
            return (IASTExpression) outArguments[1];
        }

        return sentinel;
    }

    private IASTNode[] getOutArguments(IASTIdExpression idExpression, Context context) {
        IASTFunctionCallExpression inFunctionCall = FunctionAnalyzer.getEnclosingFunctionCall(idExpression, inFunction);
        return argMapping.getOutArguments(inFunctionCall.getArguments(), idExpression, context);
    }

    @Override
    protected void updateChangeDescription(ASTChangeDescription changeDescription) {
        changeDescription.setStatementHasChanged(true);
        changeDescription.addHeaderToInclude(outFunction.getHeader());
    }
}
