package com.cevelop.codeanalysator.autosar.quickfix;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTLiteralExpression;

import com.cevelop.codeanalysator.autosar.util.LiteralHelper;
import com.cevelop.codeanalysator.core.quickfix.BaseQuickFix;


@SuppressWarnings("restriction")
public class MakeHexLiteralUppercaseQuickFix extends BaseQuickFix {

    public MakeHexLiteralUppercaseQuickFix(String label) {
        super(label);
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        if (!(markedNode instanceof CPPASTLiteralExpression)) {
            return;
        }
        CPPASTLiteralExpression literalExpression = (CPPASTLiteralExpression) markedNode;

        String value = LiteralHelper.removePreAndPostFix(literalExpression).toUpperCase();
        String prefix = LiteralHelper.getPrefix(literalExpression.toString());
        String suffix = LiteralHelper.getSuffixAsString(literalExpression.getKind(), literalExpression.getValue());

        CPPASTLiteralExpression newliteralExpression = literalExpression.copy();
        newliteralExpression.setValue(prefix.concat(value).concat(suffix.toString()).toCharArray());
        hRewrite.replace(literalExpression, newliteralExpression, null);
    }
}
