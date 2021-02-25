package com.cevelop.gslator.quickfixes.ES40ToES64ExpressionRules;

import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeConstructorExpression;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IMarker;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.ES40ToES64ExpressionRules.ES49IfMustUseNamedCastChecker;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.quickfixes.BaseQuickFix;
import com.cevelop.gslator.quickfixes.utils.ASTFactory;


public abstract class ES49_00UseNamedCastQuickFix extends BaseQuickFix {

    protected abstract String getCastName(String targetCast);

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        IASTNode subjectNode = null;
        String toSpec = null;

        if (markedNode instanceof IASTCastExpression) {
            subjectNode = markedNode.getChildren()[1];
            toSpec = (markedNode.getChildren()[0]).getRawSignature();
        } else if (markedNode instanceof ICPPASTSimpleTypeConstructorExpression) {
            subjectNode = markedNode.getChildren()[1].getChildren()[0];
            toSpec = (markedNode.getChildren()[0]).toString();
        }

        while (subjectNode instanceof IASTUnaryExpression && ((IASTUnaryExpression) subjectNode)
                .getOperator() == IASTUnaryExpression.op_bracketedPrimary) {
            subjectNode = subjectNode.getChildren()[0];
        }
        subjectNode = subjectNode.copy(CopyStyle.withLocations);

        ICPPASTFunctionCallExpression newNode = ASTFactory.newQualifiedNamedFunctionCall(new String[] {}, getCastName(toSpec),
                new IASTInitializerClause[] { (IASTInitializerClause) subjectNode });

        hRewrite.replace(markedNode, newNode, null);
    }

    protected boolean isReinterpretCastQuickFixEnabled(IMarker marker) {
        ITranslationUnit itu = getTranslationUnitViaWorkspace(marker);
        BaseChecker checker = BaseChecker.getCheckerByProblemId(ProblemId.of(getProblemId(marker)));
        return itu != null && checker != null && (boolean) checker.getPreference(itu.getResource(),
                ES49IfMustUseNamedCastChecker.PREF_ENABLE_REINTERPRET_QUICKFIX);
    }
}
