package com.cevelop.charwars.asttools;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeIdExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.internal.core.dom.rewrite.ASTModificationStore;
import org.eclipse.cdt.internal.core.dom.rewrite.changegenerator.ChangeGeneratorWriterVisitor;
import org.eclipse.cdt.internal.core.dom.rewrite.commenthandler.NodeCommentMap;

import com.cevelop.charwars.constants.Constants;
import com.cevelop.charwars.constants.Function;
import com.cevelop.charwars.utils.analyzers.BEAnalyzer;
import com.cevelop.charwars.utils.analyzers.FunctionAnalyzer;
import com.cevelop.charwars.utils.analyzers.LiteralAnalyzer;
import com.cevelop.charwars.utils.analyzers.TypeAnalyzer;
import com.cevelop.charwars.utils.analyzers.UEAnalyzer;
import com.cevelop.charwars.utils.visitors.IdExpressionsCollector;
import com.cevelop.charwars.utils.visitors.NameOccurrenceChecker;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;


@SuppressWarnings("restriction")
public class ASTAnalyzer {

    public static boolean isFunctionDefinitionParameterDeclaration(IASTParameterDeclaration declaration) {
        return declaration.getParent().getParent() instanceof IASTFunctionDefinition;
    }

    public static boolean isLValueInAssignment(IASTIdExpression idExpression) {
        IASTNode parent = idExpression.getParent();
        return BEAnalyzer.isAssignment(parent) && BEAnalyzer.isOp1(idExpression);
    }

    public static boolean isArraySubscriptExpression(IASTIdExpression idExpression) {
        IASTNode parent = idExpression.getParent();
        if (parent instanceof IASTArraySubscriptExpression) {
            IASTArraySubscriptExpression asExpression = (IASTArraySubscriptExpression) parent;
            return asExpression.getArrayExpression() == idExpression;
        }
        return false;
    }

    public static boolean isArrayLengthCalculation(IASTIdExpression idExpression) {
        for (IASTNode cn = idExpression; cn != null && !UEAnalyzer.isDereferenceExpression(cn); cn = cn.getParent()) {
            if (BEAnalyzer.isDivision(cn)) {
                return isSizeOfDivision(cn);
            }
        }
        return false;
    }

    public static boolean isStringLengthCalculation(IASTIdExpression idExpression) {
        if (UEAnalyzer.isDereferenceExpression(idExpression.getParent())) return false;

        IASTNode currentNode = idExpression;
        while (currentNode != null && !BEAnalyzer.isSubtraction(currentNode)) {
            currentNode = currentNode.getParent();
        }

        if (currentNode == null) return false;

        IASTNode minuend = BEAnalyzer.getOperand1(currentNode);
        IASTNode subtrahend = BEAnalyzer.getOperand2(currentNode);
        return isSizeOfDivision(minuend) && LiteralAnalyzer.isInteger(subtrahend, 1);
    }

    public static boolean isInSizeofExpression(IASTIdExpression idExpression) {
        return idExpression != null && idExpression.getParent() != null && UEAnalyzer.isSizeofExpression(idExpression.getParent().getParent());
    }

    private static boolean isSizeOfDivision(IASTNode node) {
        // TODO: very shaky special case, does not work in general!
        if (BEAnalyzer.isDivision(node)) {
            IASTNode dividend = BEAnalyzer.getOperand1(node);
            IASTNode divisor = BEAnalyzer.getOperand2(node);
            return UEAnalyzer.isSizeofExpression(dividend) && (UEAnalyzer.isSizeofExpression(divisor) || isSizeOfTypeIdExpression(divisor));
        }
        return false;
    }

    private static boolean isSizeOfTypeIdExpression(IASTNode node) {
        if (node instanceof IASTTypeIdExpression) {
            IASTTypeIdExpression typeIdExpression = (IASTTypeIdExpression) node;
            return typeIdExpression.getOperator() == IASTTypeIdExpression.op_sizeof;
        }
        return false;
    }

    public static IASTStatement getStatement(IASTNode node) {
        IASTNode result = node;
        while (result != null && !(result instanceof IASTStatement)) {
            result = result.getParent();
        }
        return (IASTStatement) result;
    }

    public static boolean isConversionToCharPointer(IASTNode node) {
        return isConversionToCharPointer(node, true) || isConversionToCharPointer(node, false);
    }

