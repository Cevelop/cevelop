package com.cevelop.ctylechecker.ui.parts;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.cevelop.ctylechecker.domain.ExpressionType;
import com.cevelop.ctylechecker.domain.ICtyleElement;
import com.cevelop.ctylechecker.domain.IExpression;
import com.cevelop.ctylechecker.domain.IGroupExpression;
import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.domain.ISingleExpression;
import com.cevelop.ctylechecker.ids.IdHelper;
import com.cevelop.ctylechecker.ui.SWTResourceManager;
import com.cevelop.ctylechecker.ui.dialog.ExpressionDialog;
import com.cevelop.ctylechecker.ui.util.ExpressionUIUtil;
import com.cevelop.ctylechecker.ui.util.ItemDataInfo;


public class PredefinedExpressionsComposite extends Composite {

    private Tree   predefinedExpressionTree;
    private Button newButton;

    private ICtyleElement element;
    private Button        customizeSelectedButton;
    private Button        removeSelectedButton;
    private Button        newGroupButton;
    private Button        deselectAllButton;

    public PredefinedExpressionsComposite(TabFolder folderExpressions, ICtyleElement pElement) {
        super(folderExpressions, SWT.NONE);
        element = pElement;
        GridLayout gl_predefinedExpressionsComposite = new GridLayout(1, false);
        this.setLayout(gl_predefinedExpressionsComposite);
        addTree();
    }

    private void addTree() {
        Composite treeComposite = new Composite(this, SWT.NONE);
        treeComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
        treeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        predefinedExpressionTree = new Tree(treeComposite, SWT.BORDER | SWT.FULL_SELECTION);
        predefinedExpressionTree.setSortDirection(SWT.DOWN);
        predefinedExpressionTree.setLinesVisible(true);
        predefinedExpressionTree.setHeaderVisible(true);

        TreeColumn trclmnName = new TreeColumn(predefinedExpressionTree, SWT.CENTER);
        trclmnName.setWidth(150);
        trclmnName.setText("Name");

        TreeColumn trclmnExpression = new TreeColumn(predefinedExpressionTree, SWT.CENTER);
        trclmnExpression.setWidth(150);
        trclmnExpression.setText("Expression");

        TreeColumn trclmnMatch = new TreeColumn(predefinedExpressionTree, SWT.CENTER);
        trclmnMatch.setWidth(50);
        trclmnMatch.setText("Match");

        TreeColumn trclmnResolution = new TreeColumn(predefinedExpressionTree, SWT.NONE);
        trclmnResolution.setWidth(141);
        trclmnResolution.setText("Resolution");

        TreeColumn trclmnOptional = new TreeColumn(predefinedExpressionTree, SWT.NONE);
        trclmnOptional.setWidth(126);
        trclmnOptional.setText("Argument");

        TreeColumn trclmnHint = new TreeColumn(predefinedExpressionTree, SWT.NONE);
        trclmnHint.setWidth(62);
        trclmnHint.setText("Hint");

        TreeColumn trclmnOrder = new TreeColumn(predefinedExpressionTree, SWT.NONE);
        trclmnOrder.setWidth(70);
        trclmnOrder.setText("Order");

        Composite buttonComposite = new Composite(this, SWT.NONE);
        buttonComposite.setLayout(new GridLayout(7, false));
        buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        newButton = new Button(buttonComposite, SWT.NONE);
        newButton.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        newButton.setText("New");

        newGroupButton = new Button(buttonComposite, SWT.NONE);
        newGroupButton.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        newGroupButton.setText("New Group");

        Label label = new Label(buttonComposite, SWT.SEPARATOR | SWT.VERTICAL);
        GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
        gd_label.minimumWidth = 1;
        gd_label.heightHint = 20;
        label.setLayoutData(gd_label);

        customizeSelectedButton = new Button(buttonComposite, SWT.NONE);
        customizeSelectedButton.setForeground(SWTResourceManager.getColor(0, 0, 0));
        customizeSelectedButton.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        customizeSelectedButton.setText("Customize Selected");

        removeSelectedButton = new Button(buttonComposite, SWT.NONE);
        removeSelectedButton.setForeground(SWTResourceManager.getColor(0, 0, 0));
        removeSelectedButton.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        removeSelectedButton.setText("Remove Selected");

        Label label_1 = new Label(buttonComposite, SWT.SEPARATOR | SWT.VERTICAL);
        GridData gd_label_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_label_1.minimumWidth = 1;
        gd_label_1.heightHint = 20;
        label_1.setLayoutData(gd_label_1);

        deselectAllButton = new Button(buttonComposite, SWT.NONE);
        deselectAllButton.setText("Deselect All");
    }

    public void initializePredefinedExpressions() {
        if (element instanceof IRule) {
            predefinedExpressionTree.removeAll();
            IRule existingRule = (IRule) element;
            for (IExpression iexpression : existingRule.getPredefinedExpressions()) {
                if (iexpression.getType().equals(ExpressionType.SINGLE)) {
                    ISingleExpression expression = (ISingleExpression) iexpression;
                    ExpressionUIUtil.initItem(predefinedExpressionTree, expression);
                }
                if (iexpression.getType().equals(ExpressionType.GROUP)) {
                    IGroupExpression group = (IGroupExpression) iexpression;
                    ExpressionUIUtil.initGroupItem(predefinedExpressionTree, group);
                }
            }
        }
    }

