package com.cevelop.codeanalysator.autosar.visitor;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.util.VirtualHelper;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class DoNotHideMemberFunctionsVisitor extends CodeAnalysatorVisitor {

    public DoNotHideMemberFunctionsVisitor(Rule rule, RuleReporter ruleReporter) {
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

    private boolean violatesRule(IASTDeclarator declarator) {
        if (!(declarator instanceof ICPPASTFunctionDeclarator)) {
            return false;
        }

        IASTDeclarator decl = VirtualHelper.findInnermostDeclarator(declarator);
        IASTName name = decl.getName();
        if (name != null) {
            IBinding binding = name.resolveBinding();
            if (binding instanceof ICPPMethod) {
                ICPPMethod method = (ICPPMethod) binding;

                boolean shadows = false;
                try {
                    shadows = testForShadow(method);
                } catch (DOMException e) {
                    e.printStackTrace();
                }
                return shadows;
            }
        }
        return false;
    }

    private boolean testForShadow(ICPPMethod method) throws DOMException {
        if (method instanceof ICPPConstructor || method.isDestructor() || method.isPureVirtual()) {
            return false;
        }
        ICPPClassType[] bases = VirtualHelper.getAllBases(method.getClassOwner());
        if (bases.length == 0) {
            return false;
        }

        return VirtualHelper.shadows(method);
    }
}
