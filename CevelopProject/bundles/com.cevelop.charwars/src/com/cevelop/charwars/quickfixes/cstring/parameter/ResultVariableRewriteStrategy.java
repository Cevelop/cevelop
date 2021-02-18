package com.cevelop.charwars.quickfixes.cstring.parameter;

import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTStatement;

import com.cevelop.charwars.asttools.ASTAnalyzer;
import com.cevelop.charwars.asttools.ASTModifier;
import com.cevelop.charwars.asttools.CheckAnalyzer;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;


public class ResultVariableRewriteStrategy extends RewriteStrategy {

    @Override
    protected IASTCompoundStatement getStdStringOverloadBody() {
        final IASTCompoundStatement body = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newCompoundStatement();
        final IASTName resultVariableName = ASTAnalyzer.getResultVariableName(statements);
        final IASTDeclarationStatement resultVariableDeclaration = ASTAnalyzer.getVariableDeclaration(resultVariableName, statements);
        body.addStatement(resultVariableDeclaration.copy(CopyStyle.withLocations));

        final IASTStatement[] nullCheckedStatements = CheckAnalyzer.getNullCheckedStatements(strName, statements);
        for (final IASTStatement statement : nullCheckedStatements) {
            body.addStatement(statement.copy(CopyStyle.withLocations));
        }

        final IASTStatement returnStatement = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newReturnStatement(ASTNodeFactoryFactory
                .getDefaultCPPNodeFactory().newIdExpression(resultVariableName.toString()));
        body.addStatement(returnStatement);
        return body;
    }

    @Override
    public void adaptCStringOverload() {
        final IASTName resultVariableName = ASTAnalyzer.getResultVariableName(statements);
        final IASTStatement nullCheckClause = CheckAnalyzer.getNullCheckClause(strName, statements);
        final IASTIdExpression resultVariable = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newIdExpression(resultVariableName.toString());
        final IASTExpression assignment = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newAssignment(resultVariable,
                getStdStringFunctionCallExpression());
        final IASTExpressionStatement expressionStatement = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newExpressionStatement(assignment);
        final IASTCompoundStatement compoundStatement = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newCompoundStatement(expressionStatement);
        ASTModifier.replace(nullCheckClause, compoundStatement, getMainRewrite());
    }

    @Override
    protected boolean shouldCopyDefaultValueOfParameter() {
        return false;
    }
}
