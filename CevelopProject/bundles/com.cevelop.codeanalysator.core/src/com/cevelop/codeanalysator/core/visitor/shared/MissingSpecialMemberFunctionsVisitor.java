package com.cevelop.codeanalysator.core.visitor.shared;

import java.util.Arrays;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.util.ASTHelper;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class MissingSpecialMemberFunctionsVisitor extends CodeAnalysatorVisitor {

    private static final long RULE_OF_ZERO_SPECIAL_FUNCTIONS_COUNT = 0;
    private static final long RULE_OF_FIVE_SPECIAL_FUNCTIONS_COUNT = 5;

    public MissingSpecialMemberFunctionsVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclSpecifiers = true;
    }

    @Override
    public int visit(final IASTDeclSpecifier declSpec) {
        if (!isHighestPriorityRuleForNode(declSpec)) {
            return PROCESS_CONTINUE;
        }
        if (declSpec instanceof ICPPASTCompositeTypeSpecifier) {
            ICPPASTCompositeTypeSpecifier compositeTypeSpec = (ICPPASTCompositeTypeSpecifier) declSpec;
            long numOfSpecialFunctionsFound = Arrays.stream(compositeTypeSpec.getMembers()) //
                    .filter(member -> member instanceof ICPPASTFunctionDefinition || member instanceof IASTSimpleDeclaration) //
                    .filter(this::isSpecialFunctionExceptDefaultConstructor) //
                    .count();
            if (!(numOfSpecialFunctionsFound == RULE_OF_ZERO_SPECIAL_FUNCTIONS_COUNT ||
                  numOfSpecialFunctionsFound == RULE_OF_FIVE_SPECIAL_FUNCTIONS_COUNT)) {
                reportRuleForNode(compositeTypeSpec.getName());
            }
        }
        return PROCESS_CONTINUE;
    }

    public boolean isSpecialFunctionExceptDefaultConstructor(final IASTDeclaration declaration) {
        return ASTHelper.isDefaultCopyAssignment(declaration) || ASTHelper.isDefaultCopyConstructor(declaration) || ASTHelper.isDefaultDestructor(
                declaration) || ASTHelper.isMoveConstructor(declaration) || ASTHelper.isMoveAssignment(declaration);
    }
}
