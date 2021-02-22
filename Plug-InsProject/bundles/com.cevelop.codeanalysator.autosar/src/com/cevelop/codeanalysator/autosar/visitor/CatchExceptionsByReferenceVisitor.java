package com.cevelop.codeanalysator.autosar.visitor;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTReferenceOperator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCatchHandler;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


@SuppressWarnings("restriction")
public class CatchExceptionsByReferenceVisitor extends CodeAnalysatorVisitor {

    public CatchExceptionsByReferenceVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitStatements = true;
    }

    @Override
    public int visit(IASTStatement statement) {
        if (statement instanceof CPPASTCatchHandler) {
            if (violatesRule((CPPASTCatchHandler) statement)) {
                reportRuleForNode(statement);
            }
        }
        return super.visit(statement);
    }

    private boolean violatesRule(CPPASTCatchHandler catchHandler) {
        CPPASTSimpleDeclaration declaration = (CPPASTSimpleDeclaration) catchHandler.getDeclaration();
        if (declaration == null || declaration.getDeclSpecifier() instanceof CPPASTSimpleDeclSpecifier) {
            return false;
        }
        IASTDeclarator[] declarators = declaration.getDeclarators();
        IASTDeclarator decl = declarators[0];
        for (IASTPointerOperator operation : decl.getPointerOperators()) {
            if (operation instanceof ICPPASTReferenceOperator) {
                return false;
            }
        }
        return true;
    }

}
