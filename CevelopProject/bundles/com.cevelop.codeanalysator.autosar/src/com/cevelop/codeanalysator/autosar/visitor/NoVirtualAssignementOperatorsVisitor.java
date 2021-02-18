package com.cevelop.codeanalysator.autosar.visitor;

import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTOperatorName;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.util.VirtualHelper;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class NoVirtualAssignementOperatorsVisitor extends CodeAnalysatorVisitor {

    private final static List<String> AssignementOperators = Arrays.asList( //
            "operator =", "operator +=", "operator -=", "operator *=", "operator /=", "operator %=", //
            "operator &=", "operator |=", "operator ^=", "operator <<=", "operator >>=");

    public NoVirtualAssignementOperatorsVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarators = true;
    }

    @Override
    public int visit(IASTDeclarator declarator) {
        if (violatesRule(declarator)) {
            reportRuleForNode(declarator);
        }
        return super.visit(declarator);
    }

    private boolean violatesRule(IASTDeclarator declarator) {
        if (!(declarator instanceof ICPPASTFunctionDeclarator)) return false;
        ICPPASTFunctionDeclarator funcDecl = (ICPPASTFunctionDeclarator) declarator;
        if (!VirtualHelper.isVirtualMethod(funcDecl)) return false;
        IASTName declName = funcDecl.getName();
        if (declName == null) return false;
        if (declName instanceof ICPPASTOperatorName) {
            ICPPASTOperatorName operatorName = (ICPPASTOperatorName) declName;
            return isAssignementOperator(operatorName);
        }
        return false;
    }

    /***
     * The class ICPPASTOperator name does not provide any means to find out the type of the operator.
     * Internally the operator is created by appending the operator (i.e *= or =) to the keyword operator.
     *
     * @param operatorName
     * @return
     */
    private boolean isAssignementOperator(ICPPASTOperatorName operatorName) {
        String name = operatorName.toString();
        return (AssignementOperators.contains(name));
    }
}
