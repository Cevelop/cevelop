package com.cevelop.codeanalysator.cppcore.visitor;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.index.IIndex;

import ch.hsr.ifs.iltis.cpp.core.resources.CProjectUtil;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.util.ASTHelper;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class NoDestructorVisitor extends CodeAnalysatorVisitor {

    protected ICPPASTCompositeTypeSpecifier struct;
    protected IASTDeclaration               destructor;

    public NoDestructorVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclSpecifiers = true;
    }

    /* BEGIN GSLATOR */
    @Override
    public int visit(final IASTDeclSpecifier declSpec) {
        if (declSpec instanceof ICPPASTCompositeTypeSpecifier && !isRuleSuppressedForNode(declSpec)) {

            if (hasGslOwners(declSpec) && hasNoDestructor()) {
                reportRuleForNode(struct.getName());
            }
        }
        return PROCESS_CONTINUE;
    }

    protected boolean hasNoDestructor() {
        destructor = ASTHelper.getFirstSpecialMemberFunction(struct, ASTHelper.SpecialFunction.DefaultDestructor);
        if (destructor instanceof IASTSimpleDeclaration) {
            return getImplFromDeclaration((IASTSimpleDeclaration) destructor) == null;
        }

        return !(destructor instanceof ICPPASTFunctionDefinition);
    }

    protected boolean hasGslOwners(final IASTDeclSpecifier declSpec) {
        struct = (ICPPASTCompositeTypeSpecifier) declSpec;
        final List<IASTSimpleDeclaration> gslOwners = ASTHelper.collectGslOwners(ASTHelper.collectMemberVariables(struct));

        return gslOwners.size() > 0;
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
