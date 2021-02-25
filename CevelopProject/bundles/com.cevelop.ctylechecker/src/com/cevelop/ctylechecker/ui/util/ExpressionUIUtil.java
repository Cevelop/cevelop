package com.cevelop.ctylechecker.ui.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.cevelop.ctylechecker.domain.ExpressionType;
import com.cevelop.ctylechecker.domain.IExpression;
import com.cevelop.ctylechecker.domain.IGroupExpression;
import com.cevelop.ctylechecker.domain.ISingleExpression;
import com.cevelop.ctylechecker.domain.OrderPriority;
import com.cevelop.ctylechecker.domain.ResolutionHint;
import com.cevelop.ctylechecker.service.IExpressionService;
import com.cevelop.ctylechecker.service.factory.ResolutionFactory;
import com.cevelop.ctylechecker.service.types.CtylecheckerRuntime;


public class ExpressionUIUtil {

    private static Image                    groupImage        = CtylecheckerRuntime.imageDescriptorFromPlugin("/resources/expression_group_img.jpg")
            .createImage();
    private static Image                    expressionImage   = CtylecheckerRuntime.imageDescriptorFromPlugin("/resources/expression_img.jpg")
            .createImage();
    private static final String             ALL               = "ALL";
    private static final String             ANY               = "ANY";
    private static final IExpressionService expressionService = CtylecheckerRuntime.getInstance().getRegistry().getExpressionService();

    public static void populateItem(TreeItem item, ISingleExpression expression) {
        item.setText(ExpressionItemArg.NAME, expression.getName());
        item.setText(ExpressionItemArg.REGEX, expression.getExpression());
        item.setText(ExpressionItemArg.MATCH, expression.shouldMatch().toString());
        item.setText(ExpressionItemArg.RESOLUTION, expression.getResolution().getClass().getSimpleName());
        item.setText(ExpressionItemArg.ARGUMENT, expression.getArgument());
        item.setText(ExpressionItemArg.HINT, expression.getHint().name());
        item.setText(ExpressionItemArg.ORDER, expression.getOrder().name());
    }

    public static ISingleExpression extractExpression(TreeItem item) {
        ISingleExpression newExpression = expressionService.createExpression(item.getText(ExpressionItemArg.NAME), item.getText(
                ExpressionItemArg.REGEX), Boolean.valueOf(item.getText(ExpressionItemArg.MATCH)));
        newExpression.setResolution(ResolutionFactory.createResolution(item.getText(ExpressionItemArg.RESOLUTION), newExpression));
        newExpression.setArgument(item.getText(ExpressionItemArg.ARGUMENT));
        newExpression.setHint(ResolutionHint.valueOf(item.getText(ExpressionItemArg.HINT)));
        newExpression.setOrder(OrderPriority.valueOf(item.getText(ExpressionItemArg.ORDER)));
        return newExpression;
    }

    public static IExpression extractExpressionGroup(TreeItem item) {
        IExpression group = expressionService.createExpressionGroup(item.getText(ExpressionItemArg.NAME), ExpressionUIUtil.parseMatchValue(item
                .getText(ExpressionItemArg.MATCH)));
        group.setHint(ResolutionHint.valueOf(item.getText(ExpressionItemArg.HINT)));
        if (item.getItemCount() > 0) {
            for (TreeItem subItem : item.getItems()) {
                if (subItem.getData(ItemDataInfo.DATA_EXPRESSION_TYPE_KEY).equals(ItemDataInfo.DATA_EXPRESSION_GROUP_TYPE_VALUE)) {
                    expressionService.addToExpressionGroup(group, extractExpressionGroup(subItem));
                } else {
                    expressionService.addToExpressionGroup(group, extractExpression(subItem));
                }
            }
        }
        return group;
    }

    public static List<IExpression> readExpressionFields(Tree expressionsTree) {
        List<IExpression> expressions = new ArrayList<>();
        for (TreeItem item : expressionsTree.getItems()) {
            readPreparedExpressionsItems(item, expressions);
        }
        return expressions;
    }

    public static void initGroupItem(Tree parent, IGroupExpression group) {
        TreeItem groupItem = new TreeItem(parent, SWT.NONE);
        initGroupItem(group, groupItem);
    }

    private static void initGroupItem(TreeItem parent, IGroupExpression group) {
        TreeItem groupItem = new TreeItem(parent, SWT.NONE);
        initGroupItem(group, groupItem);
    }

    private static void initGroupItem(IGroupExpression group, TreeItem groupItem) {
        groupItem.setText(ExpressionItemArg.NAME, group.getName());
        groupItem.setText(ExpressionItemArg.MATCH, ExpressionUIUtil.convertMatchValue(group.isEnabled()));
        groupItem.setText(ExpressionItemArg.HINT, group.getHint().name());
        groupItem.setData(ItemDataInfo.DATA_EXPRESSION_TYPE_KEY, ItemDataInfo.DATA_EXPRESSION_GROUP_TYPE_VALUE);
        groupItem.setImage(groupImage);
        for (IExpression expression : expressionService.getExpressions(group)) {
            if (expression.getType().equals(ExpressionType.SINGLE)) {
                initItem(groupItem, (ISingleExpression) expression);
            } else {
                initGroupItem(groupItem, (IGroupExpression) expression);
            }
        }
    }

    public static void initItem(Tree parent, ISingleExpression expression) {
        TreeItem item = new TreeItem(parent, SWT.NONE);
        initItem(expression, item);
    }

    private static void initItem(TreeItem parent, ISingleExpression expression) {
        TreeItem item = new TreeItem(parent, SWT.NONE);
        initItem(expression, item);
    }

    private static void initItem(ISingleExpression expression, TreeItem item) {
        item.setData(ItemDataInfo.DATA_EXPRESSION_TYPE_KEY, ItemDataInfo.DATA_EXPRESSION_TYPE_VALUE);
        item.setImage(expressionImage);
        ExpressionUIUtil.populateItem(item, expression);
    }

    private static void readPreparedExpressionsItems(TreeItem item, List<IExpression> pExpressions) {
        if (item.getData(ItemDataInfo.DATA_EXPRESSION_TYPE_KEY).equals(ItemDataInfo.DATA_EXPRESSION_GROUP_TYPE_VALUE)) {
            pExpressions.add(ExpressionUIUtil.extractExpressionGroup(item));
        } else {
            pExpressions.add(ExpressionUIUtil.extractExpression(item));
        }
    }

    public static String convertMatchValue(Boolean shouldMatch) {
        return shouldMatch ? ALL : ANY;
    }

    public static Boolean parseMatchValue(String shouldMatch) {
        return shouldMatch.equals(ALL);
    }
}
