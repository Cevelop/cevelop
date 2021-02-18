package com.cevelop.ctylechecker.ui.component;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.layout.GridLayout;

import java.util.Optional;
import java.util.UUID;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;

import com.cevelop.ctylechecker.domain.IConfiguration;
import com.cevelop.ctylechecker.domain.ICtyleElement;
import com.cevelop.ctylechecker.domain.IGrouping;
import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.service.IConceptService;
import com.cevelop.ctylechecker.ui.AbstractCtylecheckerComposite;
import com.cevelop.ctylechecker.ui.listener.EnterKeyListener;
import com.cevelop.ctylechecker.ui.parts.CheckedConceptComposite;
import com.cevelop.ctylechecker.ui.parts.EnabledAndRootComposite;
import com.cevelop.ctylechecker.ui.parts.ExpressionsComposite;
import com.cevelop.ctylechecker.ui.parts.MessageComposite;
import com.cevelop.ctylechecker.ui.parts.RuleNameComposite;
import com.cevelop.ctylechecker.ui.util.DialogResult;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;


public class RuleComposite extends AbstractCtylecheckerComposite {

    private Button btnNewRule;
    private Button btnCancel;

    private EnabledAndRootComposite enabledAndRootComposite;
    private RuleNameComposite       ruleNameComposite;
    private MessageComposite        messageComposite;
    private ExpressionsComposite    expressionComposite;
    private CheckedConceptComposite checkedConceptComposite;

    public RuleComposite(Composite parent, IConfiguration pConfig, ICtyleElement pElement, DialogResult pDialog) {
        super(parent, SWT.NONE);
        setLayout(new GridLayout(1, false));
        createContents(pConfig, pElement, pDialog);
    }

    private void createContents(IConfiguration pConfig, ICtyleElement pElement, DialogResult pDialog) {
        enabledAndRootComposite = new EnabledAndRootComposite(this, pConfig);
        ruleNameComposite = new RuleNameComposite(this);
        messageComposite = new MessageComposite(this);
        expressionComposite = new ExpressionsComposite(this, pElement);
        GridData gd_expressionComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_expressionComposite.minimumHeight = 250;
        gd_expressionComposite.widthHint = 617;
        expressionComposite.setLayoutData(gd_expressionComposite);
        checkedConceptComposite = new CheckedConceptComposite(this);
        addButtonsComposite();
        addListeners(pConfig, pElement, pDialog);
        initializeFields(pConfig, pElement);
    }

