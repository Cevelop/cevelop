package com.cevelop.codeanalysator.core.visitor.shared;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCatchHandler;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTRangeBasedForStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPField;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPVariable;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class AlwaysInitializeAnObjectVisitor extends CodeAnalysatorVisitor {

    public AlwaysInitializeAnObjectVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarations = true;
    }

    @Override
    public int visit(final IASTDeclaration declaration) {
        if (!isHighestPriorityRuleForNode(declaration)) {
            return PROCESS_CONTINUE;
        }
        if (declaration instanceof IASTSimpleDeclaration) {
            for (IASTDeclarator declarator : ((IASTSimpleDeclaration) declaration).getDeclarators()) {
                IBinding nameBinding = declarator.getName().resolveBinding();

                if (declarator.getInitializer() == null && !isRuleSuppressedForNode(declarator) && nameBinding instanceof ICPPVariable &&
                    !(declaration.getParent() instanceof ICPPASTRangeBasedForStatement) && !(declaration
                            .getParent() instanceof ICPPASTCatchHandler) && !(nameBinding instanceof ICPPField)) {

                    reportRuleForNode(declarator, "Always initialize an object");
                }
            }
        }

        return PROCESS_CONTINUE;
    }
}
