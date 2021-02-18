package com.cevelop.codeanalysator.cppcore.visitor;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.index.IIndex;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.util.ASTHelper;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;

import ch.hsr.ifs.iltis.cpp.core.resources.CProjectUtil;


public class DestructorHasNoBodyVisitor extends NoDestructorVisitor {

    public DestructorHasNoBodyVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    /* BEGIN GSLATOR */
    @Override
    public int visit(final IASTDeclSpecifier declSpec) {
        if (declSpec instanceof ICPPASTCompositeTypeSpecifier) {
            if (hasGslOwners(declSpec) && !hasNoDestructor() && destructorBodyEmpty() && !isRuleSuppressedForNode(destructor)) {
                reportRuleForNode(ASTHelper.getFunctionDeclaratorFromDeclarationOrDefinition(destructor));
            }
        }
        return PROCESS_CONTINUE;
    }

    protected boolean destructorBodyEmpty() {
        if (destructor instanceof IASTSimpleDeclaration) {
            return getImplFromDeclaration((IASTSimpleDeclaration) destructor).getBody() == null;
        }
        if (destructor instanceof ICPPASTFunctionDefinition) {
            return ((IASTFunctionDefinition) destructor).getBody() == null;
        }
        return false;
    }

    private ICPPASTFunctionDefinition getImplFromDeclaration(final IASTSimpleDeclaration decl) {
        final ASTHelper.SpecialFunction type = ASTHelper.getSpecialMemberFunctionType(decl);
        IASTName name = ASTHelper.getNameOfFunctionFromDeclaration(decl);//ASTHelper.getCompositeTypeSpecifier(decl).getName();
        if (name != null) {
            final List<IASTNode> nodes = ASTHelper.findNames(name.resolveBinding(), CProjectUtil.getCProject(CProjectUtil.getProject(decl)),
                    IIndex.FIND_DEFINITIONS);
            for (final IASTNode node : nodes) {
                final ICPPASTFunctionDefinition funcDef = ASTHelper.getFunctionDefinition(node);
                if (funcDef != null && ASTHelper.getSpecialMemberFunctionType(funcDef) == type) {
                    return funcDef;
                }
            }
        }
        return null;
    }

    /* END GSLATOR */
}
