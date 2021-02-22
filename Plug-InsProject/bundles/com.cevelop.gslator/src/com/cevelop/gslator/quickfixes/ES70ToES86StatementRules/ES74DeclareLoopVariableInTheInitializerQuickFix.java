package com.cevelop.gslator.quickfixes.ES70ToES86StatementRules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTNullStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.core.resources.IMarker;

import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.BaseQuickFix;
import com.cevelop.gslator.quickfixes.utils.ASTFactory;
import com.cevelop.gslator.utils.ASTHelper;


public class ES74DeclareLoopVariableInTheInitializerQuickFix extends BaseQuickFix {

    @Override
    public String getLabel() {
        return Rule.ES74 + ": Add a variable declaration";
    }

    @Override
    public boolean isApplicable(IMarker marker) {
        if (!super.isApplicable(marker)) {
            return false;
        }
        final IASTNode markedNode = getMarkedNode(marker);
        if (markedNode == null) {
            return false;
        }
        IASTTranslationUnit translatioUnit = markedNode.getTranslationUnit();
        IASTNode forStatement = markedNode.getParent();
        if (!(forStatement instanceof IASTForStatement)) {
            return false;
        }

        IASTName loopVariable = ASTHelper.getLoopVariable((IASTForStatement) forStatement);
        if (loopVariable == null) {
            return false;
        }

        IBinding loopBinding = loopVariable.resolveBinding();
        IASTName[] variableReferences = translatioUnit.getReferences(loopBinding);

        return isValidReferences(variableReferences);
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        IASTForStatement forStatement = (IASTForStatement) markedNode.getParent();
        IASTTranslationUnit translationUnit = forStatement.getTranslationUnit();

        IASTName loopVariable = ASTHelper.getLoopVariable(forStatement);

        IASTName[] declarations = translationUnit.getDeclarationsInAST(loopVariable.resolveBinding());
        IASTName[] references = translationUnit.getReferences(loopVariable.resolveBinding());

        List<IASTName> usages = new ArrayList<>();
        ArrayUtil.addAll(usages, declarations);
        ArrayUtil.addAll(usages, references);

        deleteBindingUsages(usages, hRewrite);
        IASTDeclSpecifier declSpec = null;
        if (declarations.length > 0) {
            declSpec = ((IASTSimpleDeclaration) declarations[declarations.length - 1].getParent().getParent()).getDeclSpecifier();
        }

        IASTNode rightSide = getRightSide(usages, markedNode);

        IASTForStatement newForStatement = forStatement.copy(CopyStyle.withLocations);
        IASTDeclarationStatement declstate = ASTFactory.newDeclarationStatement(loopVariable.copy(CopyStyle.withLocations), declSpec, rightSide);
        newForStatement.setInitializerStatement(declstate);

        hRewrite.replace(forStatement, newForStatement, null);
    }

    private void deleteBindingUsages(List<IASTName> usages, ASTRewrite hRewrite) {
        for (IASTName name : usages) {
            if (!(ASTHelper.isForLoopStatement(name))) {
                IASTStatement declStatement = (IASTStatement) name.getParent().getParent().getParent();
                hRewrite.remove(declStatement, null);
            }
        }
    }

    private IASTNode getRightSide(List<IASTName> names, IASTNode markedNode) {
        if (markedNode instanceof IASTNullStatement) {
            IASTNode rightSide = null;
            for (IASTName name : names) {
                if (ASTHelper.isForLoopStatement(name)) {
                    return rightSide;
                }
                IASTNode parent = name.getParent();
                if (parent instanceof IASTIdExpression) {
                    IASTNode[] expressionChildren = parent.getParent().getChildren();
                    if (expressionChildren.length >= 2) {
                        rightSide = expressionChildren[1].copy(CopyStyle.withLocations);
                    }
                } else {
                    IASTNode[] declaratorChildren = name.getParent().getChildren();
                    if (declaratorChildren.length >= 2) {
                        rightSide = declaratorChildren[1];
                    }
                }
            }
            return rightSide;
        }

        if (markedNode instanceof IASTExpressionStatement) {
            IASTExpressionStatement expressionStatement = (IASTExpressionStatement) markedNode;
            IASTExpression expression = ASTHelper.getChildExpression(expressionStatement.getExpression());
            if (expression instanceof IASTBinaryExpression) {
                return ((IASTBinaryExpression) expression).getOperand2().copy(CopyStyle.withLocations);
            }
        }
        return null;
    }

    private boolean isValidReferences(final IASTName[] references) {
        for (IASTName name : references) {
            if (!(ASTHelper.isForLoopStatement(name)) && !(isLeftSideReference(name))) {
                return false;
            }
        }
        return true;
    }

    private boolean isLeftSideReference(IASTName name) {
        IASTIdExpression idExpression = (IASTIdExpression) name.getParent();
        IASTNode parent = idExpression.getParent();
        if (parent instanceof IASTBinaryExpression) {
            IASTBinaryExpression binaryExpression = (IASTBinaryExpression) name.getParent().getParent();
            if (binaryExpression.getOperand1().equals(idExpression)) {
                return true;
            }
        }
        return false;
    }
}
