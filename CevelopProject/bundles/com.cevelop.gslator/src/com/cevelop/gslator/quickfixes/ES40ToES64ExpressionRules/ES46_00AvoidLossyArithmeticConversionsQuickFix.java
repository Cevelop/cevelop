package com.cevelop.gslator.quickfixes.ES40ToES64ExpressionRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.gslator.charwarsstub.quickfixes.gsl.include.ProjectIncluder;
import com.cevelop.gslator.quickfixes.BaseQuickFix;
import com.cevelop.gslator.quickfixes.ES40ToES64ExpressionRules.utils.ES46QuickFixData;
import com.cevelop.gslator.utils.ASTHelper;


abstract public class ES46_00AvoidLossyArithmeticConversionsQuickFix extends BaseQuickFix {

    public abstract String getCastName();

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        ES46QuickFixData data = getdataForRewrite(markedNode);
        rewrite(data, hRewrite);
    }

    private ES46QuickFixData getdataForRewrite(IASTNode markedNode) {
        ES46QuickFixData data = null;
        //Function with IdExpression as Parameter
        if (markedNode instanceof IASTName && markedNode.getParent() instanceof IASTIdExpression && markedNode.getParent()
                .getParent() instanceof IASTFunctionCallExpression) {
            data = getRewriteDataFromFunctionParamName((IASTName) markedNode);
            //Function with CastExpression as Parameter
        } else if (markedNode instanceof IASTCastExpression) {
            data = getRewriteDataFromFunctionParameterCast((IASTCastExpression) markedNode);
            //Binary Expression (Assign) with IdExpression or CastExpression in right side
        } else if (markedNode instanceof IASTBinaryExpression) {
            data = getRewriteDataFromBinaryExpression(markedNode);
            //class org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeclarator
        } else if (markedNode instanceof IASTDeclarator) {
            data = getRewriteDataFromDeclarator((IASTDeclarator) markedNode);
        }
        if (data == null) {
            data = new ES46QuickFixData();
        }
        return data;
    }

    private ES46QuickFixData getRewriteDataFromFunctionParamName(IASTName markedNode) {
        ES46QuickFixData data = new ES46QuickFixData();
        data.setOld(markedNode.getParent());
        data.setSubject(markedNode.getParent());

        IASTFunctionCallExpression functionCall = getFunctionCallExpressionFromParam(markedNode);
        if (functionCall != null) {
            data.setType(getITypeFromFunctionCallExpression(functionCall, markedNode));
        }
        return data;
    }

    private IASTFunctionCallExpression getFunctionCallExpressionFromParam(IASTNode node) {
        while (!(node instanceof IASTFunctionCallExpression) && !(node instanceof IASTTranslationUnit)) {
            node = node.getParent();
        }
        if (node instanceof IASTFunctionCallExpression) {
            return (IASTFunctionCallExpression) node;
        }
        return null;
    }

    private IType getITypeFromFunctionCallExpression(IASTFunctionCallExpression functionCall, IASTName markedName) {
        return getITypeFromFunctionCallExpression(functionCall, markedName, null);
    }

    private IType getITypeFromFunctionCallExpression(IASTFunctionCallExpression functionCall, IASTCastExpression markedCast) {
        return getITypeFromFunctionCallExpression(functionCall, null, markedCast);
    }

    private IType getITypeFromFunctionCallExpression(IASTFunctionCallExpression functionCall, IASTName markedName, IASTCastExpression markedCast) {
        IASTFunctionDeclarator functionDeclarator = ASTHelper.getFunctionDeclaratorFromName(((IASTIdExpression) (functionCall.getChildren()[0]))
                .getName());

        if (functionDeclarator != null) {
            IASTInitializerClause[] params = functionCall.getArguments();
            Map<Integer, IType> paramsspec = ASTHelper.getFunctionArguments(functionDeclarator);
            int i = 0;
            for (IASTNode iastnode : params) {
                if (markedCast != null && iastnode.equals(markedCast)) {
                    break;
                }
                while (iastnode instanceof IASTUnaryExpression && ((IASTUnaryExpression) iastnode)
                        .getOperator() == IASTUnaryExpression.op_bracketedPrimary) {
                    iastnode = iastnode.getChildren()[0];
                }
                if (markedName != null && ((IASTIdExpression) iastnode).getName().resolveBinding().equals(markedName.resolveBinding())) {
                    break;
                }
                i++;
            }
            return paramsspec.get(i);
        }
        return null;
    }

    private ES46QuickFixData getRewriteDataFromFunctionParameterCast(IASTCastExpression markedNode) {
        ES46QuickFixData data = new ES46QuickFixData();
        data.setOld(markedNode);
        IASTNode subjectNode = markedNode.getChildren()[1];
        while (subjectNode instanceof IASTUnaryExpression && ((IASTUnaryExpression) subjectNode)
                .getOperator() == IASTUnaryExpression.op_bracketedPrimary) {
            subjectNode = subjectNode.getChildren()[0];
        }
        data.setSubject(subjectNode);
        IASTFunctionCallExpression functionCall = getFunctionCallExpressionFromParam(markedNode);
        data.setType(getITypeFromFunctionCallExpression(functionCall, markedNode));
        return data;
    }

    private ES46QuickFixData getRewriteDataFromBinaryExpression(IASTNode markedNode) {
        ES46QuickFixData data = new ES46QuickFixData();
        data.setOld(markedNode.getChildren()[1]);

        if (data.getOld() instanceof IASTCastExpression) {
            for (IASTNode iastnode : data.getOld().getChildren()) {
                while (iastnode instanceof IASTUnaryExpression && ((IASTUnaryExpression) iastnode)
                        .getOperator() == IASTUnaryExpression.op_bracketedPrimary) {
                    iastnode = iastnode.getChildren()[0];
                }
                if (iastnode instanceof IASTIdExpression) {
                    data.setSubject(iastnode);
                }
            }
        } else {
            data.setSubject(data.getOld());
        }

        List<String> list = new ArrayList<>();
        ASTHelper.getTypeFromExpressionElement(markedNode.getChildren()[1], list);

        if (list.size() == 1) {
            data.setType(list.get(0));
        } else {
            data.setType(ASTHelper.getTypeFromExpressionElement(markedNode.getChildren()[0]));
        }
        return data;
    }

    private ES46QuickFixData getRewriteDataFromDeclarator(IASTDeclarator markedNode) {
        ES46QuickFixData data = new ES46QuickFixData();
        IASTNode[] childs = markedNode.getChildren();
        for (IASTNode iastNode : childs) {
            if (iastNode instanceof IASTEqualsInitializer && iastNode.getChildren().length == 1) {
                data.setOld(iastNode.getChildren()[0]);
                data.setSubject(data.getOld());
                data.setType(ASTHelper.getTypeFromBinding(markedNode.getName().resolveBinding(), false));
            }
        }
        return data;
    }

    private void rewrite(ES46QuickFixData data, ASTRewrite hRewrite) {
        if (data.allSet()) {
            IASTTranslationUnit tu = data.getOld().getTranslationUnit();
            hRewrite.replace(data.getOld(), data.getNew(getCastName()), null);
            if (!hasInclude("gsl", tu) && !hasInclude("gsl.h", tu)) {
                ProjectIncluder.createAndLinkProject(tu);
                newHeaders.add("gsl.h");
            }
        }
    }
}
