package com.cevelop.charwars.quickfixes.cstring.parameter;

import java.util.Arrays;

import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;

import com.cevelop.charwars.asttools.ASTAnalyzer;
import com.cevelop.charwars.asttools.ASTRewriteCache;
import com.cevelop.charwars.asttools.CheckAnalyzer;
import com.cevelop.charwars.utils.analyzers.BEAnalyzer;


public class RewriteStrategyFactory {

    public static RewriteStrategy createRewriteStrategy(ICPPASTParameterDeclaration strParameter, ASTRewriteCache rewriteCache) {
        RewriteStrategy rewriteStrategy = null;
        final IASTName strName = strParameter.getDeclarator().getName();
        final ICPPASTFunctionDeclarator functionDeclarator = (ICPPASTFunctionDeclarator) strParameter.getParent();
        final IASTFunctionDefinition functionDefinition = (IASTFunctionDefinition) functionDeclarator.getParent();
        final IASTStatement statements[] = ((IASTCompoundStatement) functionDefinition.getBody()).getStatements();

        if (hasGuardClause(statements, strName)) {
            rewriteStrategy = new GuardClauseRewriteStrategy();
        } else if (hasResultVariable(statements, strName)) {
            rewriteStrategy = new ResultVariableRewriteStrategy();
        } else if (hasNullCheck(statements, strName)) {
            rewriteStrategy = new NullCheckRewriteStrategy();
        } else {
            rewriteStrategy = new NoNullCheckRewriteStrategy();
        }

        rewriteStrategy.setRewriteCache(rewriteCache);
        rewriteStrategy.setStrParameter(strParameter);
        rewriteStrategy.setStatements(statements);
        return rewriteStrategy;
    }

    private static boolean hasGuardClause(IASTStatement[] bodyStatements, IASTName strName) {
        return CheckAnalyzer.findGuardClause(strName, bodyStatements) != null;
    }

    private static boolean hasNullCheck(IASTStatement[] bodyStatements, IASTName strName) {
        return CheckAnalyzer.findNullCheck(strName, bodyStatements) != null;
    }

    private static boolean hasResultVariable(IASTStatement[] bodyStatements, IASTName strName) {
        final IASTIfStatement nullCheck = CheckAnalyzer.findNullCheck(strName, bodyStatements);
        if (nullCheck == null) {
            return false;
        }

        final IASTName resultVariableName = ASTAnalyzer.getResultVariableName(bodyStatements);
        if (resultVariableName == null) {
            return false;
        }

        final IASTStatement[] nullCheckedStatements = CheckAnalyzer.getNullCheckedStatements(strName, bodyStatements);
        if (nullCheckedStatements.length == 0) {
            return false;
        }
        final IASTStatement lastClauseStatement = nullCheckedStatements[nullCheckedStatements.length - 1];

        if (!assignsToVariable(lastClauseStatement, resultVariableName)) {
            return false;
        }

        final IASTDeclarationStatement resultVariableDeclaration = ASTAnalyzer.getVariableDeclaration(resultVariableName, bodyStatements);
        if (resultVariableDeclaration != null) {
            final int resultVariableIndex = Arrays.asList(bodyStatements).indexOf(resultVariableDeclaration);
            final int nullCheckIndex = Arrays.asList(bodyStatements).indexOf(nullCheck);
            return nullCheckIndex > resultVariableIndex;
        }

        return false;
    }

    private static boolean assignsToVariable(IASTStatement statement, IASTName variableName) {
        if (statement instanceof IASTExpressionStatement) {
            final IASTExpression expression = ((IASTExpressionStatement) statement).getExpression();
            if (BEAnalyzer.isAssignment(expression)) {
                final IASTExpression variable = BEAnalyzer.getOperand1(expression);
                if (variable instanceof IASTIdExpression) {
                    final IASTName name = ((IASTIdExpression) variable).getName();
                    return name.resolveBinding().equals(variableName.resolveBinding());
                }
            }
        }
        return false;
    }
}
