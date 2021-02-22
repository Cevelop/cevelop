package com.cevelop.codeanalysator.autosar.visitor;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVirtSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.util.VirtualHelper;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class DoNotOmitVirtSpecifierVisitor extends CodeAnalysatorVisitor {

    public DoNotOmitVirtSpecifierVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarators = true;
    }

    @Override
    public int visit(IASTDeclarator decl) {
        if (violatesRule(decl)) {
            reportRuleForNode(decl);
        }
        return super.visit(decl);
    }

    private boolean violatesRule(IASTDeclarator decl) {
        if (!(decl instanceof ICPPASTFunctionDeclarator)) {
            return false;
        }
        ICPPASTFunctionDeclarator declarator = (ICPPASTFunctionDeclarator) decl;
        IASTName name = declarator.getName();
        if (name == null) {
            return false;
        }
        IBinding binding = name.resolveBinding();
        if (!(binding instanceof ICPPMethod)) {
            return false;
        }
        ICPPMethod method = (ICPPMethod) binding;
        if (!VirtualHelper.overridesVirtualMethod(method)) {
            return false;
        }
        ICPPASTVirtSpecifier[] virtSpecs = declarator.getVirtSpecifiers();
        return virtSpecs.length < 1;
    }
}
