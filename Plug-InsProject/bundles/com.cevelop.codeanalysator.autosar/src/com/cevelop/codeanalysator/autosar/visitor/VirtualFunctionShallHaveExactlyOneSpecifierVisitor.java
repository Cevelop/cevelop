package com.cevelop.codeanalysator.autosar.visitor;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;

import com.cevelop.codeanalysator.autosar.util.ContextFlagsHelper;
import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.util.VirtualHelper;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class VirtualFunctionShallHaveExactlyOneSpecifierVisitor extends CodeAnalysatorVisitor {

    public VirtualFunctionShallHaveExactlyOneSpecifierVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarators = true;
    }

    @Override
    public int visit(IASTDeclarator decl) {
        if (violatesRule(decl)) {
            reportRuleForNode(decl, createContextFlagsString(decl));
        }
        return super.visit(decl);
    }

    private boolean violatesRule(IASTDeclarator decl) {
        if (!(decl instanceof ICPPASTFunctionDeclarator)) {
            return false;
        }
        ICPPASTFunctionDeclarator declarator = (ICPPASTFunctionDeclarator) decl;
        int numberOfVirtSpecifiers = VirtualHelper.countNumberOfVirtSpecifiers(declarator);
        if (numberOfVirtSpecifiers > 1) {
            return true;
        } else if (numberOfVirtSpecifiers == 0) {
            return VirtualHelper.isVirtualMethod(declarator);
        }
        return false;
    }

    private String createContextFlagsString(IASTDeclarator decl) {
        StringBuffer contextFlagsBuffer = new StringBuffer();
        Optional<ICPPMethod> declaredMethod = getDeclaredMethod(decl);
        declaredMethod.ifPresent(method -> {
            if (method.isPureVirtual()) {
                contextFlagsBuffer.append(ContextFlagsHelper.VirtualFunctionShallHaveExactlyOneSpecifierContextFlagPureVirtual);
            }
            if (!VirtualHelper.overridesVirtualMethod(method) && method.isFinal()) {
                contextFlagsBuffer.append(ContextFlagsHelper.VirtualFunctionShallHaveExactlyOneSpecifierContextFlagIntroducingFinal);
            }
        });
        return contextFlagsBuffer.toString();
    }

    private Optional<ICPPMethod> getDeclaredMethod(IASTDeclarator decl) {
        return Optional.ofNullable(decl.getName()) //
                .map(IASTName::resolveBinding) //
                .filter(ICPPMethod.class::isInstance) //
                .map(ICPPMethod.class::cast);
    }
}
