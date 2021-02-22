package com.cevelop.charwars.quickfixes.cstring.common.refactorings;

import java.util.Arrays;
import java.util.EnumSet;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;
import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.IBetterFactory;

import com.cevelop.charwars.constants.Function;
import com.cevelop.charwars.quickfixes.cstring.common.refactorings.Context.Kind;
import com.cevelop.charwars.utils.analyzers.FunctionAnalyzer;


public class FunctionRefactoring extends Refactoring {

    private static final IBetterFactory FACTORY = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();
    private Function                    inFunction;
    private Function                    outFunction;
    private ArgMapping                  argMapping;

    public FunctionRefactoring(Function inFunction, Function outFunction, ArgMapping argMapping, EnumSet<Kind> contextKinds) {
        this.inFunction = inFunction;
        this.outFunction = outFunction;
        this.argMapping = argMapping;
        setContextKinds(contextKinds);
    }

    @Override
    protected void prepareConfiguration(IASTIdExpression idExpression, Context context) {
        if (canHandleOffsets() && (context.isOffset(idExpression) || FunctionAnalyzer.hasOffset(idExpression, inFunction))) {
            if (FunctionAnalyzer.isPartOfFunctionCallArg(idExpression, 0, inFunction)) {
                IASTNode nodeToReplace = FunctionAnalyzer.getEnclosingFunctionCall(idExpression, inFunction);
                makeApplicable(nodeToReplace);
            }
        } else if (!context.isOffset(idExpression)) {
            if (FunctionAnalyzer.isFunctionCallArg(idExpression, 0, inFunction)) {
                makeApplicable(idExpression.getParent());
            }
        }
    }

    @Override
    protected IASTNode getReplacementNode(IASTIdExpression idExpression, Context context) {
        IASTName stringName = context.createStringVarName();
        String outFunctionName = outFunction.getName();
        boolean isMemberFunction = outFunction.isMemberFunction();
        IASTNode nodeToReplace = (IASTNode) config.get(NODE_TO_REPLACE);
        IASTFunctionCallExpression inFunctionCall = (IASTFunctionCallExpression) nodeToReplace;
        IASTNode adaptedArguments[] = argMapping.getOutArguments(inFunctionCall.getArguments(), idExpression, context);

        if (isMemberFunction) {
            IASTFunctionCallExpression memberFunctionCall = FACTORY.newMemberFunctionCallExpression(stringName, outFunctionName, Arrays.copyOf(
                    adaptedArguments, adaptedArguments.length, IASTExpression[].class));

            // special case for strlen() / wcslen()
            if (outFunction == Function.SIZE && (idExpression.getParent() != nodeToReplace || context.isOffset(idExpression))) {
                IASTNode offset = context.getOffset(idExpression);
                IASTBinaryExpression minusExpression = FACTORY.newMinusExpression(memberFunctionCall, (IASTExpression) offset);
                return FACTORY.newBracketedExpression(minusExpression);
            }

            return memberFunctionCall;
        } else {
            return FACTORY.newFunctionCallExpression(outFunctionName, Arrays.copyOf(adaptedArguments, adaptedArguments.length,
                    IASTExpression[].class));
        }
    }

    @Override
    protected void updateChangeDescription(ASTChangeDescription changeDescription) {
        changeDescription.setStatementHasChanged(true);
        changeDescription.addHeaderToInclude(outFunction.getHeader());
    }
}
