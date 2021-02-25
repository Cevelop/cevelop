package com.cevelop.ctylechecker.ui.parts;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

import com.cevelop.ctylechecker.domain.ISingleExpression;
import com.cevelop.ctylechecker.domain.OrderPriority;
import com.cevelop.ctylechecker.domain.ResolutionHint;
import com.cevelop.ctylechecker.service.IExpressionService;
import com.cevelop.ctylechecker.service.factory.ResolutionFactory;
import com.cevelop.ctylechecker.service.types.CtylecheckerRuntime;
import com.cevelop.ctylechecker.ui.AbstractCtylecheckerComposite;
import com.cevelop.ctylechecker.ui.util.DialogResult;
import com.cevelop.ctylechecker.ui.util.ExpressionItemArg;
import com.cevelop.ctylechecker.ui.util.ExpressionUIUtil;


public class CustomExpressionComposite extends AbstractCtylecheckerComposite {

    private Text textRegexInput;
    private Text txtResolutionargument;

    private DialogResult dialogResult;
    private Text         expressionNameInput;
    private Button       btnMatch;
    private Combo        resolutionCombo;
    private Combo        hintCombo;
    private Combo        orderPriorityCombo;
    private Button       btnSave;
    private Button       btnCancel;

    private TreeItem item;
    private Label    lblResolutionInfo;

    public CustomExpressionComposite(Composite parent, TreeItem pItem, DialogResult pDialogResult) {
        super(parent, SWT.NONE);
        item = pItem;
        dialogResult = pDialogResult;
        setLayout(new GridLayout(1, false));
        Composite nameComposite = new Composite(this, SWT.NONE);
        nameComposite.setLayout(new FillLayout(SWT.VERTICAL));
        nameComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblExpressionName = new Label(nameComposite, SWT.NONE);
        lblExpressionName.setText("Expression Name");

        expressionNameInput = new Text(nameComposite, SWT.BORDER);

        Composite regexComposite = new Composite(this, SWT.NONE);
        GridLayout gl_regexComposite = new GridLayout(1, false);
        gl_regexComposite.marginWidth = 0;
        gl_regexComposite.marginHeight = 0;
        regexComposite.setLayout(gl_regexComposite);
        regexComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblRegex = new Label(regexComposite, SWT.NONE);
        lblRegex.setText("Regex");

        Composite regexInputComposite = new Composite(regexComposite, SWT.NONE);
        regexInputComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout gl_regexInputComposite = new GridLayout(2, false);
        gl_regexInputComposite.marginWidth = 0;
        gl_regexInputComposite.marginHeight = 0;
        regexInputComposite.setLayout(gl_regexInputComposite);

        textRegexInput = new Text(regexInputComposite, SWT.BORDER);
        textRegexInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        btnMatch = new Button(regexInputComposite, SWT.CHECK);
        btnMatch.setText("Match");

        Composite resolutionComposite = new Composite(this, SWT.NONE);
        GridLayout gl_resolutionComposite = new GridLayout(1, false);
        gl_resolutionComposite.marginWidth = 0;
        gl_resolutionComposite.marginHeight = 0;
        resolutionComposite.setLayout(gl_resolutionComposite);
        resolutionComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Composite resolutionNameComposite = new Composite(resolutionComposite, SWT.NONE);
        GridLayout gl_resolutionNameComposite = new GridLayout(2, false);
        gl_resolutionNameComposite.marginWidth = 0;
        gl_resolutionNameComposite.marginHeight = 0;
        resolutionNameComposite.setLayout(gl_resolutionNameComposite);
        resolutionNameComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

        Label lblResolution = new Label(resolutionNameComposite, SWT.NONE);
        lblResolution.setText("Resolution");

        lblResolutionInfo = new Label(resolutionNameComposite, SWT.NONE);
        lblResolutionInfo.setToolTipText("Huhu");
        lblResolutionInfo.setImage(new Image(Display.getDefault(), Display.getDefault().getSystemImage(SWT.ICON_INFORMATION).getImageData().scaledTo(
                20, 20)));

        Composite composite = new Composite(resolutionComposite, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout gl_composite = new GridLayout(2, false);
        gl_composite.marginHeight = 0;
        gl_composite.marginWidth = 0;
        composite.setLayout(gl_composite);

        resolutionCombo = new Combo(composite, SWT.READ_ONLY);
        resolutionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        txtResolutionargument = new Text(composite, SWT.BORDER);
        txtResolutionargument.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtResolutionargument.setMessage("type resolution argument");

        Composite hintComposite = new Composite(this, SWT.NONE);
        hintComposite.setLayout(new FillLayout(SWT.VERTICAL));
        hintComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblHint = new Label(hintComposite, SWT.NONE);
        lblHint.setText("Hint");

        hintCombo = new Combo(hintComposite, SWT.READ_ONLY);

        Composite orderPriorityComposite = new Composite(this, SWT.NONE);
        orderPriorityComposite.setLayout(new FillLayout(SWT.VERTICAL));
        orderPriorityComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblProcessingPriority = new Label(orderPriorityComposite, SWT.NONE);
        lblProcessingPriority.setText("Processing Priority");

        orderPriorityCombo = new Combo(orderPriorityComposite, SWT.READ_ONLY);

        Composite buttonsComposite = new Composite(this, SWT.NONE);
        buttonsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
        buttonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true, 1, 1));

        btnSave = new Button(buttonsComposite, SWT.NONE);
        btnSave.setText("Save");

        btnCancel = new Button(buttonsComposite, SWT.NONE);
        btnCancel.setText("Cancel");

