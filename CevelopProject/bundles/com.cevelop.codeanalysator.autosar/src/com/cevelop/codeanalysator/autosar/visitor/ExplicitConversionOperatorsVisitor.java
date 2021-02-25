package com.cevelop.codeanalysator.autosar.visitor;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConversionName;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.util.ASTHelper;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class ExplicitConversionOperatorsVisitor extends CodeAnalysatorVisitor {

    public ExplicitConversionOperatorsVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarations = true;
    }

    @Override
    public int visit(IASTDeclaration decl) {
        if (decl instanceof IASTFunctionDefinition) {
            if (violatesRule((IASTFunctionDefinition) decl)) {
                reportRuleForNode(decl);
            }
        }
        return super.visit(decl);
    }

    private boolean violatesRule(IASTFunctionDefinition funcDef) {
        return isConversionOperator(funcDef) && !isExplicit(funcDef);
    }

    private boolean isConversionOperator(IASTFunctionDefinition funcDef) {
        IASTFunctionDeclarator funcDecl = funcDef.getDeclarator();
        IASTName name = funcDecl.getName();
        return name instanceof ICPPASTConversionName;
    }

    private boolean isExplicit(IASTFunctionDefinition funcDef) {
        return ASTHelper.isExplicit(funcDef);
    }
}
