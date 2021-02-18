package com.cevelop.codeanalysator.cppcore.visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.EScopeKind;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.internal.core.dom.parser.ASTQueries;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.util.ASTHelper;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class DontUseVariableForTwoUnrelatedPurposesVisitor extends CodeAnalysatorVisitor {

    /* BEGIN GSLATOR */
    private final int ALLOWED_INITIALIZATIONS = 2;

    public DontUseVariableForTwoUnrelatedPurposesVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarations = true;
    }

    @Override
    public int visit(final IASTDeclaration declaration) {
        if (!(declaration instanceof IASTSimpleDeclaration)) return PROCESS_CONTINUE;

        IASTSimpleDeclaration simpleDecl = (IASTSimpleDeclaration) declaration;
        IASTDeclarator[] declarators = simpleDecl.getDeclarators();
        for (IASTDeclarator declarator : declarators) {
            int usedInitializations = 0;

            if (declarator.getChildren().length >= 2) {
                usedInitializations++;
            }

            IBinding binding = declarator.getName().resolveBinding();
            if (skipNonLocalVariable(binding)) {
                return PROCESS_CONTINUE;
            }

            IASTTranslationUnit translationUnit = declarator.getTranslationUnit();
            IASTName[] declarations = translationUnit.getDeclarationsInAST(binding);
            List<IASTName> usages = new ArrayList<>();
            ArrayUtil.addAll(usages, declarations);
            usages.addAll(getLeftSideOnlyReferences(translationUnit.getReferences(binding)));
            IASTStatement declaratorInsideIfOrSwitch = getIfOrSwitchStatement(declarator.getName());

            for (IASTName name : usages) {
                if (isRuleSuppressedForNode(getAttributeOwner(name))) return PROCESS_CONTINUE;
                if (ASTHelper.isForLoopStatement(name)) return PROCESS_CONTINUE;
                if (usedInitializations > ALLOWED_INITIALIZATIONS) {
                    reportRuleForNode(name);
                } else {
                    IASTStatement insideIfOrSwitch = getIfOrSwitchStatement(name);
                    if (insideIfOrSwitch != null && !(insideIfOrSwitch.equals(declaratorInsideIfOrSwitch))) {
                        usedInitializations--;
                    }
                    usedInitializations++;
                }
            }
        }
        return PROCESS_CONTINUE;
    }

    private static boolean skipNonLocalVariable(IBinding binding) {
        if (!(binding instanceof IVariable)) {
            return false;
        }
        try {
            IScope scope = binding.getScope();
            EScopeKind scopeKind = scope.getKind();
            if (scopeKind != EScopeKind.eLocal) {
                return true;
            }
        } catch (DOMException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static IASTStatement getIfOrSwitchStatement(IASTName reference) {
        IASTNode parent = reference.getParent();
        while (!(parent instanceof IASTIfStatement) && !(parent instanceof IASTSwitchStatement) && parent != null) {
            parent = parent.getParent();
        }
        if (parent instanceof IASTIfStatement || parent instanceof IASTSwitchStatement) {
            return (IASTStatement) parent;
        }
        return null;
    }

    private static List<IASTName> getLeftSideOnlyReferences(IASTName[] references) {
        List<IASTName> leftSideReferences = new ArrayList<>();
        for (IASTName reference : references) {
            IASTExpression expression = getParentExpression(reference);
            if (expression instanceof IASTBinaryExpression) {
                IASTBinaryExpression binaryExpression = (IASTBinaryExpression) expression;
                int operator = binaryExpression.getOperator();
                if (IASTBinaryExpression.op_assign == operator) {
                    IASTExpression operand1 = binaryExpression.getOperand1();
                    if (operand1 instanceof IASTIdExpression) {
                        IASTIdExpression lhsIdExpression = (IASTIdExpression) operand1;
                        if (reference.equals(lhsIdExpression.getName())) {
                            IASTInitializerClause rhsOperand = binaryExpression.getInitOperand2();
                            boolean isUsedOnRight = Arrays.stream(references).anyMatch(name -> ASTQueries.isAncestorOf(rhsOperand, name));
                            if (!isUsedOnRight) {
                                leftSideReferences.add(reference);
                            }
                        }
                    }
                }
            }
        }
        return leftSideReferences;
    }

    private static IASTNode getAttributeOwner(IASTName name) {
        IASTNode parent = name.getParent();
        while (!(parent instanceof IASTSimpleDeclaration) && !(parent instanceof IASTExpressionStatement) && parent != null) {
            parent = parent.getParent();
        }
        return parent;
    }

    private static IASTExpression getParentExpression(IASTName name) {
        IASTNode parent = name.getParent();
        while (!(parent instanceof IASTBinaryExpression) && !(parent instanceof IASTUnaryExpression) && parent != null) {
            parent = parent.getParent();
        }
        return (IASTExpression) parent;
    }
    /* END GSLATOR */
}