        initializeFields();
        addListeners();
        if (item.getText(ExpressionItemArg.NAME).isEmpty()) {
            initDefault();
        } else {
            initItem();
        }
    }

    private void initItem() {
        initDefault();
        initExpressionFields(ExpressionUIUtil.extractExpression(item));
    }

    private void initDefault() {
        resolutionCombo.select(0);
        updateResolutionInfo();
        orderPriorityCombo.select(0);
        btnMatch.setSelection(true);
        initializeResolutionCombo(resolutionCombo.getText());
        hintCombo.select(0);
    }

    private void updateResolutionInfo() {
        String resolutionInfoText = ResolutionFactory.getResolutionInfo(resolutionCombo.getText());
        lblResolutionInfo.setVisible(!resolutionInfoText.isEmpty());
        lblResolutionInfo.setToolTipText(resolutionInfoText);
    }

    private void addListeners() {
        resolutionCombo.addListener(SWT.Selection, (event) -> {
            if (resolutionCombo.getSelectionIndex() >= 0) {
                String selectedResolutionItem = resolutionCombo.getText();
                updateArgumentVisibility(selectedResolutionItem);
                populateHintCombo(selectedResolutionItem);
                if (item != null) {
                    initializeHintCombo(item.getText(0));
                }
            }
            updateResolutionInfo();
        });

        textRegexInput.addListener(SWT.Modify, (event) -> {
            checkRegex();
        });

        btnSave.addListener(SWT.MouseUp, (event) -> {
            if (checkRegex()) {
                ISingleExpression newExpression = prepareExpression();
                ExpressionUIUtil.populateItem(item, newExpression);
                dialogResult.setResult(SWT.OK);
                getShell().close();
            } else {
                CtylecheckerRuntime.showMessage("Warning", "Your regex is invalid, please fix your regex.");
            }
        });

        btnCancel.addListener(SWT.MouseUp, (event) -> {
            dialogResult.setResult(SWT.CANCEL);
            getShell().close();
        });
    }

    private Boolean checkRegex() {
        try {
            Pattern.compile(textRegexInput.getText());
            textRegexInput.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_TRANSPARENT));
            textRegexInput.setToolTipText("Your regex is OK");
            return true;
        } catch (PatternSyntaxException ex) {
            textRegexInput.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_YELLOW));
            textRegexInput.setToolTipText("Your regex is invalid");
        }
        return false;
    }

    private void initExpressionFields(ISingleExpression currentExpression) {
        expressionNameInput.setText(currentExpression.getName());
        textRegexInput.setText(currentExpression.getExpression());
        btnMatch.setSelection(currentExpression.shouldMatch());
        txtResolutionargument.setText(currentExpression.getArgument());
        initializeResolutionCombo(currentExpression.getResolution().getClass().getSimpleName());
        initializeHintCombo(currentExpression.getHint().name());
        initializeOrderCombo(currentExpression.getOrder().name());
        updateResolutionInfo();
    }

    private ISingleExpression prepareExpression() {
        IExpressionService expressionService = getRegistry().getExpressionService();
        ISingleExpression newExpression = expressionService.createExpression(expressionNameInput.getText(), textRegexInput.getText(), btnMatch
                .getSelection());
        newExpression.setResolution(ResolutionFactory.createResolution(resolutionCombo.getText(), newExpression));
        newExpression.setArgument(txtResolutionargument.getText());
        newExpression.setHint(ResolutionHint.valueOf(hintCombo.getText()));
        newExpression.setOrder(OrderPriority.valueOf(orderPriorityCombo.getText()));
        return newExpression;
    }

    private void updateArgumentVisibility(String selectedResolutionItem) {
        if (selectedResolutionItem.equals(ResolutionFactory.ADD_PREFIX_RESOLUTION) || selectedResolutionItem.equals(
                ResolutionFactory.ADD_SUFFIX_RESOLUTION) || selectedResolutionItem.equals(ResolutionFactory.REPLACE_RESOLUTION)) {
            txtResolutionargument.setVisible(true);
        } else {
            txtResolutionargument.setVisible(false);
        }
    }

    private void initializeResolutionCombo(String pResolutionName) {
        for (int i = 0; i < resolutionCombo.getItemCount(); i++) {
            if (resolutionCombo.getItem(i).equals(pResolutionName)) {
                resolutionCombo.select(i);
                populateHintCombo(resolutionCombo.getItem(i));
                updateArgumentVisibility(resolutionCombo.getText());
                break;
            }
        }
    }

    private void initializeOrderCombo(String pOrderName) {
        for (int i = 0; i < orderPriorityCombo.getItemCount(); i++) {
            if (orderPriorityCombo.getItem(i).equals(pOrderName)) {
                orderPriorityCombo.select(i);
            }
        }
    }

    private void initializeHintCombo(String pHintName) {
        hintCombo.select(0);
        for (int i = 0; i < hintCombo.getItemCount(); i++) {
            if (hintCombo.getItem(i).equals(pHintName)) {
                hintCombo.select(i);
            }
        }
    }

    private void populateHintCombo(String selectedResolution) {
        hintCombo.removeAll();
        if (selectedResolution.equals(ResolutionFactory.ADD_PREFIX_RESOLUTION) || selectedResolution.equals(
                ResolutionFactory.ADD_SUFFIX_RESOLUTION)) {
            for (ResolutionHint hint : ResolutionHint.casingHints()) {
                hintCombo.add(hint.name());
            }
        } else {
            for (ResolutionHint hint : ResolutionHint.nonCasingHints()) {
                hintCombo.add(hint.name());
            }
        }
    }

    private void initializeFields() {
        for (OrderPriority priority : OrderPriority.values()) {
            orderPriorityCombo.add(priority.name());
        }
        for (ResolutionHint hint : ResolutionHint.nonCasingHints()) {
            hintCombo.add(hint.name());
        }
        for (String resolution : ResolutionFactory.names()) {
            resolutionCombo.add(resolution);
        }
    }
}
