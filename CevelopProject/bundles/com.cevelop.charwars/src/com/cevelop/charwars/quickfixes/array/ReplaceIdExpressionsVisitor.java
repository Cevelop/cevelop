package com.cevelop.charwars.quickfixes.array;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IArrayType;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.charwars.asttools.ASTAnalyzer;
import com.cevelop.charwars.asttools.ASTModifier;
import com.cevelop.charwars.constants.StdArray;
import com.cevelop.charwars.utils.analyzers.BEAnalyzer;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;
import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.IBetterFactory;


public class ReplaceIdExpressionsVisitor extends ASTVisitor {

    private static final IBetterFactory nodeFactory = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();

    private ASTRewrite rewrite;
    private IASTName   arrayName;

    public ReplaceIdExpressionsVisitor(ASTRewrite rewrite, IASTName arrayName) {
        this.shouldVisitExpressions = true;
        this.rewrite = rewrite;
        this.arrayName = arrayName;
    }

    @Override
    public int leave(IASTExpression expression) {
        if (expression instanceof IASTIdExpression) {
            IASTIdExpression idExpression = (IASTIdExpression) expression;
            if (ASTAnalyzer.isSameName(idExpression.getName(), arrayName)) {
                if (ASTAnalyzer.isArraySubscriptExpression(idExpression)) {
                    // keep Array Subscript Expressions
                } else if (ASTAnalyzer.isArrayLengthCalculation(idExpression)) {
                    // TODO: very shaky!! only special case addressed
                    IASTNode currentNode = idExpression;
                    while (currentNode != null) {
                        currentNode = currentNode.getParent();
                        if (BEAnalyzer.isDivision(currentNode)) break;
                    }
                    IASTFunctionCallExpression sizeCall = nodeFactory.newMemberFunctionCallExpression(arrayName, StdArray.SIZE);
                    ASTModifier.replace(currentNode, sizeCall, rewrite);
                } else if (ASTAnalyzer.isInSizeofExpression(idExpression)) {
                    IType type = idExpression.getExpressionType();
                    if (type instanceof IArrayType) {
                        IType arrayTypeType = ((IArrayType) type).getType();
                        IASTNode sizeOfExpression = idExpression.getParent().getParent();
                        if (arrayTypeType instanceof IBasicType && ((IBasicType) arrayTypeType).getKind() == IBasicType.Kind.eChar) {
                            IASTFunctionCallExpression sizecall = nodeFactory.newMemberFunctionCallExpression(arrayName, StdArray.SIZE);
                            ASTModifier.replace(sizeOfExpression, sizecall, rewrite);
                        }
                    }
                } else {
                    IASTFunctionCallExpression dataCall = nodeFactory.newMemberFunctionCallExpression(arrayName, StdArray.DATA);
                    ASTModifier.replace(idExpression, dataCall, rewrite);
                }
            }
        }
        return PROCESS_CONTINUE;
    }
}
