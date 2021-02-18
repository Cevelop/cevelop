package com.cevelop.charwars.quickfixes.cstring.common.refactorings;

import java.util.EnumSet;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import com.cevelop.charwars.asttools.ASTAnalyzer;
import com.cevelop.charwars.asttools.CheckAnalyzer;
import com.cevelop.charwars.constants.Function;
import com.cevelop.charwars.quickfixes.cstring.common.refactorings.Context.Kind;
import com.cevelop.charwars.utils.analyzers.BoolAnalyzer;
import com.cevelop.charwars.utils.analyzers.FunctionAnalyzer;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;
import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.IBetterFactory;


public class OperatorRefactoring extends Refactoring {

    private static final IBetterFactory FACTORY = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();
    private Function                    inFunction;
    private Function                    outFunction;

    public OperatorRefactoring(Function inFunction, Function outFunction, EnumSet<Kind> contextKinds) {
        this.inFunction = inFunction;
        this.outFunction = outFunction;
        setContextKinds(contextKinds);
    }

    @Override
    protected void prepareConfiguration(IASTIdExpression idExpression, Context context) {
        if (context.isOffset(idExpression)) {
            return;
        }

        boolean isStrcmpOrWcscmp = (inFunction == Function.STRCMP) || (inFunction == Function.WCSCMP);
        if (isStrcmpOrWcscmp) {
            boolean isStringEqualityCheck = outFunction == Function.OP_EQUALS && CheckAnalyzer.isPartOfStringCheck(idExpression, true);
            boolean isStringInequalityCheck = outFunction == Function.OP_NOT_EQUALS && CheckAnalyzer.isPartOfStringCheck(idExpression, false);

            if (isStringEqualityCheck || isStringInequalityCheck) {
                makeApplicable(BoolAnalyzer.getEnclosingBoolean(idExpression));
            }
        } else if (FunctionAnalyzer.isFunctionCallArg(idExpression, 0, inFunction)) {
            makeApplicable(idExpression.getParent());
        }
    }

    @Override
    protected IASTNode getReplacementNode(IASTIdExpression idExpression, Context context) {
        IASTFunctionCallExpression functionCall = (IASTFunctionCallExpression) idExpression.getParent();
        IASTInitializerClause[] args = functionCall.getArguments();
        IASTExpression lhs = idExpression;

        if (outFunction == Function.OP_ASSIGNMENT) {
            IASTExpression rhs = (IASTExpression) ASTAnalyzer.extractStdStringArg(args[1]);
            return FACTORY.newAssignment(lhs, rhs);
        } else if (outFunction == Function.OP_PLUS_ASSIGNMENT) {
            IASTExpression rhs = (IASTExpression) ASTAnalyzer.extractStdStringArg(args[1]);
            return FACTORY.newPlusAssignment(lhs, rhs);
        } else if (outFunction == Function.OP_EQUALS || outFunction == Function.OP_NOT_EQUALS) {
            IASTExpression rhs = (IASTExpression) (args[0] == idExpression ? args[1] : args[0]);
            return FACTORY.newEqualityComparison(lhs, rhs, outFunction == Function.OP_EQUALS);
        }
        return null;
    }
}
