package com.cevelop.ctylechecker.ui.parts;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.cevelop.ctylechecker.domain.IConcept;
import com.cevelop.ctylechecker.service.IConceptService;
import com.cevelop.ctylechecker.ui.AbstractCtylecheckerComposite;
import com.cevelop.ctylechecker.ui.SWTResourceManager;


public class CheckedConceptComposite extends AbstractCtylecheckerComposite {

    private Button btnMoveToRight;
    private Button btnMoveToLeft;

    private Button btnMoveQualifierToRight;
    private Button btnMoveQualifierToLeft;
    private Image  infoIcon;

    private List<IConcept>               checkedConcepts = new ArrayList<>();
    private org.eclipse.swt.widgets.List availableCheckingsList;
    private org.eclipse.swt.widgets.List activeCheckingsList;
    private org.eclipse.swt.widgets.List activeConceptsList;
    private org.eclipse.swt.widgets.List availableQualifiersList;
    private org.eclipse.swt.widgets.List activeQualifiersList;

    public CheckedConceptComposite(Composite parent) {
        super(parent, SWT.NONE);
        infoIcon = new Image(Display.getDefault(), Display.getDefault().getSystemImage(SWT.ICON_INFORMATION).getImageData().scaledTo(20, 20));

        this.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout gl_conceptsContainerComposite = new GridLayout(1, false);
        gl_conceptsContainerComposite.marginWidth = 0;
        gl_conceptsContainerComposite.marginHeight = 0;
        this.setLayout(gl_conceptsContainerComposite);

        Label conceptsContainerTopSeparator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
        conceptsContainerTopSeparator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        Label conceptsContainerTitle = new Label(this, SWT.NONE);
        conceptsContainerTitle.setText("C++ Concepts and Qualifiers");

        TabFolder conceptsTabFolder = new TabFolder(this, SWT.NONE);
        conceptsTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        TabItem tbtmConcepts = new TabItem(conceptsTabFolder, SWT.NONE);
        tbtmConcepts.setText("Concepts");
        Composite checkedConceptsAllComposite = new Composite(conceptsTabFolder, SWT.NONE);
        tbtmConcepts.setControl(checkedConceptsAllComposite);
        GridLayout gl_checkedConceptsAllComposite = new GridLayout(1, false);
        checkedConceptsAllComposite.setLayout(gl_checkedConceptsAllComposite);

        Label lblAvailableConceptsTo = new Label(checkedConceptsAllComposite, SWT.NONE);
        lblAvailableConceptsTo.setBounds(0, 0, 55, 15);
        lblAvailableConceptsTo.setText("Concepts to check");

        Composite checkedConceptsListLabelComposite = new Composite(checkedConceptsAllComposite, SWT.NONE);
        checkedConceptsListLabelComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
        checkedConceptsListLabelComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        checkedConceptsListLabelComposite.setBounds(0, 0, 64, 64);

        Label lblAvailable = new Label(checkedConceptsListLabelComposite, SWT.NONE);
        lblAvailable.setText("Available");

        Label lblActive = new Label(checkedConceptsListLabelComposite, SWT.NONE);
        lblActive.setAlignment(SWT.RIGHT);
        lblActive.setText("Active");

        Composite checkedConceptsComposite = new Composite(checkedConceptsAllComposite, SWT.NONE);
        checkedConceptsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1));
        GridData gd_checkedConceptsComposite = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
        gd_checkedConceptsComposite.minimumHeight = 100;
        checkedConceptsComposite.setLayoutData(gd_checkedConceptsComposite);
        GridLayout gl_checkedConceptsComposite = new GridLayout(3, false);
        gl_checkedConceptsComposite.marginWidth = 0;
        gl_checkedConceptsComposite.marginHeight = 0;
        checkedConceptsComposite.setLayout(gl_checkedConceptsComposite);

        availableCheckingsList = new org.eclipse.swt.widgets.List(checkedConceptsComposite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gridData.widthHint = 100;
        gridData.minimumWidth = 100;
        gridData.heightHint = 162;
        availableCheckingsList.setLayoutData(gridData);

        Composite distributeCheckingsButtonsComposite = new Composite(checkedConceptsComposite, SWT.NONE);
        GridData gd_distributeCheckingsButtonsComposite = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
        gd_distributeCheckingsButtonsComposite.widthHint = 100;
        distributeCheckingsButtonsComposite.setLayoutData(gd_distributeCheckingsButtonsComposite);
        FillLayout fl_distributeCheckingsButtonsComposite = new FillLayout(SWT.VERTICAL);
        fl_distributeCheckingsButtonsComposite.marginWidth = 10;
        fl_distributeCheckingsButtonsComposite.spacing = 10;
        fl_distributeCheckingsButtonsComposite.marginHeight = 50;
        distributeCheckingsButtonsComposite.setLayout(fl_distributeCheckingsButtonsComposite);

        btnMoveToRight = new Button(distributeCheckingsButtonsComposite, SWT.NONE);
        btnMoveToRight.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
        btnMoveToRight.setText(">");

        btnMoveToLeft = new Button(distributeCheckingsButtonsComposite, SWT.NONE);
        btnMoveToLeft.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
        btnMoveToLeft.setText("<");

        activeCheckingsList = new org.eclipse.swt.widgets.List(checkedConceptsComposite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
        GridData gd_activeCheckingsList = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_activeCheckingsList.widthHint = 100;
        gd_activeCheckingsList.minimumWidth = 100;
        activeCheckingsList.setLayoutData(gd_activeCheckingsList);

        TabItem tbtmQualifiers = new TabItem(conceptsTabFolder, SWT.NONE);
        tbtmQualifiers.setText("Qualifiers");

        Composite qualifiersAllComposite = new Composite(conceptsTabFolder, SWT.NONE);
        tbtmQualifiers.setControl(qualifiersAllComposite);
        qualifiersAllComposite.setLayout(new GridLayout(1, false));

        Composite qualifiersTitleComposite = new Composite(qualifiersAllComposite, SWT.NONE);
        GridLayout gl_qualifiersTitleComposite = new GridLayout(2, false);
        gl_qualifiersTitleComposite.marginWidth = 0;
        gl_qualifiersTitleComposite.marginHeight = 0;
        qualifiersTitleComposite.setLayout(gl_qualifiersTitleComposite);
        qualifiersTitleComposite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));

        Label lblActiveConceptQualifiers = new Label(qualifiersTitleComposite, SWT.NONE);
        lblActiveConceptQualifiers.setBounds(0, 0, 55, 15);
        lblActiveConceptQualifiers.setText("Qualifiers to check");

        Label labelACtiveConceptQualifiersInfo = new Label(qualifiersTitleComposite, SWT.NONE);
        labelACtiveConceptQualifiersInfo.setToolTipText("Some Concepts may not have any qualifiers to set.\r\n" +
                                                        "If no active qualifiers are defined, all types will be checked.\r\nThe default qualifier can be used to check against lack of all qualifiers.");
        labelACtiveConceptQualifiersInfo.setAlignment(SWT.CENTER);
        labelACtiveConceptQualifiersInfo.setImage(infoIcon);

        Composite qualifiersContainerComposite = new Composite(qualifiersAllComposite, SWT.NONE);
        GridLayout gl_qualifiersContainerComposite = new GridLayout(4, false);
        gl_qualifiersContainerComposite.marginHeight = 0;
        gl_qualifiersContainerComposite.marginWidth = 0;
        qualifiersContainerComposite.setLayout(gl_qualifiersContainerComposite);
        qualifiersContainerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        qualifiersContainerComposite.setBounds(0, 0, 64, 64);

        Composite activeConceptsListComposite = new Composite(qualifiersContainerComposite, SWT.NONE);
        activeConceptsListComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout gl_activeConceptsListComposite = new GridLayout(1, false);
        gl_activeConceptsListComposite.marginWidth = 0;
        gl_activeConceptsListComposite.marginHeight = 0;
        activeConceptsListComposite.setLayout(gl_activeConceptsListComposite);

        Label labelActiveConcepts = new Label(activeConceptsListComposite, SWT.NONE);
        labelActiveConcepts.setText("Active Concepts");

        activeConceptsList = new org.eclipse.swt.widgets.List(activeConceptsListComposite, SWT.BORDER | SWT.V_SCROLL);
        activeConceptsList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Composite availableQualifiersListComposite = new Composite(qualifiersContainerComposite, SWT.NONE);
        availableQualifiersListComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout gl_availableQualifiersListComposite = new GridLayout(1, false);
        gl_availableQualifiersListComposite.marginWidth = 0;
        gl_availableQualifiersListComposite.marginHeight = 0;
        availableQualifiersListComposite.setLayout(gl_availableQualifiersListComposite);

        Label labelAvailableQualifiersList = new Label(availableQualifiersListComposite, SWT.NONE);
        labelAvailableQualifiersList.setText("Available Qualifiers");

        availableQualifiersList = new org.eclipse.swt.widgets.List(availableQualifiersListComposite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
        availableQualifiersList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Composite qualifierTransferComposite = new Composite(qualifiersContainerComposite, SWT.NONE);
        GridData gd_qualifierTransferComposite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_qualifierTransferComposite.minimumWidth = 50;
        gd_qualifierTransferComposite.widthHint = 50;
        qualifierTransferComposite.setLayoutData(gd_qualifierTransferComposite);
        GridLayout gl_qualifierTransferComposite = new GridLayout(1, false);
        gl_qualifierTransferComposite.marginHeight = 0;
        gl_qualifierTransferComposite.marginWidth = 0;
        qualifierTransferComposite.setLayout(gl_qualifierTransferComposite);

        btnMoveQualifierToRight = new Button(qualifierTransferComposite, SWT.NONE);
        btnMoveQualifierToRight.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnMoveQualifierToRight.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
        btnMoveQualifierToRight.setText(">");

        btnMoveQualifierToLeft = new Button(qualifierTransferComposite, SWT.NONE);
        btnMoveQualifierToLeft.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnMoveQualifierToLeft.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
        btnMoveQualifierToLeft.setText("<");

        Composite activeQualifiersListComposite = new Composite(qualifiersContainerComposite, SWT.NONE);
        activeQualifiersListComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout gl_activeQualifiersListComposite = new GridLayout(1, false);
        gl_activeQualifiersListComposite.marginWidth = 0;
        gl_activeQualifiersListComposite.marginHeight = 0;
        activeQualifiersListComposite.setLayout(gl_activeQualifiersListComposite);

        Label labelActiveQualifiersList = new Label(activeQualifiersListComposite, SWT.NONE);
        labelActiveQualifiersList.setText("Active Qualifiers");

        activeQualifiersList = new org.eclipse.swt.widgets.List(activeQualifiersListComposite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
        activeQualifiersList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    }

    public void addCheckedConceptsListeners() {
        btnMoveToRight.addListener(SWT.MouseUp, (event) -> {
            for (String concept : availableCheckingsList.getSelection()) {
                activeCheckingsList.add(concept);
                IConceptService conceptService = getRegistry().getConceptService();
                checkedConcepts.add(conceptService.createConcept(conceptService.originalName(concept)));
                availableCheckingsList.remove(concept);
            }
            updateActiveConceptsList();
        });

        btnMoveToLeft.addListener(SWT.MouseUp, (event) -> {
            for (String concept : activeCheckingsList.getSelection()) {
                availableCheckingsList.add(concept);
                activeCheckingsList.remove(concept);
                IConceptService conceptService = getRegistry().getConceptService();
                checkedConcepts.remove(conceptService.createConcept(conceptService.originalName(concept)));
            }
            updateActiveConceptsList();
        });

        activeConceptsList.addListener(SWT.Selection, (event) -> {
            if (activeConceptsList.getSelectionCount() > 0) {
                enableQualifierLists(true);
                String conceptType = activeConceptsList.getItem(activeConceptsList.getSelectionIndex());
                IConcept foundConcept = findConcept(conceptType);
                if (foundConcept != null) {
                    List<String> foundQualifiers = foundConcept.getQualifiers();
                    activeQualifiersList.removeAll();
                    availableQualifiersList.removeAll();
                    IConceptService conceptService = getRegistry().getConceptService();
                    Optional<IConcept> oReferenceConcept = conceptService.getConcept(foundConcept.getType());
                    if (oReferenceConcept.isPresent()) {
                        IConcept referenceConcept = oReferenceConcept.get();
                        for (String qualifier : referenceConcept.getQualifiers()) {
                            if (foundQualifiers.contains(qualifier)) {
                                activeQualifiersList.add(qualifier);
                            } else {
                                availableQualifiersList.add(qualifier);
                            }
                        }
                    }
                }
            } else {
                enableQualifierLists(false);
            }
        });

        btnMoveQualifierToRight.addListener(SWT.MouseUp, (event) -> {
            if (activeConceptsList.getSelectionCount() > 0) {
                String conceptType = activeConceptsList.getItem(activeConceptsList.getSelectionIndex());
                IConcept foundConcept = findConcept(conceptType);
                if (availableQualifiersList.getSelectionCount() > 0) {
                    for (String qualifier : availableQualifiersList.getSelection()) {
                        activeQualifiersList.add(qualifier);
                        foundConcept.getQualifiers().add(qualifier);
                        availableQualifiersList.remove(qualifier);
                    }
                }
            }
        });

        btnMoveQualifierToLeft.addListener(SWT.MouseUp, (event) -> {
            if (activeConceptsList.getSelectionCount() > 0) {
                String conceptType = activeConceptsList.getItem(activeConceptsList.getSelectionIndex());
                IConcept foundConcept = findConcept(conceptType);
                if (activeQualifiersList.getSelectionCount() > 0) {
                    for (String qualifier : activeQualifiersList.getSelection()) {
                        availableQualifiersList.add(qualifier);
                        foundConcept.getQualifiers().remove(qualifier);
                        activeQualifiersList.remove(qualifier);
                    }
                }
            }
        });

    }

    private IConcept findConcept(String pConceptType) {
        IConceptService conceptService = getRegistry().getConceptService();
        IConcept foundConcept = checkedConcepts.get(checkedConcepts.indexOf(conceptService.createConcept(conceptService.originalName(pConceptType))));
        return foundConcept;
    }

    private void enableQualifierLists(Boolean pEnabled) {
        availableQualifiersList.setEnabled(pEnabled);
        activeQualifiersList.setEnabled(pEnabled);
        btnMoveQualifierToRight.setEnabled(pEnabled);
        btnMoveQualifierToLeft.setEnabled(pEnabled);
    }

    public void updateActiveConceptsList() {
        activeConceptsList.setItems(activeCheckingsList.getItems());
        availableQualifiersList.removeAll();
        activeQualifiersList.removeAll();
    }

    public Button getBtnMoveToRight() {
        return btnMoveToRight;
    }

    public Button getBtnMoveToLeft() {
        return btnMoveToLeft;
    }

    public Button getBtnMoveQualifierToRight() {
        return btnMoveQualifierToRight;
    }

    public Button getBtnMoveQualifierToLeft() {
        return btnMoveQualifierToLeft;
    }

    public List<IConcept> getCheckedConcepts() {
        return checkedConcepts;
    }

    public org.eclipse.swt.widgets.List getAvailableCheckingsList() {
        return availableCheckingsList;
    }

    public org.eclipse.swt.widgets.List getActiveCheckingsList() {
        return activeCheckingsList;
    }

    public org.eclipse.swt.widgets.List getActiveConceptsList() {
        return activeConceptsList;
    }

    public org.eclipse.swt.widgets.List getAvailableQualifiersList() {
        return availableQualifiersList;
    }

    public org.eclipse.swt.widgets.List getActiveQualifiersList() {
        return activeQualifiersList;
    }

    public void setBtnMoveToRight(Button btnMoveToRight) {
        this.btnMoveToRight = btnMoveToRight;
    }

    public void setBtnMoveToLeft(Button btnMoveToLeft) {
        this.btnMoveToLeft = btnMoveToLeft;
    }

    public void setBtnMoveQualifierToRight(Button btnMoveQualifierToRight) {
        this.btnMoveQualifierToRight = btnMoveQualifierToRight;
    }

    public void setBtnMoveQualifierToLeft(Button btnMoveQualifierToLeft) {
        this.btnMoveQualifierToLeft = btnMoveQualifierToLeft;
    }

    public void setCheckedConcepts(List<IConcept> pCheckedConcepts) {
        this.checkedConcepts = pCheckedConcepts;
    }

    public void setAvailableCheckingsList(org.eclipse.swt.widgets.List availableCheckingsList) {
        this.availableCheckingsList = availableCheckingsList;
    }

    public void setActiveCheckingsList(org.eclipse.swt.widgets.List activeCheckingsList) {
        this.activeCheckingsList = activeCheckingsList;
    }

    public void setActiveConceptsList(org.eclipse.swt.widgets.List activeConceptsList) {
        this.activeConceptsList = activeConceptsList;
    }

    public void setAvailableQualifiersList(org.eclipse.swt.widgets.List availableQualifiersList) {
        this.availableQualifiersList = availableQualifiersList;
    }

    public void setActiveQualifiersList(org.eclipse.swt.widgets.List activeQualifiersList) {
        this.activeQualifiersList = activeQualifiersList;
    }

}