    public static boolean isConversionToCharPointer(IASTNode node, boolean isConst) {
        if (isConst) {
            return FunctionAnalyzer.isCallToMemberFunction(node, Function.C_STR);
        } else {
            if (UEAnalyzer.isAddressOperatorExpression(node)) {
                IASTNode operand = UEAnalyzer.getOperand(node);
                if (UEAnalyzer.isDereferenceExpression(operand)) {
                    IASTNode dereferencedNode = UEAnalyzer.getOperand(operand);
                    return FunctionAnalyzer.isCallToMemberFunction(dereferencedNode, Function.BEGIN);
                }
            }
        }
        return false;
    }

    public static IASTNode getEnclosingBlock(IASTNode node) {
        IASTNode block = node;
        while (block != null && !(block instanceof IASTCompoundStatement || block instanceof ICPPASTNamespaceDefinition ||
                                  block instanceof IASTCompositeTypeSpecifier)) {
            block = block.getParent();
        }
        return block == null ? node.getTranslationUnit() : block;
    }

    public static boolean isNameAvailable(String name, IASTNode nodeInBlock) {
        IASTNode block = getEnclosingBlock(nodeInBlock);
        IASTNode blockParent = block.getParent();

        if (blockParent instanceof IASTFunctionDefinition) {
            IASTFunctionDefinition funcDefinition = (IASTFunctionDefinition) blockParent;
            ICPPASTFunctionDeclarator funcDeclarator = (ICPPASTFunctionDeclarator) funcDefinition.getDeclarator();
            for (IASTParameterDeclaration parameterDeclaration : funcDeclarator.getParameters()) {
                if (parameterDeclaration.getDeclarator().getName().toString().equals(name)) {
                    return false;
                }
            }
        }

        NameOccurrenceChecker visitor = new NameOccurrenceChecker(name);
        block.accept(visitor);
        return !visitor.hasNameOccurred();
    }

    public static IASTNode extractStdStringArg(IASTNode node) {
        IASTNode result = node;

        if (isConversionToCharPointer(node, true)) { //str.c_str()
            IASTFunctionCallExpression c_strCall = (IASTFunctionCallExpression) node;
            IASTFieldReference fieldReference = (IASTFieldReference) c_strCall.getFunctionNameExpression();
            result = fieldReference.getFieldOwner();
        } else if (isConversionToCharPointer(node, false)) { //&str.begin()
            IASTFunctionCallExpression beginCall = (IASTFunctionCallExpression) UEAnalyzer.getOperand(UEAnalyzer.getOperand(node));
            IASTFieldReference fieldReference = (IASTFieldReference) beginCall.getFunctionNameExpression();
            result = fieldReference.getFieldOwner();
        }

        return result;
    }

    public static boolean isLeftShiftExpressionToStdCout(IASTNode node) {
        if (BEAnalyzer.isLeftShiftExpression(node) && node instanceof ICPPASTBinaryExpression) {
            IASTExpression leftOperand = BEAnalyzer.getOperand1(node);
            return isStdCout(leftOperand) || isLeftShiftExpressionToStdCout(leftOperand);
        }
        return false;
    }

    private static boolean isStdCout(IASTNode node) {
        if (node instanceof IASTIdExpression) {
            IASTName name = ((IASTIdExpression) node.getOriginalNode()).getName();
            String nameStr = name.getRawSignature();
            return nameStr.equals(Constants.STD_COUT) || nameStr.equals(Constants.COUT);
        }
        return false;
    }

    public static IASTNode getMarkedNode(IASTTranslationUnit ast, int offset, int length) {
        return ast.getNodeSelector(null).findNode(offset, length);
    }

    public static IASTName getResultVariableName(IASTStatement[] statements) {
        if (statements.length == 0) {
            return null;
        }

        IASTStatement lastStatement = statements[statements.length - 1];
        if (lastStatement instanceof IASTReturnStatement) {
            IASTExpression returnValue = ((IASTReturnStatement) lastStatement).getReturnValue();
            if (returnValue instanceof IASTIdExpression) {
                return ((IASTIdExpression) returnValue).getName();
            }
        }

        return null;
    }

