package com.cevelop.charwars.quickfixes.cstring.parameter;

import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTStatement;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;

import com.cevelop.charwars.asttools.ASTModifier;
import com.cevelop.charwars.asttools.CheckAnalyzer;


public class NullCheckRewriteStrategy extends RewriteStrategy {

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
        final IASTStatement nullCheckClause = CheckAnalyzer.getNullCheckClause(strName, statements);
        final IASTCompoundStatement newClause = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newCompoundStatement(
                getStdStringFunctionCallStatement());
        ASTModifier.replace(nullCheckClause, newClause, getMainRewrite());
    }

    @Override
    protected boolean shouldCopyDefaultValueOfParameter() {
        return false;
    }
}
