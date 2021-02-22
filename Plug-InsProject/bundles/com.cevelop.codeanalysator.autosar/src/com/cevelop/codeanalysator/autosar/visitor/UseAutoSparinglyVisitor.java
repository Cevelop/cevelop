package com.cevelop.codeanalysator.autosar.visitor;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTRangeBasedForStatement;

import com.cevelop.codeanalysator.autosar.util.AutoHelper;
import com.cevelop.codeanalysator.autosar.util.ContextFlagsHelper;
import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class UseAutoSparinglyVisitor extends CodeAnalysatorVisitor {

    public UseAutoSparinglyVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarations = true;
    }

    @Override
    public int visit(IASTDeclaration decl) {
        if (decl instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) decl;

            if (AutoHelper.isAutoDeclaringVariables(simpleDeclaration) && !AutoHelper
                    .isInitializingVariablesWithFunctionCallOrInitializerOfNonFundamentalType(simpleDeclaration)) {
                String contextFlagsString = createContextFlagsString(simpleDeclaration);
                reportRuleForNode(simpleDeclaration, contextFlagsString);
            }
        } else if (decl instanceof ICPPASTFunctionDefinition) {
            ICPPASTFunctionDefinition functionDefinition = (ICPPASTFunctionDefinition) decl;

            if (AutoHelper.isAutoDeclaringFunction(functionDefinition) && !AutoHelper.isDeclaringTemplateFunctionWithTrailingReturnTypeSyntax(
                    functionDefinition)) {
                String contextFlagsString = createContextFlagsString();
                reportRuleForNode(functionDefinition, contextFlagsString);
            }
        }
        return super.visit(decl);
    }

    private String createContextFlagsString(IASTSimpleDeclaration simpleDeclaration) {
        return isControlDeclaration(simpleDeclaration) ? ContextFlagsHelper.UseAutoSparinglyContextFlagControlDeclaration : "";
    }

    private boolean isControlDeclaration(IASTSimpleDeclaration simpleDeclaration) {
        IASTNode parent = simpleDeclaration.getParent();
        if (parent instanceof IASTIfStatement) {
            return true;
        } else if (parent instanceof IASTSwitchStatement) {
            return true;
        } else if (parent instanceof IASTWhileStatement) {
            return true;
        } else if (parent instanceof IASTForStatement) {
            return true;
        } else if (parent instanceof ICPPASTRangeBasedForStatement) {
            return true;
        }
        return false;
    }

    private String createContextFlagsString() {
        return "";
    }
}
