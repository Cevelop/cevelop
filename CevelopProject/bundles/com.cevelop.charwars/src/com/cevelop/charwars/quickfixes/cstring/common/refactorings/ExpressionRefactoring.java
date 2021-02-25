package com.cevelop.charwars.quickfixes.cstring.common.refactorings;

import java.util.EnumSet;

import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;
import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.IBetterFactory;

import com.cevelop.charwars.asttools.ASTAnalyzer;
import com.cevelop.charwars.asttools.CheckAnalyzer;
import com.cevelop.charwars.constants.StdString;
import com.cevelop.charwars.quickfixes.cstring.common.refactorings.Context.Kind;
import com.cevelop.charwars.utils.ComplementaryNodeFactory;
import com.cevelop.charwars.utils.analyzers.BEAnalyzer;
import com.cevelop.charwars.utils.analyzers.BoolAnalyzer;
import com.cevelop.charwars.utils.analyzers.LiteralAnalyzer;
import com.cevelop.charwars.utils.analyzers.UEAnalyzer;


public class ExpressionRefactoring extends Refactoring {

    private static final IBetterFactory FACTORY = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();

    private enum Transformation {
        SIZE, EMPTY, NOT_EMPTY, DEREFERENCED, MODIFIED, ARRAY_SUBSCRIPTION, INDEX_CALCULATION, ALIAS_COMPARISON
    }

    private static final String TRANSFORMATION = "TRANSFORMATION";

    public ExpressionRefactoring(EnumSet<Kind> contextKinds) {
        setContextKinds(contextKinds);
    }

    private void makeApplicable(IASTNode nodeToReplace, Transformation transformation) {
        super.makeApplicable(nodeToReplace);
        config.put(TRANSFORMATION, transformation);
    }

    @Override
    protected void prepareConfiguration(IASTIdExpression idExpression, Context context) {
        if (ASTAnalyzer.isStringLengthCalculation(idExpression)) {
            //sizeof(str) / sizeof(*str) - 1 -> str.size()
            //sizeof str / sizeof *str -1 -> str.size()
            IASTNode nodeToReplace = idExpression.getParent();
            while (!BEAnalyzer.isSubtraction(nodeToReplace)) {
                nodeToReplace = nodeToReplace.getParent();
            }
            makeApplicable(nodeToReplace, Transformation.SIZE);
        } else if (CheckAnalyzer.isCheckedForEmptiness(idExpression, true)) {
            //!*str -> str.empty()
            //*str == 0 -> str.empty()
            //if modified: !*str -> !str[str_pos]
            makeApplicable(BoolAnalyzer.getEnclosingBoolean(idExpression), Transformation.EMPTY);
        } else if (CheckAnalyzer.isCheckedForEmptiness(idExpression, false)) {
            //if(*str) -> if(!str.empty())
            //*str != 0 -> !str.empty()
            //if modified: *str -> str[str_pos]
            makeApplicable(BoolAnalyzer.getEnclosingBoolean(idExpression), Transformation.NOT_EMPTY);
        } else if (ASTAnalyzer.isDereferencedToChar(idExpression)) {
            //*str -> str[0]
            //*(str) -> str[0]
            //*(str+n) -> str[n]
            //if modified: *str -> str[str_pos]
            IASTNode nodeToReplace = idExpression.getParent();
            while (!UEAnalyzer.isDereferenceExpression(nodeToReplace)) {
                nodeToReplace = nodeToReplace.getParent();
            }
            makeApplicable(nodeToReplace, Transformation.DEREFERENCED);
        } else if (ASTAnalyzer.modifiesCharPointer(idExpression)) {
            //++str -> ++str_pos
            //*++str -> str[++str_pos]
            //str++ -> str_pos++
            //*str++ -> str[str_pos++]
            //str += n -> str_pos += n
            makeApplicable(idExpression, Transformation.MODIFIED);
        } else if (ASTAnalyzer.isArraySubscriptExpression(idExpression) && context.isOffset(idExpression)) {
            //str[0] -> str[str_pos]
            //str[1] -> str[str_pos + 1]
            makeApplicable(idExpression.getParent(), Transformation.ARRAY_SUBSCRIPTION);
        } else if (context.getKind() == Kind.Modified_Alias && ASTAnalyzer.isIndexCalculation(idExpression)) {
            //ptr - str -> ptr
            IASTNode nodeToReplace = idExpression.getParent();
            if (UEAnalyzer.isBracketExpression(nodeToReplace.getParent())) {
                nodeToReplace = nodeToReplace.getParent();
            }
            makeApplicable(nodeToReplace, Transformation.INDEX_CALCULATION);
        } else if (context.getKind() == Kind.Modified_Alias && !ASTAnalyzer.isLValueInAssignment(idExpression) && (CheckAnalyzer.isNodeComparedToNull(
                idExpression) || CheckAnalyzer.isNodeComparedToStrlen(idExpression))) {
                    makeApplicable(BoolAnalyzer.getEnclosingBoolean(idExpression), Transformation.ALIAS_COMPARISON);
                }
    }

