package com.cevelop.ctylechecker.ui.parts;

import java.util.Optional;

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
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

import com.cevelop.ctylechecker.common.ExpressionNames;
import com.cevelop.ctylechecker.domain.IExpression;
import com.cevelop.ctylechecker.domain.ISingleExpression;
import com.cevelop.ctylechecker.domain.OrderPriority;
import com.cevelop.ctylechecker.domain.ResolutionHint;
import com.cevelop.ctylechecker.service.IExpressionService;
import com.cevelop.ctylechecker.service.factory.ResolutionFactory;
import com.cevelop.ctylechecker.ui.AbstractCtylecheckerComposite;
import com.cevelop.ctylechecker.ui.util.DialogResult;
import com.cevelop.ctylechecker.ui.util.ExpressionItemArg;
import com.cevelop.ctylechecker.ui.util.ExpressionUIUtil;


public class PredefinedExpressionComposite extends AbstractCtylecheckerComposite {

    private Text textRegexInput;
    private Text txtResolutionargument;

    private DialogResult dialogResult;
    private Combo        expressionNameCombo;
    private Button       btnMatch;
    private Combo        resolutionCombo;
    private Combo        hintCombo;
    private Combo        orderPriorityCombo;
    private Button       btnSave;
    private Button       btnCancel;

    private Listener expressionNameComboChangedListener;
    private TreeItem item;
    private Label    lblResolutionInfo;

    public PredefinedExpressionComposite(Composite parent, TreeItem pItem, DialogResult pResult) {
        super(parent, SWT.NONE);
        item = pItem;
        dialogResult = pResult;
        setLayout(new GridLayout(1, false));
        Composite nameComposite = new Composite(this, SWT.NONE);
        nameComposite.setLayout(new FillLayout(SWT.VERTICAL));
        nameComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblExpressionName = new Label(nameComposite, SWT.NONE);
        lblExpressionName.setText("Expression Name");

        expressionNameCombo = new Combo(nameComposite, SWT.READ_ONLY);

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

        textRegexInput = new Text(regexInputComposite, SWT.BORDER | SWT.READ_ONLY);
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
        for (int i = 0; i < expressionNameCombo.getItemCount(); i++) {
            if (expressionNameCombo.getItem(i).equals(item.getText(ExpressionItemArg.NAME))) {
                expressionNameCombo.select(i);
                expressionNameComboChangedListener.handleEvent(null);
                initExpressionFields(ExpressionUIUtil.extractExpression(item));
            }
        }
    }

    private void initDefault() {
        expressionNameCombo.select(0);
        expressionNameComboChangedListener.handleEvent(null);
    }

    private void updateResolutionInfo() {
        String resolutionInfoText = ResolutionFactory.getResolutionInfo(resolutionCombo.getText());
        lblResolutionInfo.setVisible(!resolutionInfoText.isEmpty());
        lblResolutionInfo.setToolTipText(resolutionInfoText);
    }

    private void addListeners() {
        expressionNameComboChangedListener = (event) -> {
            String expressionName = expressionNameCombo.getText();
            if (!expressionName.isEmpty()) {
                if (expressionName.equals(item.getText(ExpressionItemArg.NAME))) {
                    initExpressionFields(ExpressionUIUtil.extractExpression(item));
                } else {
                    IExpressionService expressionService = getRegistry().getExpressionService();
                    Optional<ISingleExpression> oExpression = expressionService.find(expressionName);
                    if (oExpression.isPresent()) {
                        initExpressionFields(oExpression.get());
                    }
                }
            }
            updateResolutionInfo();
        };

        expressionNameCombo.addListener(SWT.Selection, expressionNameComboChangedListener);

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

        btnSave.addListener(SWT.MouseUp, (event) -> {
            ISingleExpression newExpression = prepareExpression();
            ExpressionUIUtil.populateItem(item, newExpression);
            dialogResult.setResult(SWT.OK);
            getShell().close();
        });

        btnCancel.addListener(SWT.MouseUp, (event) -> {
            dialogResult.setResult(SWT.CANCEL);
            getShell().close();
        });
    }

    private void initExpressionFields(ISingleExpression currentExpression) {
        textRegexInput.setText(currentExpression.getExpression());
        btnMatch.setSelection(currentExpression.shouldMatch());
        txtResolutionargument.setText(currentExpression.getArgument());
        initializeResolutionCombo(currentExpression.getName(), currentExpression.getResolution().getClass().getSimpleName());
        initializeHintCombo(currentExpression.getHint().name());
        initializeOrderCombo(currentExpression.getOrder().name());
        updateResolutionInfo();
    }

    private ISingleExpression prepareExpression() {
        IExpressionService expressionService = getRegistry().getExpressionService();
        ISingleExpression newExpression = expressionService.createExpression(expressionNameCombo.getText(), textRegexInput.getText(), btnMatch
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

    private void initializeResolutionCombo(String pExpressionName, String pResolutionName) {
        resolutionCombo.removeAll();
        if (pExpressionName.equals(ExpressionNames.HAS_PREFIX_NAME)) {
            resolutionCombo.add(ResolutionFactory.ADD_PREFIX_RESOLUTION);
        }
        if (pExpressionName.equals(ExpressionNames.HAS_SUFFIX_NAME)) {
            resolutionCombo.add(ResolutionFactory.ADD_SUFFIX_RESOLUTION);
        }
        if (!isNotCaseTransformer(pExpressionName)) {
            resolutionCombo.add(ResolutionFactory.CASE_TRANSFORMER_RESOLUTION);
        }
        if (pExpressionName.equals(ExpressionNames.CPP_FILE_ENDING_NAME) || pExpressionName.equals(ExpressionNames.H_FILE_ENDING_NAME) ||
            pExpressionName.equals(ExpressionNames.CC_FILE_ENDING_NAME) || pExpressionName.equals(ExpressionNames.CPP_CC_C_FILE_ENDING_NAME) ||
            pExpressionName.equals(ExpressionNames.H_HPP_FILE_ENDING_NAME)) {
            resolutionCombo.add(ResolutionFactory.REPLACE_RESOLUTION);
        }
        resolutionCombo.add(ResolutionFactory.DEFAULT_RENAME_RESOLUTION);
        for (int i = 0; i < resolutionCombo.getItemCount(); i++) {
            if (resolutionCombo.getItem(i).equals(pResolutionName)) {
                resolutionCombo.select(i);
                populateHintCombo(resolutionCombo.getItem(i));
                updateArgumentVisibility(resolutionCombo.getText());
                break;
            }
        }
    }

    private boolean isNotCaseTransformer(String pExpressionName) {
        return pExpressionName.equals(ExpressionNames.HAS_PREFIX_NAME) || pExpressionName.equals(ExpressionNames.HAS_SUFFIX_NAME) || pExpressionName
                .equals(ExpressionNames.CPP_FILE_ENDING_NAME);

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
        IExpressionService expressionService = getRegistry().getExpressionService();
        for (IExpression expression : expressionService.getAll()) {
            expressionNameCombo.add(expression.getName());
        }
        for (OrderPriority priority : OrderPriority.values()) {
            orderPriorityCombo.add(priority.name());
        }
    }
}
