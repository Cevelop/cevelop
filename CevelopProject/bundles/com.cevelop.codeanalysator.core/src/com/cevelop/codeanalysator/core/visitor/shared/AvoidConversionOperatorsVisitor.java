package com.cevelop.codeanalysator.core.visitor.shared;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConversionName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.util.ASTHelper;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class AvoidConversionOperatorsVisitor extends CodeAnalysatorVisitor {

    public AvoidConversionOperatorsVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarators = true;
    }

    @Override
    public int visit(final IASTDeclarator declarator) {
        if (!isHighestPriorityRuleForNode(declarator)) {
            return PROCESS_CONTINUE;
        }
        /* BEGIN GSLATOR */
        if (declarator instanceof ICPPASTFunctionDeclarator && !ASTHelper.isExplicit(declarator)) {
            ICPPASTFunctionDeclarator funcDecl = (ICPPASTFunctionDeclarator) declarator;
            IASTName name = funcDecl.getName();
            if (name instanceof ICPPASTConversionName) {
                reportRuleForNode(funcDecl);
            }
        }
        return PROCESS_CONTINUE;
        /* END GSLATOR */
    }
}