    public void addPredefinedExpressionListeners() {
        addNewButtonListener();
        addNewGroupButtonListener();
        addCustomizeSelectedButtonListener();
        addRemoveSelectedButtonListener();
        addDeselectAllButtonListener();
    }

    private void addDeselectAllButtonListener() {
        deselectAllButton.addListener(SWT.MouseUp, (event) -> {
            predefinedExpressionTree.deselectAll();
        });
    }

    private void addRemoveSelectedButtonListener() {
        removeSelectedButton.addListener(SWT.MouseUp, (event) -> {
            if (predefinedExpressionTree.getItemCount() > 0) {
                for (TreeItem item : predefinedExpressionTree.getSelection()) {
                    item.dispose();
                }
            }
        });
    }

    private void addCustomizeSelectedButtonListener() {
        customizeSelectedButton.addListener(SWT.MouseUp, (event) -> {
            if (predefinedExpressionTree.getSelectionCount() > 0) {
                TreeItem selectedItem = predefinedExpressionTree.getSelection()[0];
                ExpressionDialog dialog = new ExpressionDialog(getShell(), "Customize", selectedItem);
                dialog.open();
            }
        });
    }

    private void addNewGroupButtonListener() {
        newGroupButton.addListener(SWT.MouseUp, (event) -> {
            TreeItem newItem = null;
            if (predefinedExpressionTree.getSelectionCount() > 0) {
                TreeItem selectedItem = predefinedExpressionTree.getSelection()[0];
                if (selectedItem.getData(ItemDataInfo.DATA_EXPRESSION_TYPE_KEY).equals(ItemDataInfo.DATA_EXPRESSION_GROUP_TYPE_VALUE)) {
                    newItem = new TreeItem(selectedItem, SWT.NONE);
                } else {
                    if (selectedItem.getParentItem() != null) {
                        newItem = new TreeItem(selectedItem.getParentItem(), SWT.NONE);
                    } else {
                        newItem = new TreeItem(predefinedExpressionTree, SWT.NONE);
                    }
                }
            } else {
                newItem = new TreeItem(predefinedExpressionTree, SWT.NONE);
            }
            newItem.setData(ItemDataInfo.DATA_EXPRESSION_TYPE_KEY, ItemDataInfo.DATA_EXPRESSION_GROUP_TYPE_VALUE);
            newItem.setImage(AbstractUIPlugin.imageDescriptorFromPlugin(IdHelper.PLUGIN_ID, "/resources/expression_group_img.jpg").createImage());
            ExpressionDialog dialog = new ExpressionDialog(getShell(), "New", newItem);
            int returnValue = dialog.open();
            if (returnValue == SWT.CANCEL) {
                newItem.dispose();
            }
        });
    }

    private void addNewButtonListener() {
        newButton.addListener(SWT.MouseUp, (event) -> {
            TreeItem newItem = null;
            if (predefinedExpressionTree.getSelectionCount() > 0) {
                TreeItem selectedItem = predefinedExpressionTree.getSelection()[0];
                TreeItem parentItem = selectedItem.getParentItem();
                if (parentItem != null) {
                    if (selectedItem.getData(ItemDataInfo.DATA_EXPRESSION_TYPE_KEY).equals(ItemDataInfo.DATA_EXPRESSION_GROUP_TYPE_VALUE)) {
                        newItem = new TreeItem(selectedItem, SWT.NONE);
                    } else {
                        newItem = new TreeItem(parentItem, SWT.NONE);
                    }
                } else {
                    if (selectedItem.getData(ItemDataInfo.DATA_EXPRESSION_TYPE_KEY).equals(ItemDataInfo.DATA_EXPRESSION_GROUP_TYPE_VALUE)) {
                        newItem = new TreeItem(selectedItem, SWT.NONE);
                    } else {
                        newItem = new TreeItem(predefinedExpressionTree, SWT.NONE);
                    }
                }
            } else {
                newItem = new TreeItem(predefinedExpressionTree, SWT.NONE);
            }
            Image expressionImage = AbstractUIPlugin.imageDescriptorFromPlugin(IdHelper.PLUGIN_ID, "/resources/expression_img.jpg").createImage();
            newItem.setData(ItemDataInfo.DATA_EXPRESSION_TYPE_KEY, ItemDataInfo.DATA_EXPRESSION_TYPE_VALUE);
            newItem.setImage(expressionImage);

            ExpressionDialog dialog = new ExpressionDialog(getShell(), "New", newItem);
            if (dialog.open() == SWT.CANCEL) {
                newItem.dispose();
            }
        });
    }

    public List<IExpression> readPreparedExpressionsField() {
        return ExpressionUIUtil.readExpressionFields(predefinedExpressionTree);
    }
}
