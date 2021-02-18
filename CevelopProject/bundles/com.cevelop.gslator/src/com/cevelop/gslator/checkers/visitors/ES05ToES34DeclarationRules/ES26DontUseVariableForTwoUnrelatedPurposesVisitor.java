package com.cevelop.gslator.checkers.visitors.ES05ToES34DeclarationRules;

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

import com.cevelop.gslator.CCGlator;
import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.BaseVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.utils.ASTHelper;


@SuppressWarnings("restriction")
public class ES26DontUseVariableForTwoUnrelatedPurposesVisitor extends BaseVisitor {

    private final int ALLOWED_INITIALIZATIONS = 2;

    public ES26DontUseVariableForTwoUnrelatedPurposesVisitor(BaseChecker checker) {
        super(checker);
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
                if (!nodeHasNoIgnoreAttribute(this, getAttributeOwner(name))) return PROCESS_CONTINUE;
                if (ASTHelper.isForLoopStatement(name)) return PROCESS_CONTINUE;
                if (usedInitializations > ALLOWED_INITIALIZATIONS) {
                    checker.reportProblem(ProblemId.P_ES26, name);
                } else {
                    IASTStatement insideIfOrSwitch = getIfOrSwitchStatement(name);
                    if (insideIfOrSwitch != null && !(insideIfOrSwitch.equals(declaratorInsideIfOrSwitch))) {
                        usedInitializations--;
                    }
                    usedInitializations++;
                }
            }
        }
        return super.visit(declaration);
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
            CCGlator.getDefault().logException(e);
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
                            IASTExpression rhsOperand = binaryExpression.getOperand2();
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
}
