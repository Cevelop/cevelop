package com.cevelop.gslator.quickfixes.ES70ToES86StatementRules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.BaseQuickFix;
import com.cevelop.gslator.quickfixes.utils.ASTFactory;
import com.cevelop.gslator.utils.ASTHelper;


public class ES75AvoidDoStatementsQuickFix extends BaseQuickFix {

    @Override
    public String getLabel() {
        return Rule.ES75 + ": change to normal while statement";
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        if (markedNode instanceof IASTDoStatement) {
            IASTDoStatement dostmt = (IASTDoStatement) markedNode;

            IASTNode[] rewritedata = getDeclarationAndWhileStatementToReplaceDoStatement(dostmt, dostmt);
            hRewrite.insertBefore(dostmt.getParent(), dostmt, rewritedata[0], null);

            hRewrite.replace(dostmt, rewritedata[1], null);
        }
    }

    private static String findFreeName(IASTNode scopePoint) {
        IASTNode node = scopePoint;
        IScope scope = ASTHelper.getNextParentScope(node);
        if (scope == null) return "firstRun";

        List<String> names = getNameListFromScope(scope, "firstRun", scopePoint);
        if (!names.contains("firstRun")) return "firstRun";

        int i = 0;
        do {
            i++;
            names.addAll(getNameListFromScope(scope, "firstRun" + i, scopePoint));
        } while (names.contains("firstRun" + i));
        return "firstRun" + i;
    }

    private static List<String> getNameListFromScope(IScope scope, String name, IASTNode scopePoint) {
        List<String> names = new ArrayList<>();
        IBinding[] firstRuns = scope.find(name, scopePoint.getTranslationUnit());
        if (firstRuns != null) {
            for (IBinding iBinding : firstRuns) {
                names.add(iBinding.getName());
            }
        }
        return names;
    }

    public static IASTNode[] getDeclarationAndWhileStatementToReplaceDoStatement(IASTDoStatement dostmt, IASTNode scopePoint) {
        IASTNode[] ret = new IASTNode[2];
        IASTName firstrunName = ASTFactory.newName(findFreeName(scopePoint));

        ret[0] = ASTFactory.newDeclarationStatement(firstrunName, ASTFactory.newSimpleDeclSpec(Kind.eBoolean), ASTFactory.newLiteralExpression(
                IASTLiteralExpression.lk_true, "true"));
        ret[1] = getNewWhileStatement(dostmt, firstrunName);
        return ret;
    }

    private static IASTWhileStatement getNewWhileStatement(IASTDoStatement dostmt, IASTName firstrunName) {
        // get new condition
        IASTExpression oldcond = ASTFactory.newUnaryExpression(IASTUnaryExpression.op_bracketedPrimary, dostmt.getCondition().copy(
                CopyStyle.withLocations));
        IASTBinaryExpression condition = ASTFactory.newBinaryExpression(IASTBinaryExpression.op_logicalOr, ASTFactory.newIdExpression(firstrunName),
                oldcond);

        // get new body
        ArrayList<IASTStatement> bodystatements = new ArrayList<>();

        // get new body - update firstrun variable
        IASTBinaryExpression unsetFirstRun = ASTFactory.newBinaryExpression(IASTBinaryExpression.op_assign, ASTFactory.newIdExpression(firstrunName),
                ASTFactory.newLiteralExpression(IASTLiteralExpression.lk_false, "false"));
        IASTExpressionStatement unsetFirstRunStmt = ASTFactory.newExpressionStatement(unsetFirstRun);

        bodystatements.add(unsetFirstRunStmt);

        // get new body - add old statements
        IASTStatement oldbody = dostmt.getBody();
        if (oldbody instanceof IASTCompoundStatement) {
            for (IASTNode iastNode : oldbody.getChildren()) {
                bodystatements.add((IASTStatement) iastNode.copy(CopyStyle.withLocations));
            }
        } else {
            bodystatements.add(oldbody.copy(CopyStyle.withLocations));
        }

        IASTCompoundStatement body = ASTFactory.newCompoundStatement(bodystatements);

        // generate and replace while statement
        IASTWhileStatement whilestmt = ASTFactory.newWhileStatement(condition, body);
        return whilestmt;
    }

}
