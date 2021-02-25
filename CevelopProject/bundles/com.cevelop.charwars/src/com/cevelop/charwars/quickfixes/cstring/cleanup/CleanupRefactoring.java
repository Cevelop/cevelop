package com.cevelop.charwars.quickfixes.cstring.cleanup;

import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.charwars.asttools.ASTAnalyzer;
import com.cevelop.charwars.asttools.DeclaratorAnalyzer;
import com.cevelop.charwars.constants.Function;
import com.cevelop.charwars.utils.analyzers.BEAnalyzer;


public abstract class CleanupRefactoring {

    protected IASTStatement              oldStatement;
    protected IASTFunctionCallExpression functionCall;
    protected Function                   inFunction;
    protected Function                   outFunction;
    protected ASTRewrite                 rewrite;
    protected IASTIdExpression           str;
    protected IASTNode                   secondArg;

    public CleanupRefactoring(IASTFunctionCallExpression functionCall, Function inFunction, Function outFunction, ASTRewrite rewrite) {
        this.oldStatement = ASTAnalyzer.getStatement(functionCall);
        this.functionCall = functionCall;
        this.inFunction = inFunction;
        this.outFunction = outFunction;
        this.rewrite = rewrite;
        this.str = (IASTIdExpression) ASTAnalyzer.extractStdStringArg(functionCall.getArguments()[0]);
        this.secondArg = ASTAnalyzer.extractStdStringArg(functionCall.getArguments()[1]);
    }

    public void perform() {
        if (optimizedRefactoringPossible()) {
            performOptimized();
        } else {
            performNormal();
        }
    }

    protected boolean optimizedRefactoringPossible() {
        final IASTStatement oldStatement = ASTAnalyzer.getStatement(functionCall);
        if (!(oldStatement instanceof IASTDeclarationStatement)) {
            return false;
        }
        final IASTDeclarationStatement declarationStatement = (IASTDeclarationStatement) oldStatement;
        final IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) declarationStatement.getDeclaration();
        final IASTName varName = simpleDeclaration.getDeclarators()[0].getName();
        final boolean hasPtrReturnType = !(inFunction == Function.STRCSPN || inFunction == Function.STRSPN);
        final IASTNode block = ASTAnalyzer.getEnclosingBlock(declarationStatement);
        final OptimizationCheckerVisitor visitor = new OptimizationCheckerVisitor(varName, hasPtrReturnType);
        block.accept(visitor);
        return visitor.isOptimizationPossible();
    }

    protected abstract void performNormal();

    protected abstract void performOptimized();

    protected String getResultVarName(IASTFunctionCallExpression functionCall) {
        IASTNode cn = functionCall;
        while (cn != null && !(cn instanceof IASTDeclarator) && !BEAnalyzer.isAssignment(cn)) {
            cn = cn.getParent();
        }

        String resultVarName = null;
        if (cn instanceof IASTDeclarator) {
            final IASTDeclarator declarator = (IASTDeclarator) cn;
            resultVarName = declarator.getName().toString();
        } else if (BEAnalyzer.isAssignment(cn)) {
            IASTExpression lvalue = BEAnalyzer.getOperand1(cn);
            if (lvalue instanceof IASTArraySubscriptExpression) {
                final IASTArraySubscriptExpression arraySubscriptExpression = (IASTArraySubscriptExpression) lvalue;
                lvalue = arraySubscriptExpression.getArrayExpression();
            }

            if (lvalue instanceof IASTIdExpression) {
                final IASTIdExpression idExpression = (IASTIdExpression) lvalue;
                resultVarName = idExpression.getName().toString();
            } else if (lvalue instanceof IASTFieldReference) {
                final IASTFieldReference fieldReference = (IASTFieldReference) lvalue;
                resultVarName = fieldReference.getFieldName().toString();
            }
        }
        return resultVarName;
    }

    protected String getPosVarName(IASTFunctionCallExpression functionCall) {
        String posVarName = getResultVarName(functionCall) + "_pos";

        if (!ASTAnalyzer.isNameAvailable(posVarName, functionCall)) {
            int counter = 2;
            while (!ASTAnalyzer.isNameAvailable(posVarName + counter, functionCall)) {
                counter++;
            }
            posVarName += counter;
        }

        return posVarName;
    }

    protected IASTFunctionCallExpression getFunctionCallNode(IASTStatement statement) {
        if (statement instanceof IASTDeclarationStatement) {
            final IASTDeclarationStatement declStatement = (IASTDeclarationStatement) statement;
            final IASTSimpleDeclaration declaration = (IASTSimpleDeclaration) declStatement.getDeclaration();
            for (final IASTDeclarator declarator : declaration.getDeclarators()) {
                final IASTInitializerClause initializerClause = DeclaratorAnalyzer.getInitializerClause(declarator);
                if (initializerClause instanceof IASTFunctionCallExpression) {
                    return (IASTFunctionCallExpression) initializerClause;
                }
            }
        } else if (statement instanceof IASTExpressionStatement) {
            final IASTExpressionStatement exprStatement = (IASTExpressionStatement) statement;
            final IASTExpression op2 = BEAnalyzer.getOperand2(exprStatement.getExpression());
            if (op2 instanceof IASTFunctionCallExpression) {
                return (IASTFunctionCallExpression) op2;
            }
            if (op2 instanceof IASTCastExpression) {
                final IASTNode child1 = op2.getChildren()[1];
                if (child1 instanceof IASTFunctionCallExpression) {
                    return (IASTFunctionCallExpression) child1;
                }
                if (child1 instanceof IASTUnaryExpression && child1.getChildren()[0] instanceof IASTFunctionCallExpression) {
                    return (IASTFunctionCallExpression) child1.getChildren()[0];
                }
            }
        }
        return null;
    }
}
