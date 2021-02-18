package com.cevelop.codeanalysator.autosar.visitor;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;

import com.cevelop.codeanalysator.autosar.util.ContextFlagsHelper;
import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.util.VirtualHelper;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class DoNotIntroduceVirtualFunctionInFinalClassVisitor extends CodeAnalysatorVisitor {

    public DoNotIntroduceVirtualFunctionInFinalClassVisitor(Rule rule, RuleReporter ruleReporter) {
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
        if (!(decl instanceof IASTFunctionDeclarator)) return false;
        IASTFunctionDeclarator functionDeclarator = (IASTFunctionDeclarator) decl;

        IASTNode parent = functionDeclarator.getParent().getParent();
        if (parent instanceof ICPPASTCompositeTypeSpecifier) {
            ICPPASTCompositeTypeSpecifier typeSpec = (ICPPASTCompositeTypeSpecifier) parent;
            if (!typeSpec.isFinal()) {
                return false;
            }
            return VirtualHelper.isVirtualMethod((ICPPASTFunctionDeclarator) functionDeclarator);
        }
        return false;
    }

    private String createContextFlagsString(IASTDeclarator decl) {
        StringBuffer contextFlagsBuffer = new StringBuffer();
        Optional<ICPPMethod> declaredMethod = getDeclaredMethod(decl);
        declaredMethod.ifPresent(method -> {
            if (method.isPureVirtual()) {
                contextFlagsBuffer.append(ContextFlagsHelper.DoNotIntroduceVirtualFunctionInFinalClassContextFlagPureVirtual);
            }
            if (!VirtualHelper.overridesVirtualMethod(method) && method.isVirtual()) {
                contextFlagsBuffer.append(ContextFlagsHelper.DoNotIntroduceVirtualFunctionInFinalClassContextFlagIntroducingVirtual);
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
