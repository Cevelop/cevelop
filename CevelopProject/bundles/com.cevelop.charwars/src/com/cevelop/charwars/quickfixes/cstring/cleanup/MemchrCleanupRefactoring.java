package com.cevelop.charwars.quickfixes.cstring.cleanup;

import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;
import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.IBetterFactory;

import com.cevelop.charwars.asttools.ASTAnalyzer;
import com.cevelop.charwars.asttools.ASTModifier;
import com.cevelop.charwars.constants.Constants;
import com.cevelop.charwars.constants.Function;
import com.cevelop.charwars.constants.StdString;
import com.cevelop.charwars.utils.analyzers.FunctionAnalyzer;


public class MemchrCleanupRefactoring extends CleanupRefactoring {

    private static final IBetterFactory FACTORY = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();

    public MemchrCleanupRefactoring(IASTFunctionCallExpression functionCall, Function inFunction, Function outFunction, ASTRewrite rewrite) {
        super(functionCall, inFunction, outFunction, rewrite);
    }

    @Override
    protected boolean optimizedRefactoringPossible() {
        return false;
    }

    @Override
    protected void performNormal() {
        final String posVarName = getPosVarName(functionCall);
        final IASTDeclarationStatement posVarDS = FACTORY.newDeclarationStatement(Constants.AUTO, posVarName, newOutFunctionCall());
        IASTExpression conditionalExpression = newMemchrConditionalExpression(posVarName);
        if (functionCall.getParent() instanceof IASTCastExpression) {
            conditionalExpression = FACTORY.newBracketedExpression(conditionalExpression);
        }

        if (oldStatement.getParent() instanceof IASTIfStatement) {
            final IASTStatement oldStatementCopy = oldStatement.copy();
            final IASTFunctionCallExpression functionCallCopy = getFunctionCallNode(oldStatementCopy);
            ASTModifier.replaceNode(functionCallCopy, conditionalExpression);
            final IASTCompoundStatement compoundStatement = FACTORY.newCompoundStatement(posVarDS, oldStatementCopy);
            ASTModifier.replace(oldStatement, compoundStatement, rewrite);
        } else {
            ASTModifier.insertBefore(oldStatement.getParent(), oldStatement, posVarDS, rewrite);
            ASTModifier.replace(functionCall, conditionalExpression, rewrite);
        }
    }

    @Override
    protected void performOptimized() {
        // not available for MemchrCleanupRefactoring
    }

    private IASTFunctionCallExpression newOutFunctionCall() {
        final IASTExpression first = newStartIterator();
        final IASTExpression last = newEndIterator();
        return FACTORY.newFunctionCallExpression(outFunction.getName(), first, last, (IASTExpression) secondArg.copy());
    }

    private IASTExpression newStartIterator() {
        final IASTName stringName = str.getName();
        return FACTORY.newMemberFunctionCallExpression(stringName, StdString.BEGIN);
    }

    private IASTExpression newEndIterator() {
        final IASTName stringName = str.getName();
        final IASTNode thirdArg = functionCall.getArguments()[2];
        IASTExpression last = null;

        if (FunctionAnalyzer.isCallToMemberFunction(thirdArg, Function.SIZE)) {
            final IASTFunctionCallExpression sizeCall = (IASTFunctionCallExpression) thirdArg;
            final IASTFieldReference functionNameExpr = (IASTFieldReference) sizeCall.getFunctionNameExpression();
            final IASTIdExpression fieldOwner = (IASTIdExpression) functionNameExpr.getFieldOwner();
            if (ASTAnalyzer.isSameName(fieldOwner.getName(), stringName)) {
                last = FACTORY.newMemberFunctionCallExpression(stringName, StdString.END);
            }
        }

        if (last == null) {
            final IASTExpression lhs = FACTORY.newMemberFunctionCallExpression(stringName, StdString.BEGIN);
            last = FACTORY.newPlusExpression(lhs.copy(), (IASTExpression) thirdArg.copy());
        }
        return last;
    }

    private IASTConditionalExpression newMemchrConditionalExpression(String posVarName) {
        final IASTIdExpression posVar = FACTORY.newIdExpression(posVarName);
        final IASTExpression condition = FACTORY.newEqualityComparison(posVar, newEndIterator(), false);
        final IASTExpression positive = FACTORY.newAdressOperatorExpression(FACTORY.newDereferenceOperatorExpression(posVar));
        final IASTExpression negative = FACTORY.newIdExpression(Constants.NULLPTR);
        return FACTORY.newConditionalExpression(condition, positive, negative);
    }
}