    @Override
    protected IASTNode getReplacementNode(IASTIdExpression idExpression, Context context) {
        IASTName stringVarName = context.createStringVarName();
        switch ((Transformation) config.get(TRANSFORMATION)) {
        case SIZE:
            return FACTORY.newMemberFunctionCallExpression(stringVarName, StdString.SIZE);
        case EMPTY:
            if (context.isOffset(idExpression)) {
                IASTIdExpression subscript = context.createOffsetVarIdExpression();
                IASTArraySubscriptExpression arraySubscription = FACTORY.newArraySubscriptExpression(context.createStringVarIdExpression(),
                        subscript);
                return FACTORY.newLogicalNotExpression(arraySubscription);
            } else {
                return FACTORY.newMemberFunctionCallExpression(stringVarName, StdString.EMPTY);
            }
        case NOT_EMPTY:
            if (context.isOffset(idExpression)) {
                IASTIdExpression subscript = context.createOffsetVarIdExpression();
                return FACTORY.newArraySubscriptExpression(context.createStringVarIdExpression(), subscript);
            } else {
                IASTExpression emptyCall = FACTORY.newMemberFunctionCallExpression(stringVarName, StdString.EMPTY);
                return FACTORY.newLogicalNotExpression(emptyCall);
            }
        case DEREFERENCED:
            IASTExpression subscript;
            if (context.isOffset(idExpression)) {
                subscript = context.createOffsetVarIdExpression();
            } else {
                subscript = FACTORY.newIntegerLiteral(0);
            }

            if (BEAnalyzer.isAddition(idExpression.getParent())) {
                IASTExpression otherOperand = BEAnalyzer.getOtherOperand(idExpression);

                if (context.isOffset(idExpression)) {
                    subscript = FACTORY.newPlusExpression(subscript, otherOperand);
                } else {
                    subscript = otherOperand;
                }
            }
            return FACTORY.newArraySubscriptExpression(context.createStringVarIdExpression(), subscript);
        case MODIFIED:
            IASTNode parent = idExpression.getParent();
            if (UEAnalyzer.isIncrementation(parent)) {
                if (UEAnalyzer.isDereferenceExpression(parent.getParent())) {
                    config.put(NODE_TO_REPLACE, parent.getParent());
                    int operator = ((IASTUnaryExpression) parent).getOperator();
                    IASTExpression subscriptExpr = FACTORY.newUnaryExpression(operator, context.createOffsetVarIdExpression());
                    return FACTORY.newArraySubscriptExpression(context.createStringVarIdExpression(), subscriptExpr);
                }
            }
            return context.createOffsetVarIdExpression();
        case ARRAY_SUBSCRIPTION:
            IASTArraySubscriptExpression oldArraySubscriptExpression = (IASTArraySubscriptExpression) idExpression.getParent();
            IASTExpression oldArraySubscript = (IASTExpression) oldArraySubscriptExpression.getArgument();
            IASTExpression newArraySubscript = context.createOffsetVarIdExpression();

            if (!LiteralAnalyzer.isZero(oldArraySubscript)) {
                newArraySubscript = FACTORY.newPlusExpression(newArraySubscript, oldArraySubscript);
            }
            return FACTORY.newArraySubscriptExpression(context.createStringVarIdExpression(), newArraySubscript);
        case INDEX_CALCULATION:
            return context.createOffsetVarIdExpression();
        case ALIAS_COMPARISON:
            IASTExpression lhs = context.createOffsetVarIdExpression();
            IASTExpression rhs = ComplementaryNodeFactory.newNposExpression(context.getStringType());
            boolean isEqual;
            if (CheckAnalyzer.isNodeComparedToNull(idExpression)) {
                isEqual = CheckAnalyzer.isNodeComparedToNull(idExpression, true);
            } else {
                isEqual = CheckAnalyzer.isNodeComparedToStrlen(idExpression, true);
            }
            return FACTORY.newEqualityComparison(lhs, rhs, isEqual);
        default:
            return null;
        }
    }
}
