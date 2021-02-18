package com.cevelop.charwars.quickfixes.cstring.cleanup;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
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
import com.cevelop.charwars.constants.Function;
import com.cevelop.charwars.constants.StdString;
import com.cevelop.charwars.constants.StringType;
import com.cevelop.charwars.utils.ComplementaryNodeFactory;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;
import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.IBetterFactory;


public class SizeCleanupRefactoring extends CleanupRefactoring {

    private static final IBetterFactory FACTORY = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();

    public SizeCleanupRefactoring(IASTFunctionCallExpression functionCall, Function inFunction, Function outFunction, ASTRewrite rewrite) {
        super(functionCall, inFunction, outFunction, rewrite);
    }

    @Override
    protected void performOptimized() {
        String resultVarName = getResultVarName(functionCall);
        IASTDeclarationStatement posVarDS = FACTORY.newDeclarationStatement(StdString.STRING_SIZE_TYPE, resultVarName, newOutFunctionCall());
        ASTModifier.replace(oldStatement, posVarDS, rewrite);

        IASTNode block = ASTAnalyzer.getEnclosingBlock(oldStatement);
        IASTName varName = ((IASTDeclarator) functionCall.getParent().getParent()).getName();

        SizeReturnValueVisitor visitor = new SizeReturnValueVisitor(varName, rewrite);
        block.accept(visitor);
    }

    @Override
    protected void performNormal() {
        String resultVarName = getResultVarName(functionCall);
        IASTStatement oldStatementCopy = oldStatement.copy();
        IASTFunctionCallExpression functionCallCopy = getFunctionCallNode(oldStatementCopy);
        ASTModifier.replaceNode(functionCallCopy, newOutFunctionCall());
        ASTModifier.insertBefore(oldStatement.getParent(), oldStatement, oldStatementCopy, rewrite);

        IASTIdExpression resultVarIdExpression = FACTORY.newIdExpression(resultVarName);
        IASTBinaryExpression condition = FACTORY.newEqualityComparison(resultVarIdExpression, ComplementaryNodeFactory.newNposExpression(
                StringType.STRING), true);
        IASTBinaryExpression assignment = FACTORY.newAssignment(resultVarIdExpression, FACTORY.newMemberFunctionCallExpression(str.getName(),
                StdString.SIZE));
        IASTStatement assignmentStatement = FACTORY.newExpressionStatement(assignment);
        IASTCompoundStatement ifBody = FACTORY.newCompoundStatement(assignmentStatement);
        IASTIfStatement ifStatement = FACTORY.newIfStatement(condition, ifBody);

        ASTModifier.insertBefore(oldStatement.getParent(), oldStatement, ifStatement, rewrite);
        ASTModifier.remove(oldStatement, rewrite);
    }

    private IASTFunctionCallExpression newOutFunctionCall() {
        String outFunctionName = outFunction.getName();
        return FACTORY.newMemberFunctionCallExpression(str.getName(), outFunctionName, (IASTExpression) secondArg.copy());
    }
}
