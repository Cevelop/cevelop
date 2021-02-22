package com.cevelop.codeanalysator.cppcore.visitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeleteExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExpressionList;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.index.IIndex;

import ch.hsr.ifs.iltis.cpp.core.resources.CProjectUtil;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.util.ASTHelper;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;
import com.cevelop.codeanalysator.cppcore.visitor.util.NameVisitor;


public class DestructorWithMissingDeleteStatementsVisitor extends DestructorHasNoBodyVisitor {

    public DestructorWithMissingDeleteStatementsVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    /* BEGIN GSLATOR */
    @Override
    public int visit(final IASTDeclSpecifier declSpec) {
        if (declSpec instanceof ICPPASTCompositeTypeSpecifier) {
            if (hasGslOwners(declSpec) && !hasNoDestructor() && !destructorBodyEmpty() && !isRuleSuppressedForNode(destructor)) {
                final String errorText = destructorMissingDeleteStatements(declSpec);
                if (!errorText.isEmpty()) {
                    reportRuleForNode(ASTHelper.getFunctionDeclaratorFromDeclarationOrDefinition(destructor), errorText);
                }
            }
        }
        return PROCESS_CONTINUE;
    }

    protected String destructorMissingDeleteStatements(final IASTDeclSpecifier declSpec) {
        final List<IASTSimpleDeclaration> gslOwners = ASTHelper.collectGslOwners(ASTHelper.collectMemberVariables(struct));

        List<String> names = listOfMembersToListOfNames(gslOwners);
        names = removePresentDelStmts(names);

        if (!names.isEmpty()) {
            return createMissingDelStmts(names);
        }
        return "";
    }

    private String createMissingDelStmts(final List<String> names) {
        final StringBuffer buf = new StringBuffer();
        for (final String name : names) {
            buf.append(" " + name);
        }
        return buf.toString();
    }

    private List<String> removePresentDelStmts(final List<String> names) {
        final ICPPASTCompoundStatement body = getBody();
        for (final IASTStatement statement : body.getStatements()) {
            for (final IASTName name : getNamesFromDeleteStatement(statement)) {
                names.remove(name.toString());
            }
        }
        return names;
    }

    private ICPPASTCompoundStatement getBody() {
        if (destructor instanceof IASTSimpleDeclaration) {
            return (ICPPASTCompoundStatement) getImplFromDeclaration((IASTSimpleDeclaration) destructor).getBody();
        } else if (destructor instanceof ICPPASTFunctionDefinition) {
            return (ICPPASTCompoundStatement) ((IASTFunctionDefinition) destructor).getBody();
        }
        return null;
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

    public List<String> listOfMembersToListOfNames(final List<IASTSimpleDeclaration> memberVars) {
        final List<String> namesOfMemberVars = new ArrayList<>();
        for (final IASTSimpleDeclaration memberVar : memberVars) {
            final StringBuffer buf = new StringBuffer();
            for (final IASTDeclarator decl : memberVar.getDeclarators()) {
                buf.append(decl.getName());
            }
            namesOfMemberVars.add(buf.toString());
        }
        return namesOfMemberVars;
    }

    public IASTName getNameFromDeleteExpression(final ICPPASTDeleteExpression expression) {
        final NameVisitor visitor = new NameVisitor();
        expression.accept(visitor);
        return visitor.getNames().get(0);
    }

    public List<IASTName> getNamesFromDeleteStatement(final IASTStatement statement) {
        final List<IASTName> names = new ArrayList<>();
        if (statement instanceof IASTExpressionStatement) {
            final IASTNode[] children = statement.getChildren();
            if (children.length == 1 && children[0] instanceof ICPPASTDeleteExpression) {
                names.add(getNameFromDeleteExpression((ICPPASTDeleteExpression) children[0]));
            } else if (children.length == 1 && children[0] instanceof ICPPASTExpressionList) {
                for (final IASTNode expression : children[0].getChildren()) {
                    names.add(getNameFromDeleteExpression((ICPPASTDeleteExpression) expression));
                }
            }
        }
        return names;
    }
    /* END GSLATOR */
}
