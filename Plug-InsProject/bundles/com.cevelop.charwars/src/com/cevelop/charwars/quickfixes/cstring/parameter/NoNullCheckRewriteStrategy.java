package com.cevelop.charwars.quickfixes.cstring.parameter;

import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;

import com.cevelop.charwars.asttools.ASTModifier;
import com.cevelop.charwars.asttools.IndexFinder;
import com.cevelop.charwars.asttools.IndexFinder.IndexFinderInstruction;


public class NoNullCheckRewriteStrategy extends RewriteStrategy {

    @Override
    protected IASTCompoundStatement getStdStringOverloadBody() {
        final IASTCompoundStatement body = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newCompoundStatement();
        for (final IASTStatement statement : statements) {
            body.addStatement(statement.copy(CopyStyle.withLocations));
        }
        return body;
    }

    @Override
    public void adaptCStringOverload() {
        ASTModifier.remove(functionDefinition, getMainRewrite());
        IndexFinder.findDeclarations(functionDefinition.getDeclarator().getName(), rewriteCache, (name, rewrite) -> {
            final ICPPASTFunctionDeclarator functionDeclarator = (ICPPASTFunctionDeclarator) name.getParent();
            if (functionDeclarator.getParent() instanceof IASTSimpleDeclaration) {
                ASTModifier.remove(functionDeclarator.getParent(), rewrite);
            }
            return IndexFinderInstruction.CONTINUE_SEARCH;
        });
    }

    @Override
    protected boolean shouldCopyDefaultValueOfParameter() {
        return true;
    }
}
