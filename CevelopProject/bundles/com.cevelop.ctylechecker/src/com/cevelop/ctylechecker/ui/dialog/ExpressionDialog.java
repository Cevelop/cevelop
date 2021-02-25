package com.cevelop.ctylechecker.ui.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

import com.cevelop.ctylechecker.service.types.CtylecheckerRuntime;
import com.cevelop.ctylechecker.ui.parts.CustomExpressionComposite;
import com.cevelop.ctylechecker.ui.parts.ExpressionGroupComposite;
import com.cevelop.ctylechecker.ui.parts.PredefinedExpressionComposite;
import com.cevelop.ctylechecker.ui.util.DialogResult;
import com.cevelop.ctylechecker.ui.util.ItemDataInfo;


public class ExpressionDialog extends Dialog {

    private DialogResult dialogResult = new DialogResult();
    protected Shell      shell;

    public ExpressionDialog(Shell parent, String title, TreeItem pItem, Boolean pIsCustom) {
        super(parent);
        setText(title);
        createContents(pItem, pIsCustom);
    }

    /**
     * Create the dialog.
     *
     * @param parent
     * The shell
     * @param title
     * Title of this dialog
     * @param pItem
     * The tree item
     */
    public ExpressionDialog(Shell parent, String title, TreeItem pItem) {
        this(parent, title, pItem, false);
    }

    /**
     * Open the dialog.
     *
     * @return the result
     */
    public int open() {
        shell.open();
        shell.layout();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return dialogResult.getResult();
    }

    /**
     * Create contents of the dialog.
     *
     * @param pTree
     * The tree item
     *
     * @param pIsCustom
     * True iff this is a custom expression.
     *
     */
    private void createContents(TreeItem pItem, Boolean pIsCustom) {
        shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE | SWT.PRIMARY_MODAL);
        Image ruleImage = CtylecheckerRuntime.imageDescriptorFromPlugin("/resources/expression_img.jpg").createImage();
        shell.setImage(ruleImage);
        shell.setMinimumSize(new Point(630, 350));
        shell.setLocation(new Point(500, 100));
        shell.setSize(644, 349);
        shell.setText(getText());
        shell.setLayout(new GridLayout(1, false));
        if (pItem.getData(ItemDataInfo.DATA_EXPRESSION_TYPE_KEY).equals(ItemDataInfo.DATA_EXPRESSION_TYPE_VALUE)) {
            if (pIsCustom) {
                initCustomExpressionComposite(pItem);
            } else {
                initPredefinedExpressionComposite(pItem);
            }
        } else {
            initExpressionGroupComposite(pItem);
        }
    }

    private void initExpressionGroupComposite(TreeItem pItem) {
        shell.setText(getText() + " Expression Group");
        ExpressionGroupComposite expressionGroupComposite = new ExpressionGroupComposite(shell, pItem, dialogResult);
        prepareLayouting(expressionGroupComposite);
    }

    private void initPredefinedExpressionComposite(TreeItem pItem) {
        shell.setText(getText() + " Expression");
        PredefinedExpressionComposite rootComposite = new PredefinedExpressionComposite(shell, pItem, dialogResult);
        prepareLayouting(rootComposite);
    }

    private void initCustomExpressionComposite(TreeItem pItem) {
        shell.setText(getText() + " Expression");
        CustomExpressionComposite rootComposite = new CustomExpressionComposite(shell, pItem, dialogResult);
        prepareLayouting(rootComposite);
    }

    private void prepareLayouting(Composite pComposite) {
        GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_composite.minimumWidth = 500;
        pComposite.setLayoutData(gd_composite);
        pComposite.setLayout(new GridLayout(1, false));
    }
}