    public static IASTDeclarationStatement getVariableDeclaration(IASTName name, IASTStatement[] statements) {
        for (IASTStatement statement : statements) {
            if (statement instanceof IASTDeclarationStatement) {
                IASTDeclarationStatement declarationStatement = (IASTDeclarationStatement) statement;
                IASTDeclaration declaration = declarationStatement.getDeclaration();
                if (declaration instanceof IASTSimpleDeclaration) {
                    IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) declaration;
                    for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
                        if (isSameName(declarator.getName(), name)) {
                            return declarationStatement;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean isDereferencedToChar(IASTNode node) {
        IASTNode parent = node.getParent();
        if (UEAnalyzer.isBracketExpression(parent) || BEAnalyzer.isAddition(parent)) {
            return isDereferencedToChar(parent);
        }
        return UEAnalyzer.isDereferenceExpression(parent);
    }

    public static IASTStatement getTopLevelParentStatement(IASTNode node) {
        IASTStatement lastStatement = null;
        for (IASTNode cn = node; cn != null && !(cn instanceof IASTFunctionDefinition); cn = cn.getParent()) {
            if (cn instanceof IASTStatement && !(cn instanceof IASTCompoundStatement)) {
                lastStatement = (IASTStatement) cn;
            }
        }
        return lastStatement;
    }

    public static boolean modifiesCharPointer(IASTIdExpression idExpression) {
        IASTNode parent = idExpression.getParent();
        boolean isLValue = isLValueInAssignment(idExpression) && BEAnalyzer.getOperand2(parent) instanceof IASTBinaryExpression;
        boolean isPlusAssigned = BEAnalyzer.isPlusAssignment(parent) && BEAnalyzer.isOp1(idExpression);
        boolean isIncremented = UEAnalyzer.isIncrementation(parent);
        return isLValue || isPlusAssigned || isIncremented;
    }

    public static boolean isIndexCalculation(IASTIdExpression idExpression) {
        IASTNode parent = idExpression.getParent();
        if (BEAnalyzer.isSubtraction(parent)) {
            return BEAnalyzer.isOp1(idExpression) && isConversionToCharPointer(BEAnalyzer.getOtherOperand(idExpression), true);
        }
        return false;
    }

    public static boolean isSameName(IASTName name1, IASTName name2) {
        return name1.resolveBinding().equals(name2.resolveBinding());
    }

    public static boolean isOffsettedCString(IASTExpression expr) {
        IASTIdExpression idExpression = getStdStringIdExpression(expr);
        if (idExpression == null) return false;

        IASTNode cstrCall = idExpression.getParent().getParent();
        boolean isCstrCall = isConversionToCharPointer(cstrCall, true);

        IASTNode currentNode = cstrCall;
        while (currentNode != expr) {
            currentNode = currentNode.getParent();
            if (!BEAnalyzer.isAddition(currentNode) && !BEAnalyzer.isSubtraction(currentNode) && !UEAnalyzer.isBracketExpression(currentNode)) {
                return false;
            }
        }
        return isCstrCall;
    }

    public static IASTIdExpression getStdStringIdExpression(IASTExpression expr) {
        IdExpressionsCollector collector = new IdExpressionsCollector();
        expr.accept(collector);
        List<IASTIdExpression> idExpressions = collector.getIdExpressions();
        if (idExpressions.size() == 1) {
            IASTIdExpression idExpression = idExpressions.get(0);
            IASTIdExpression originalIdExpression = (IASTIdExpression) idExpression.getOriginalNode();
            boolean isStdStringType = TypeAnalyzer.isStdStringType(originalIdExpression.getExpressionType());
            if (isStdStringType) return idExpression;
        }
        return null;
    }

    public static IASTExpression extractPointerOffset(IASTExpression expr) {
        IASTExpression offset = expr.copy(CopyStyle.withLocations);
        IASTIdExpression idExpression = getStdStringIdExpression(offset);
        if (idExpression == null || idExpression == offset) {
            return null;
        }

        IASTNode cstrCall = idExpression.getParent().getParent();
        IASTNode parent = cstrCall.getParent();
        IASTExpression remainingOperand;
        if (BEAnalyzer.isSubtraction(parent) && BEAnalyzer.isOp1(cstrCall)) {
            remainingOperand = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newNegatedExpression(BEAnalyzer.getOperand2(parent));
        } else if (parent instanceof IASTBinaryExpression) {
            remainingOperand = BEAnalyzer.getOtherOperand(cstrCall);
        } else {
            return null;
        }

        if (parent == offset) {
            return remainingOperand;
        } else {
            ASTModifier.replaceNode(parent, remainingOperand);
            return offset;
        }
    }

    public static String nodeToString(IASTNode node) {
        ChangeGeneratorWriterVisitor writer = new ChangeGeneratorWriterVisitor(new ASTModificationStore(), new NodeCommentMap());
        node.accept(writer);
        return writer.toString();
    }
}
