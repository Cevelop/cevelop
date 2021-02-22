package com.cevelop.charwars.quickfixes.cstring.parameter;

import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTStatement;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;

import com.cevelop.charwars.asttools.ASTModifier;
import com.cevelop.charwars.asttools.CheckAnalyzer;


public class GuardClauseRewriteStrategy extends RewriteStrategy {

    @Override
    protected IASTCompoundStatement getStdStringOverloadBody() {
        final IASTCompoundStatement body = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newCompoundStatement();
        final IASTStatement[] nullCheckedStatements = CheckAnalyzer.getNullCheckedStatements(strName, statements);
        for (final IASTStatement statement : nullCheckedStatements) {
            body.addStatement(statement.copy(CopyStyle.withLocations));
        }
        return body;
    }

    @Override
    public void adaptCStringOverload() {
        final IASTStatement[] nullCheckedStatements = CheckAnalyzer.getNullCheckedStatements(strName, statements);
        final IASTStatement insertionPoint = nullCheckedStatements[0];
        ASTModifier.insertBefore(insertionPoint.getParent(), insertionPoint, getStdStringFunctionCallStatement(), getMainRewrite());
        for (final IASTStatement statement : nullCheckedStatements) {
            ASTModifier.remove(statement, getMainRewrite());
        }
    }

    @Override
    protected boolean shouldCopyDefaultValueOfParameter() {
        return false;
    }
}
