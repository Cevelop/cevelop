package com.cevelop.gslator.checkers.visitors.C30ToC37DestructorRules;

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

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.util.NameVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.infos.GslatorInfo;
import com.cevelop.gslator.utils.ASTHelper;


public class C31_03DestructorWithMissingDeleteStatementsVisitor extends C31_02DestructorHasNoBodyVisitor {

    public C31_03DestructorWithMissingDeleteStatementsVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    public int visit(final IASTDeclSpecifier declSpec) {
        if (declSpec instanceof ICPPASTCompositeTypeSpecifier) {
            if (hasGslOwners(declSpec) && !hasNoDestructor() && !destructorBodyEmpty() && nodeHasNoIgnoreAttribute(this, destructor)) {
                if (!destructorMissingDeleteStatements(declSpec).isEmpty()) {
                    checker.reportProblem(ProblemId.P_C31_03, ASTHelper.getFunctionDeclaratorFromDeclarationOrDefinition(destructor), new GslatorInfo(
                            destructorMissingDeleteStatements(declSpec)));
                }
            }
        }
        return super.visit(declSpec);
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

}
