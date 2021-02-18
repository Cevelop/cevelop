package com.cevelop.codeanalysator.autosar.visitor;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

import com.cevelop.codeanalysator.autosar.util.LambdaHelper;
import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class NoImplicitLambdaReturnTypeVisitor extends CodeAnalysatorVisitor {

    public NoImplicitLambdaReturnTypeVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitExpressions = true;
    }

    @Override
    public int visit(IASTExpression expr) {
        if (expr instanceof ICPPASTLambdaExpression) {
            ICPPASTLambdaExpression lambda = (ICPPASTLambdaExpression) expr;

            if (!isReturnTypeExplicitlySpecified(lambda) && !hasVoidReturnType(lambda)) {
                reportRuleForNode(lambda);
            }
        }

        return super.visit(expr);
    }

    private boolean isReturnTypeExplicitlySpecified(ICPPASTLambdaExpression lambda) {
        ICPPASTFunctionDeclarator declarator = lambda.getDeclarator();
        if (declarator == null) {
            return false;
        }

        IASTTypeId trailingReturnType = declarator.getTrailingReturnType();
        if (trailingReturnType == null) {
            return false;
        }

        IASTDeclSpecifier declSpec = trailingReturnType.getDeclSpecifier();
        if (declSpec instanceof IASTSimpleDeclSpecifier) {
            IASTSimpleDeclSpecifier simpleDeclSpec = (IASTSimpleDeclSpecifier) declSpec;
            return simpleDeclSpec.getType() != IASTSimpleDeclSpecifier.t_decltype_auto;
        }
        return true;
    }

    @SuppressWarnings("restriction")
    private boolean hasVoidReturnType(ICPPASTLambdaExpression lambda) {
        IType returnType = LambdaHelper.getLambdaReturnType(lambda);
        return SemanticUtil.isVoidType(returnType);
    }
}