    private void addButtonsComposite() {
        Composite buttonsComposite = new Composite(this, SWT.NONE);
        buttonsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
        buttonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true, 1, 1));
        buttonsComposite.setBounds(0, 0, 64, 64);

        btnNewRule = new Button(buttonsComposite, SWT.NONE);
        btnCancel = new Button(buttonsComposite, SWT.NONE);
        btnCancel.setText("Cancel");
    }

    private void addListeners(IConfiguration pConfig, ICtyleElement pElement, DialogResult pDialog) {
        addNewRuleListener(pConfig, pElement, pDialog);
        addCancelListener(pDialog);
        expressionComposite.addCustomExpressionListeners();
        expressionComposite.addPredefinedExpressionListeners();
        checkedConceptComposite.addCheckedConceptsListeners();
    }

    private void initializeFields(IConfiguration pConfig, ICtyleElement pElement) {
        IGrouping ruleRoot = null;
        if (pElement instanceof IRule) {
            ruleRoot = intializeCustomizeRuleFields(pConfig, pElement, ruleRoot);
        } else {
            ruleRoot = initializeNewRuleFields(pElement, ruleRoot);
        }
        enabledAndRootComposite.populateRootComboField(pConfig);
        enabledAndRootComposite.initializeRootCombo(ruleRoot);
    }

    private IGrouping initializeNewRuleFields(ICtyleElement pElement, IGrouping ruleRoot) {
        if (pElement instanceof IGrouping) {
            ruleRoot = (IGrouping) pElement;
        }
        IConceptService conceptService = getRegistry().getConceptService();
        for (String concept : conceptService.getAll()) {
            checkedConceptComposite.getAvailableCheckingsList().add(conceptService.simpleName(concept));
        }
        btnNewRule.setText("New Rule");
        return ruleRoot;
    }

    private IGrouping intializeCustomizeRuleFields(IConfiguration pConfig, ICtyleElement pElement, IGrouping ruleRoot) {
        btnNewRule.setText("Save Rule");
        IRule existingRule = (IRule) pElement;
        ruleNameComposite.getRuleNameText().setText(existingRule.getName());
        enabledAndRootComposite.getBtnEnabled().setSelection(existingRule.isEnabled());
        messageComposite.getMessageInputText().setText(existingRule.getMessage());

        expressionComposite.initializePredefinedExpressions();
        expressionComposite.initializeCustomExpressions(existingRule);

        Optional<IGrouping> oRuleRoot = getRegistry().getStyleguideService().findGroupOfRule(pConfig.getActiveStyleguide(), existingRule);
        if (oRuleRoot.isPresent()) {
            ruleRoot = oRuleRoot.get();
        }

        checkedConceptComposite.setCheckedConcepts(existingRule.getCheckedConcepts());
        IConceptService conceptService = getRegistry().getConceptService();
        for (String concept : conceptService.getAll()) {
            if (!checkedConceptComposite.getCheckedConcepts().contains(conceptService.createConcept(concept))) {
                checkedConceptComposite.getAvailableCheckingsList().add(conceptService.simpleName(concept));
            } else {
                checkedConceptComposite.getActiveCheckingsList().add(conceptService.simpleName(concept));
            }
        }
        checkedConceptComposite.updateActiveConceptsList();
        return ruleRoot;
    }

    private void addCancelListener(DialogResult pDialog) {
        Listener cancelListener = (event) -> {
            pDialog.setResult(SWT.CANCEL);
            getShell().close();
        };
        btnCancel.addListener(SWT.MouseUp, cancelListener);
        btnCancel.addListener(SWT.Traverse, new EnterKeyListener() {

            @Override
            public void handle(Event event) {
                cancelListener.handleEvent(event);
            }
        });
    }

    private void addNewRuleListener(IConfiguration pConfig, ICtyleElement pElement, DialogResult pDialog) {
        Listener newRuleListener = (event) -> {
            IRule newRule = prepareRule();
            if (pElement != null) {
                if (pElement instanceof IRule) {
                    saveExistingRule(pConfig, pElement, newRule);
                }
                if (pElement instanceof IGrouping) {
                    saveNewRule(pConfig, pElement, newRule);
                }
            } else {
                saveNewRootRule(pConfig, newRule);
            }
            pDialog.setResult(SWT.OK);
            getShell().close();
        };
        EnterKeyListener newEnterKeyRuleListener = new EnterKeyListener() {

            @Override
            public void handle(Event event) {
                newRuleListener.handleEvent(event);
            }
        };
        btnNewRule.addListener(SWT.MouseUp, newRuleListener);
        btnNewRule.addListener(SWT.Traverse, newEnterKeyRuleListener);
    }

    private IRule prepareRule() {
        IRule newRule = getRegistry().getRuleService().createRule(ruleNameComposite.getRuleNameText().getText(), enabledAndRootComposite
                .getBtnEnabled().getSelection());
        newRule.setPredefinedExpressions(expressionComposite.readPreparedExpressionsField());
        newRule.setCustomExpressions(expressionComposite.readCustomExpressionsField());
        newRule.setMessage(messageComposite.getMessageInputText().getText());
        newRule.setCheckedConcepts(checkedConceptComposite.getCheckedConcepts());
        return newRule;
    }

    private void saveNewRootRule(IConfiguration pConfig, IRule newRule) {
        IGrouping selectedGrouping = null;
        if (enabledAndRootComposite.getRootCombo().getSelectionIndex() > 0) {
            String id = enabledAndRootComposite.getRootCombo().getData(enabledAndRootComposite.getRootCombo().getSelectionIndex() + "").toString();
            selectedGrouping = pConfig.getActiveStyleguide().getGrouping(UUID.fromString(id));
        }
        if (selectedGrouping != null) {
            selectedGrouping.addRule(newRule);
        } else {
            pConfig.getActiveStyleguide().addRule(newRule);
        }
    }

    private void saveNewRule(IConfiguration pConfig, ICtyleElement pElement, IRule newRule) {
        String id = enabledAndRootComposite.getRootCombo().getData(enabledAndRootComposite.getRootCombo().getSelectionIndex() + "").toString();
        IGrouping initialGrouping = (IGrouping) pElement;
        IGrouping selectedGrouping = pConfig.getActiveStyleguide().getGrouping(UUID.fromString(id));
        if (selectedGrouping.getId().equals(initialGrouping.getId())) {
            initialGrouping.addRule(newRule);
        } else {
            selectedGrouping.addRule(newRule);
        }
    }

    private void saveExistingRule(IConfiguration pConfig, ICtyleElement pElement, IRule newRule) {
        IRule existingRule = (IRule) pElement;
        existingRule = getRegistry().getRuleService().updateRule(existingRule, newRule);
        IGrouping selectedGrouping = null;
        if (enabledAndRootComposite.getRootCombo().getSelectionIndex() > 0) {
            String id = enabledAndRootComposite.getRootCombo().getData(enabledAndRootComposite.getRootCombo().getSelectionIndex() + "").toString();
            selectedGrouping = pConfig.getActiveStyleguide().getGrouping(UUID.fromString(id));
        }

        Optional<IGrouping> oInitialGrouping = getRegistry().getStyleguideService().findGroupOfRule(pConfig.getActiveStyleguide(), existingRule);
        if (oInitialGrouping.isPresent()) {
            IGrouping initialGrouping = oInitialGrouping.get();
            if (selectedGrouping != null) {
                if (!initialGrouping.getId().equals(selectedGrouping.getId())) {
                    initialGrouping.removeRule(existingRule.getId());
                    selectedGrouping.addRule(existingRule);
                }
            } else {
                initialGrouping.removeRule(existingRule.getId());
                pConfig.getActiveStyleguide().addRule(existingRule);
            }
        } else {
            if (selectedGrouping != null) {
                pConfig.getActiveStyleguide().removeRule(existingRule.getId());
                selectedGrouping.addRule(existingRule);
            }
        }
    }
}
