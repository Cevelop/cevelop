package com.cevelop.gslator.checkers.visitors.ES40ToES64ExpressionRules;

import org.eclipse.cdt.core.dom.ast.IASTAttributeOwner;
import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeConstructorExpression;
import org.eclipse.core.resources.IResource;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.ES40ToES64ExpressionRules.ES49IfMustUseNamedCastChecker;
import com.cevelop.gslator.checkers.visitors.BaseVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;


public class ES49IfMustUseNamedCastVisitor extends BaseVisitor {

    public ES49IfMustUseNamedCastVisitor(BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitExpressions = true;
    }

    @Override
    public int visit(final IASTExpression expression) {
        IASTNode attributeOwner = expression;
        while (attributeOwner != null && !(attributeOwner instanceof IASTAttributeOwner)) {
            attributeOwner = attributeOwner.getParent();
        }
        if (!nodeHasNoIgnoreAttribute(this, attributeOwner)) {
            return super.visit(expression);
        }

        if (expression instanceof IASTCastExpression && isTraditionalCastEnabled(expression)) {
            IASTCastExpression cast = (IASTCastExpression) expression;

            if (ES50DontCastAwayConstVisitor.isConstCast(cast)) {
                return super.visit(expression);
            }

            if (cast.getOperator() == IASTCastExpression.op_cast) {
                reportProblem(expression);
            }

        } else if (expression instanceof ICPPASTSimpleTypeConstructorExpression && isTypeConstEnabled(expression) && expression
                .getChildren()[0] instanceof IASTSimpleDeclSpecifier && expression.getChildren()[1] instanceof ICPPASTConstructorInitializer &&
                   ((ICPPASTConstructorInitializer) expression.getChildren()[1]).getArguments().length > 0) {
                       reportProblem(expression);
                   }

        return super.visit(expression);
    }

    private void reportProblem(final IASTExpression expression) {
        checker.reportProblem(ProblemId.P_ES49, expression);
    }

    private boolean isTraditionalCastEnabled(IASTNode node) {
        IResource res = node.getTranslationUnit().getOriginatingTranslationUnit().getResource();
        return (boolean) checker.getPreference(res, ES49IfMustUseNamedCastChecker.PREF_ENABLE_TRADITIONALCAST);
    }

    private boolean isTypeConstEnabled(IASTNode node) {
        IResource res = node.getTranslationUnit().getOriginatingTranslationUnit().getResource();
        return (boolean) checker.getPreference(res, ES49IfMustUseNamedCastChecker.PREF_ENABLE_TYPECONST_WITH_CONSTINIT);
    }
}
