package com.cevelop.ctylechecker.ui.parts;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.cevelop.ctylechecker.domain.ICtyleElement;
import com.cevelop.ctylechecker.domain.IExpression;
import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.service.types.CtylecheckerRuntime;
import com.cevelop.ctylechecker.ui.SWTResourceManager;


public class ExpressionsComposite extends Composite {

    private Image infoIcon;

    private PredefinedExpressionsComposite predefinedExpressionComposite;
    private CustomExpressionsComposite     customExpressionComposite;

    private ICtyleElement element;

    public ExpressionsComposite(Composite parent, ICtyleElement pElement) {
        super(parent, SWT.NONE);
        element = pElement;
        infoIcon = new Image(Display.getDefault(), Display.getDefault().getSystemImage(SWT.ICON_INFORMATION).getImageData().scaledTo(20, 20));
        GridLayout gl_expressionsContainerComposite = new GridLayout(1, false);
        gl_expressionsContainerComposite.marginHeight = 0;
        gl_expressionsContainerComposite.marginWidth = 0;
        this.setLayout(gl_expressionsContainerComposite);
        this.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        Label topSeparator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
        topSeparator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        topSeparator.setSize(64, 2);

        addExpressionsTitleComposite();
        addTabFolder();
    }

    private void addTabFolder() {
        TabFolder folderExpressions = new TabFolder(this, SWT.NONE);
        folderExpressions.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
        folderExpressions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        TabItem predefinedExpressionTabItem = new TabItem(folderExpressions, SWT.NONE);
        predefinedExpressionTabItem.setText("Predefined Expressions");
        predefinedExpressionComposite = new PredefinedExpressionsComposite(folderExpressions, element);
        predefinedExpressionTabItem.setControl(predefinedExpressionComposite);

        TabItem customExpressionsTabItem = new TabItem(folderExpressions, SWT.NONE);
        customExpressionsTabItem.setText("Custom Expressions");
        customExpressionComposite = new CustomExpressionsComposite(folderExpressions, element);
        customExpressionsTabItem.setControl(customExpressionComposite);
    }

    private void addExpressionsTitleComposite() {
        Composite expressionsTitleComposite = new Composite(this, SWT.NONE);
        GridLayout gl_expressionsTitleComposite = new GridLayout(2, false);
        gl_expressionsTitleComposite.marginWidth = 0;
        gl_expressionsTitleComposite.marginHeight = 0;
        expressionsTitleComposite.setLayout(gl_expressionsTitleComposite);
        expressionsTitleComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        Label lblExpressions = new Label(expressionsTitleComposite, SWT.NONE);
        lblExpressions.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));
        lblExpressions.setText("Expressions");

        Label lblInfo = new Label(expressionsTitleComposite, SWT.NONE);
        String message = "Be cautious of conflicting definitions (especially in Custom Expressions). " +
                         "\r\nExpressionGroups and Expressions with PREFERED Hint will be" +
                         "\r\nthe only applied resolutions in their respective levels (including root)." +
                         "\r\nExpressions and ExpressionGroups on root level will all be checked." +
                         "\r\nFor checks with alternatives and complex AND/OR logic, use nested ExpressionGroups.";
        lblInfo.setToolTipText(message);
        GridData gd_lblInfo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_lblInfo.minimumHeight = -1;
        lblInfo.setLayoutData(gd_lblInfo);
        lblInfo.setImage(infoIcon);
        lblInfo.addListener(SWT.MouseUp, (event) -> {
            CtylecheckerRuntime.showMessage("Expression Info", message);
        });
    }

    public void initializePredefinedExpressions() {
        predefinedExpressionComposite.initializePredefinedExpressions();
    }

    public void addPredefinedExpressionListeners() {
        predefinedExpressionComposite.addPredefinedExpressionListeners();
    }

    public void initializeCustomExpressions(IRule existingRule) {
        customExpressionComposite.initializeCustomExpressions();
    }

    public void addCustomExpressionListeners() {
        customExpressionComposite.addCustomExpressionListeners();
    }

    public List<IExpression> readCustomExpressionsField() {
        return customExpressionComposite.readCustomExpressionsField();
    }

    public List<IExpression> readPreparedExpressionsField() {
        return predefinedExpressionComposite.readPreparedExpressionsField();
    }
}
