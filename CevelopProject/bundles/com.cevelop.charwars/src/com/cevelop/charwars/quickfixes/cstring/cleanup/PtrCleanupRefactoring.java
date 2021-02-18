package com.cevelop.charwars.quickfixes.cstring.cleanup;

import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.charwars.asttools.ASTAnalyzer;
import com.cevelop.charwars.asttools.ASTModifier;
import com.cevelop.charwars.constants.Constants;
import com.cevelop.charwars.constants.Function;
import com.cevelop.charwars.constants.StdString;
import com.cevelop.charwars.constants.StringType;
import com.cevelop.charwars.quickfixes.cstring.common.BlockRefactoring;
import com.cevelop.charwars.quickfixes.cstring.common.BlockRefactoringConfiguration;
import com.cevelop.charwars.utils.ComplementaryNodeFactory;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;
import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.IBetterFactory;


public class PtrCleanupRefactoring extends CleanupRefactoring {

    private static final IBetterFactory FACTORY = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();

    public PtrCleanupRefactoring(IASTFunctionCallExpression functionCall, Function inFunction, Function outFunction, ASTRewrite rewrite) {
        super(functionCall, inFunction, outFunction, rewrite);
    }

    @Override
    protected void performOptimized() {
        String posVarName = getPosVarName(functionCall);
        IASTDeclarationStatement posVarDS = FACTORY.newDeclarationStatement(StdString.STRING_SIZE_TYPE, posVarName, newOutFunctionCall());
        IASTNode block = ASTAnalyzer.getEnclosingBlock(oldStatement);
        IASTName varName = ((IASTDeclarator) functionCall.getParent().getParent()).getName();

        BlockRefactoringConfiguration config = new BlockRefactoringConfiguration();
        config.setBlock(block);
        config.setASTRewrite(rewrite);
        config.setStringType(StringType.STRING);
        config.setStrName(str.getName());
        config.setVarName(varName);
        config.setNewVarNameString(posVarName);

        BlockRefactoring blockRefactoring = new BlockRefactoring(config);
        blockRefactoring.refactorAllStatements();

        ASTModifier.insertBefore(oldStatement.getParent(), oldStatement, posVarDS, rewrite);
        ASTModifier.remove(oldStatement, rewrite);
    }

    @Override
    protected void performNormal() {
        String posVarName = getPosVarName(functionCall);
        IASTDeclarationStatement posVarDS = FACTORY.newDeclarationStatement(StdString.STRING_SIZE_TYPE, posVarName, newOutFunctionCall());
        IASTConditionalExpression conditionalExpression = newConditionalExpression(posVarName);

        if (oldStatement.getParent() instanceof IASTIfStatement) {
            IASTStatement oldStatementCopy = oldStatement.copy();
            IASTFunctionCallExpression functionCallCopy = getFunctionCallNode(oldStatementCopy);
            ASTModifier.replaceNode(functionCallCopy, conditionalExpression);
            IASTCompoundStatement compoundStatement = FACTORY.newCompoundStatement(posVarDS, oldStatementCopy);
            ASTModifier.replace(oldStatement, compoundStatement, rewrite);
        } else {
            ASTModifier.insertBefore(oldStatement.getParent(), oldStatement, posVarDS, rewrite);
            ASTModifier.replace(functionCall, conditionalExpression, rewrite);
        }
    }

    private IASTConditionalExpression newConditionalExpression(String posVarName) {
        IASTIdExpression posVar = FACTORY.newIdExpression(posVarName);
        IASTExpression condition = FACTORY.newEqualityComparison(posVar, ComplementaryNodeFactory.newNposExpression(StringType.STRING), false);
        IASTExpression positive = FACTORY.newAdressOperatorExpression(FACTORY.newArraySubscriptExpression(str.copy(), posVar));
        IASTExpression negative = FACTORY.newIdExpression(Constants.NULLPTR);
        return FACTORY.newConditionalExpression(condition, positive, negative);
    }

    private IASTFunctionCallExpression newOutFunctionCall() {
        String outFunctionName = outFunction.getName();
        return FACTORY.newMemberFunctionCallExpression(str.getName(), outFunctionName, (IASTExpression) secondArg.copy());
    }
}
