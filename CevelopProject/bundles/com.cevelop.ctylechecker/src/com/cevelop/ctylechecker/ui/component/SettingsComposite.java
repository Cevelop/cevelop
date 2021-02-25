package com.cevelop.ctylechecker.ui.component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.cevelop.ctylechecker.domain.ConfigurationType;
import com.cevelop.ctylechecker.domain.IConfiguration;
import com.cevelop.ctylechecker.domain.IGrouping;
import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.domain.IStyleguide;
import com.cevelop.ctylechecker.ids.IdHelper;
import com.cevelop.ctylechecker.service.IConfigurationService;
import com.cevelop.ctylechecker.service.types.ConfigurationService;
import com.cevelop.ctylechecker.service.types.CtylecheckerRuntime;
import com.cevelop.ctylechecker.service.util.ConfigurationMapper;
import com.cevelop.ctylechecker.ui.AbstractCtylecheckerComposite;
import com.cevelop.ctylechecker.ui.SWTResourceManager;
import com.cevelop.ctylechecker.ui.dialog.GroupingDialog;
import com.cevelop.ctylechecker.ui.dialog.RuleDialog;
import com.cevelop.ctylechecker.ui.preferences.CtylecheckerPropertiesPage;
import com.cevelop.ctylechecker.ui.util.ItemDataInfo;


public class SettingsComposite extends AbstractCtylecheckerComposite {

    private IConfiguration             config;
    private CtylecheckerPropertiesPage propertyPage;

    private Combo     activeStyleguideCombo;
    private Tree      groupingsAndRulesTree;
    private Composite allContainerComposite;
    private Composite styleguideSettingsComposite;
    private Button    newActiveStyleguideButton;
    private Text      groupingsAndRulesFilterText;
    private Button    importStyleguide;
    private Button    exportStyleguide;

    /**
     * @param parent
     * The composite
     * @param pConfig
     * The configuration
     *
     * @wbp.parser.constructor
     */
    public SettingsComposite(Composite parent, IConfiguration pConfig) {
        super(parent, SWT.NONE);
        config = pConfig;
        createContents();
    }

    public SettingsComposite(Composite parent, IConfiguration pConfig, CtylecheckerPropertiesPage pPropertyPage) {
        super(parent, SWT.NONE);
        config = pConfig;
        propertyPage = pPropertyPage;
        createContents();
    }

    private void createContents() {
        GridLayout gl_rootComposite = new GridLayout(1, false);
        gl_rootComposite.marginWidth = 0;
        gl_rootComposite.marginHeight = 0;
        this.setLayout(gl_rootComposite);
        this.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        createHeader();
        if (propertyPage != null) {
            createPropertyPageSpecificContent();
        }
        createBody();
    }

    private void createHeader() {
        Composite headerComposite = new Composite(this, SWT.NONE);
        GridLayout gl_headerComposite = new GridLayout(2, false);
        gl_headerComposite.marginWidth = 0;
        gl_headerComposite.marginHeight = 0;
        headerComposite.setLayout(gl_headerComposite);
        headerComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label headerInfoLabel = new Label(headerComposite, SWT.NONE);
        headerInfoLabel.setText("C++ Styleguide Checker Plug-In.");

        Button moreInfo = new Button(headerComposite, SWT.NONE);
        moreInfo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
        moreInfo.setText(Messages.ABOUT);

        moreInfo.addListener(SWT.MouseUp, (event) -> CtylecheckerRuntime.showMessage(Messages.ABOUT, Messages.ABOUT_INFO));

        Label separator = new Label(headerComposite, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

    }

    private void createPropertyPageSpecificContent() {
        Composite useWorkspaceOrProjectSettingsComposite = new Composite(this, SWT.NONE);
        useWorkspaceOrProjectSettingsComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout gl_useWorkspaceOrProjectSettingsComposite = new GridLayout(1, false);
        gl_useWorkspaceOrProjectSettingsComposite.marginBottom = 5;
        gl_useWorkspaceOrProjectSettingsComposite.marginWidth = 0;
        gl_useWorkspaceOrProjectSettingsComposite.marginHeight = 0;
        useWorkspaceOrProjectSettingsComposite.setLayout(gl_useWorkspaceOrProjectSettingsComposite);

        Composite wrapperComposite = new Composite(useWorkspaceOrProjectSettingsComposite, SWT.NONE);
        wrapperComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        wrapperComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

        Composite radiosComposite = new Composite(wrapperComposite, SWT.NONE);
        radiosComposite.setLayout(new FillLayout(SWT.VERTICAL));

        Button btnUseWorkspace = new Button(radiosComposite, SWT.RADIO);
        btnUseWorkspace.setText("Use workspace settings");
        btnUseWorkspace.setSelection(config.isWorkspaceSetting());

        Button btnUseReference = new Button(radiosComposite, SWT.RADIO);
        btnUseReference.setText("Reference workspace settings");
        btnUseReference.setSelection(config.isReferenceSetting());

        Button btnUseProject = new Button(radiosComposite, SWT.RADIO);
        btnUseProject.setText("Use project settings");
        btnUseProject.setSelection(config.isProjectSetting());

        Listener listener = (event) -> {
            if (btnUseWorkspace.getSelection()) {
                config.setSetting(ConfigurationType.WORKSPACE);
                refresh();
            }
            if (btnUseReference.getSelection()) {
                config.setSetting(ConfigurationType.REFERENCE);
                refresh();
            }

            if (btnUseProject.getSelection()) {
                config.setSetting(ConfigurationType.PROJECT);
                refresh();
            }
            allContainerComposite.setVisible(config.isProjectSetting() || config.isReferenceSetting());
            styleguideSettingsComposite.setVisible(config.isProjectSetting());
            newActiveStyleguideButton.setVisible(config.isProjectSetting());
        };

        btnUseWorkspace.addListener(SWT.Selection, listener);
        btnUseReference.addListener(SWT.Selection, listener);
        btnUseProject.addListener(SWT.Selection, listener);

        Composite configureButtonComposite = new Composite(wrapperComposite, SWT.NONE);
        GridLayout gl_configureButtonComposite = new GridLayout(1, false);
        gl_configureButtonComposite.marginWidth = 0;
        gl_configureButtonComposite.marginHeight = 0;
        configureButtonComposite.setLayout(gl_configureButtonComposite);

        Button btnConfigureWorkspaceSettings = new Button(configureButtonComposite, SWT.NONE);
        btnConfigureWorkspaceSettings.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1, 1));
        Listener configureWorkspaceListener = (event) -> {
            openPreferencePage(IdHelper.CTYLECHECKER_PREFERENCES_PAGE_ID);
        };
        btnConfigureWorkspaceSettings.addListener(SWT.MouseUp, configureWorkspaceListener);
        btnConfigureWorkspaceSettings.addListener(SWT.Traverse, (event) -> {
            if (event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR || event.keyCode == SWT.TRAVERSE_RETURN) {
                configureWorkspaceListener.handleEvent(event);
            }
        });

        btnConfigureWorkspaceSettings.setText("Configure workspace settings");

        Label bottomHorizontalLine = new Label(useWorkspaceOrProjectSettingsComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
        bottomHorizontalLine.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    }

    private void createBody() {
        allContainerComposite = new Composite(this, SWT.NONE);
        allContainerComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));
        GridLayout gl_allContainerComposite = new GridLayout(1, false);
        gl_allContainerComposite.marginWidth = 0;
        gl_allContainerComposite.marginHeight = 0;
        allContainerComposite.setLayout(gl_allContainerComposite);
        allContainerComposite.setVisible(propertyPage == null || (config.isReferenceSetting() || config.isProjectSetting()));

        Composite enableCheckBoxComposite = new Composite(allContainerComposite, SWT.NONE);
        GridLayout gl_enableCheckBoxComposite = new GridLayout(3, false);
        gl_enableCheckBoxComposite.marginWidth = 0;
        gl_enableCheckBoxComposite.marginHeight = 0;
        enableCheckBoxComposite.setLayout(gl_enableCheckBoxComposite);
        enableCheckBoxComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        Button btnCheckButton = new Button(enableCheckBoxComposite, SWT.CHECK);
        btnCheckButton.setSelection(config.isEnabled());
        btnCheckButton.addListener(SWT.Selection, (event) -> {
            config.isEnabled(btnCheckButton.getSelection());
        });
        btnCheckButton.setText("Enable Ctylechecker");

        Label checkButtonInfoLabel = new Label(enableCheckBoxComposite, SWT.NONE);
        checkButtonInfoLabel.setImage(new Image(Display.getDefault(), Display.getDefault().getSystemImage(SWT.ICON_INFORMATION).getImageData()
                .scaledTo(20, 20)));
        checkButtonInfoLabel.setToolTipText("This setting is independent from the Codan Checker Preferences." +
                                            "\r\nIt enables/disables checking internally. Check the Codan Settings and make sure" +
                                            "\r\nDynamic Style Problem and Dynamic Style Problem for Files is active on workspace" +
                                            "\r\nand project level in order for the Ctylechecker to work properly." +
                                            "\r\nRemember to save Codan related settings via the Codan Page.");

        Composite codanComposite = new Composite(enableCheckBoxComposite, SWT.NONE);
        GridLayout gl_codanComposite = new GridLayout(1, false);
        gl_codanComposite.marginWidth = 0;
        gl_codanComposite.marginHeight = 0;
        codanComposite.setLayout(gl_codanComposite);
        codanComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        // TODO(tstauber - May 20, 2019) REMOVE AFTER TESTING
        //        Button btnGoToCodanSettings = new Button(codanComposite, SWT.NONE);
        //        btnGoToCodanSettings.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
        //        btnGoToCodanSettings.setText("Codan Settings...");
        //
        //        btnGoToCodanSettings.addListener(SWT.MouseUp, (event) -> {
        //            if (propertyPage != null) {
        //                openPropertyPage(CODAN_PROPERTIES_PAGE_ID);
        //            } else {
        //                openPreferencePage(CODAN_PREFERENCES_PAGE_ID);
        //            }
        //        });

        Composite activeStyleguideTitleComposite = new Composite(allContainerComposite, SWT.NONE);
        activeStyleguideTitleComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout gl_activeStyleguideTitleComposite = new GridLayout(1, false);
        gl_activeStyleguideTitleComposite.marginWidth = 0;
        gl_activeStyleguideTitleComposite.marginHeight = 0;
        activeStyleguideTitleComposite.setLayout(gl_activeStyleguideTitleComposite);

        Label activeStyleguideLabel = new Label(activeStyleguideTitleComposite, SWT.NONE);
        activeStyleguideLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        activeStyleguideLabel.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
        activeStyleguideLabel.setText("Active Styleguide");

        Composite activeStyleguideComposite = new Composite(allContainerComposite, SWT.NONE);
        GridLayout gl_activeStyleguideComposite = new GridLayout(4, false);
        gl_activeStyleguideComposite.marginWidth = 0;
        gl_activeStyleguideComposite.marginHeight = 0;
        activeStyleguideComposite.setLayout(gl_activeStyleguideComposite);
        activeStyleguideComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        activeStyleguideCombo = new Combo(activeStyleguideComposite, SWT.READ_ONLY);
        activeStyleguideCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        activeStyleguideCombo.setItems();
        activeStyleguideCombo.select(0);

        Listener activeStyleguideChangeListener = (event) -> {
            Optional<IStyleguide> styleguide = config.findStyleGuide(activeStyleguideCombo.getText());
            if (styleguide.isPresent()) {
                config.setActiveStyleguide(styleguide.get());
                refreshGroupingsAndRulesTree();
            }
        };

        activeStyleguideCombo.addListener(SWT.Selection, activeStyleguideChangeListener);

        refreshActiveStyleguideCombo();

        newActiveStyleguideButton = new Button(activeStyleguideComposite, SWT.CENTER);
        newActiveStyleguideButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        newActiveStyleguideButton.setText("New");
        newActiveStyleguideButton.setVisible(propertyPage == null || config.isProjectSetting());

        importStyleguide = new Button(activeStyleguideComposite, SWT.NONE);
        importStyleguide.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        importStyleguide.setText("Import");
        importStyleguide.addListener(SWT.MouseUp, (event) -> {
            FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
            dialog.setFilterNames(new String[] { "Ctylechecker Styleguide" });
            dialog.setFilterExtensions(new String[] { "*.ctyleguide" });
            dialog.setText("Import Styleguide");
            String filePath = dialog.open();
            if (filePath != null) {
                try {
                    byte[] bytes = Files.readAllBytes(Paths.get(filePath));
                    String styleguideJson = new String(bytes, StandardCharsets.UTF_8);
                    IStyleguide importedStyleguide = ConfigurationMapper.fromJson(styleguideJson, IStyleguide.class);
                    IStyleguide styleguideCopy = getRegistry().getStyleguideService().makeCopy(importedStyleguide);
                    adjustImportedStyleguideName(styleguideCopy);
                    config.addStyleguide(styleguideCopy);
                    activeStyleguideCombo.add(styleguideCopy.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        exportStyleguide = new Button(activeStyleguideComposite, SWT.NONE);
        exportStyleguide.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        exportStyleguide.setText("Export");
        new Label(activeStyleguideComposite, SWT.NONE);

        Button btnRemove = new Button(activeStyleguideComposite, SWT.NONE);
        btnRemove.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnRemove.setText("Remove");

        btnRemove.addListener(SWT.MouseUp, (event) -> {
            String activestyleguideName = activeStyleguideCombo.getText();
            //			if(activestyleguideName.equals(StyleguideFactory.GOOGLE_STYLEGUIDE)
            //					|| activestyleguideName.equals(StyleguideFactory.CANONICAL_STYLEGUIDE)
            //					|| activestyleguideName.equals(StyleguideFactory.GEOSOFT_STYLEGUIDE)) {
            //				CtylecheckerRuntime.showMessage("Info", "Can't remove predefined Styleguides.");
            //				return;
            //			}
            if (activestyleguideName.isEmpty()) {
                CtylecheckerRuntime.showMessage("Info", "No selection found. Try selecting an active styleguide first.");
                return;
            }
            Optional<IStyleguide> styleguide = config.findStyleGuide(activestyleguideName);
            if (styleguide.isPresent()) {
                config.removeStyleguide(styleguide.get());
                config.setActiveStyleguide(getRegistry().getStyleguideService().createStyleguide(""));
                activeStyleguideCombo.remove(activeStyleguideCombo.getSelectionIndex());
                activeStyleguideCombo.select(0);
                activeStyleguideChangeListener.handleEvent(null);
            }
        });

        exportStyleguide.addListener(SWT.MouseUp, (event) -> {
            FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
            dialog.setFilterNames(new String[] { "Ctylechecker Styleguide (*.ctyleguide)" });
            dialog.setFilterExtensions(new String[] { "*.ctyleguide" });
            dialog.setText("Export Styleguide");
            String filePath = dialog.open();
            if (filePath != null) {
                try {
                    Files.write(Paths.get(filePath), ConfigurationMapper.toJson(config.getActiveStyleguide()).getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        styleguideSettingsComposite = new Composite(allContainerComposite, SWT.NONE);
        styleguideSettingsComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout gl_styleguideSettingsComposite = new GridLayout(1, false);
        gl_styleguideSettingsComposite.marginWidth = 0;
        gl_styleguideSettingsComposite.marginHeight = 0;
        styleguideSettingsComposite.setLayout(gl_styleguideSettingsComposite);
        styleguideSettingsComposite.setVisible(propertyPage == null || config.isProjectSetting());

        Composite groupingsAndRulesLabelComposite = new Composite(styleguideSettingsComposite, SWT.NONE);
        groupingsAndRulesLabelComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout gl_groupingsAndRulesLabelComposite = new GridLayout(1, false);
        gl_groupingsAndRulesLabelComposite.marginWidth = 0;
        groupingsAndRulesLabelComposite.setLayout(gl_groupingsAndRulesLabelComposite);

        Label groupingsAndRulesLabel = new Label(groupingsAndRulesLabelComposite, SWT.NONE);
        groupingsAndRulesLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));
        groupingsAndRulesLabel.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
        groupingsAndRulesLabel.setText("Groupings and Rules");

        groupingsAndRulesFilterText = new Text(groupingsAndRulesLabelComposite, SWT.BORDER);
        groupingsAndRulesFilterText.setMessage("type filter text");
        groupingsAndRulesFilterText.setToolTipText("type filter text");
        groupingsAndRulesFilterText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        groupingsAndRulesFilterText.addListener(SWT.Modify, (event) -> {
            refreshGroupingsAndRulesTree();
        });

        Composite groupingsAndRulesTreeComposite = new Composite(styleguideSettingsComposite, SWT.NONE);
        groupingsAndRulesTreeComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout gl_groupingsAndRulesTreeComposite = new GridLayout(2, false);
        gl_groupingsAndRulesTreeComposite.marginWidth = 0;
        gl_groupingsAndRulesTreeComposite.horizontalSpacing = 10;
        groupingsAndRulesTreeComposite.setLayout(gl_groupingsAndRulesTreeComposite);

        groupingsAndRulesTree = new Tree(groupingsAndRulesTreeComposite, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL);
        GridData gd_groupingsAndRulesTree = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
        gd_groupingsAndRulesTree.heightHint = 240;
        gd_groupingsAndRulesTree.minimumWidth = 320;
        gd_groupingsAndRulesTree.minimumHeight = 240;
        groupingsAndRulesTree.setLayoutData(gd_groupingsAndRulesTree);

        refreshGroupingsAndRulesTree();

        Composite groupingAndRulesButtonsComposite = new Composite(groupingsAndRulesTreeComposite, SWT.NONE);
        FillLayout fl_groupingAndRulesButtonsComposite = new FillLayout(SWT.VERTICAL);
        fl_groupingAndRulesButtonsComposite.spacing = 5;
        groupingAndRulesButtonsComposite.setLayout(fl_groupingAndRulesButtonsComposite);
        GridData gd_groupingAndRulesButtonsComposite = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        gd_groupingAndRulesButtonsComposite.widthHint = 120;
        groupingAndRulesButtonsComposite.setLayoutData(gd_groupingAndRulesButtonsComposite);

        Button btnNewGrouping = new Button(groupingAndRulesButtonsComposite, SWT.NONE);
        btnNewGrouping.setText("Add Group");
        btnNewGrouping.addListener(SWT.MouseUp, (event) -> {
            GroupingDialog dialog = new GroupingDialog(getShell(), "Add Group", config, null);
            if (dialog.open() == SWT.OK) {
                refreshGroupingsAndRulesTree();
            }
        });

        Button btnNewRule = new Button(groupingAndRulesButtonsComposite, SWT.NONE);
        btnNewRule.setText("Add Rule");
        btnNewRule.addListener(SWT.MouseUp, (event) -> {
            IGrouping ruleRoot = null;
            if (groupingsAndRulesTree.getSelection().length > 0) {
                TreeItem item = groupingsAndRulesTree.getSelection()[0];
                if (item.getData(ItemDataInfo.DATA_TYPE_KEY).equals(ItemDataInfo.DATA_GROUP_VALUE)) {
                    String id = item.getData(ItemDataInfo.DATA_ID_KEY).toString();
                    ruleRoot = config.getActiveStyleguide().getGrouping(UUID.fromString(id));
                }
            }
            RuleDialog dialog = new RuleDialog(getShell(), "Add Rule", config, ruleRoot);
            if (dialog.open() == SWT.OK) {
                refreshGroupingsAndRulesTree();
            }
        });

        Composite groupingsAndRulesButtonsComposite = new Composite(styleguideSettingsComposite, SWT.NONE);
        groupingsAndRulesButtonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout gl_groupingsAndRulesButtonsComposite = new GridLayout(2, false);
        gl_groupingsAndRulesButtonsComposite.marginWidth = 0;
        gl_groupingsAndRulesButtonsComposite.marginHeight = 0;
        gl_groupingsAndRulesButtonsComposite.verticalSpacing = 3;
        groupingsAndRulesButtonsComposite.setLayout(gl_groupingsAndRulesButtonsComposite);

        Button btnCustomizeSelected = new Button(groupingsAndRulesButtonsComposite, SWT.NONE);
        GridData gd_btnCustomizeSelected = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
        gd_btnCustomizeSelected.widthHint = 120;
        btnCustomizeSelected.setLayoutData(gd_btnCustomizeSelected);
        btnCustomizeSelected.setText("Customize Selected");
        btnCustomizeSelected.addListener(SWT.MouseUp, (event) -> {
            if (groupingsAndRulesTree.getSelection().length > 0) {
                TreeItem item = groupingsAndRulesTree.getSelection()[0];
                String id = item.getData(ItemDataInfo.DATA_ID_KEY).toString();
                if (item.getData(ItemDataInfo.DATA_TYPE_KEY).equals(ItemDataInfo.DATA_GROUP_VALUE)) {
                    IGrouping grouping = config.getActiveStyleguide().getGrouping(UUID.fromString(id));
                    GroupingDialog dialog = new GroupingDialog(getShell(), "Customize Group", config, grouping);
                    if (dialog.open() == SWT.OK) {
                        refreshGroupingsAndRulesTree();
                    }
                } else if (item.getData(ItemDataInfo.DATA_TYPE_KEY).equals(ItemDataInfo.DATA_RULE_VALUE)) {
                    IRule rule = config.getActiveStyleguide().getRule(UUID.fromString(id));
                    RuleDialog dialog = new RuleDialog(getShell(), "Customize Rule", config, rule);
                    if (dialog.open() == SWT.OK) {
                        refreshGroupingsAndRulesTree();
                    }
                }
            }
        });

        Button btnRemoveSelected = new Button(groupingsAndRulesButtonsComposite, SWT.NONE);
        GridData gd_btnRemoveSelected = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
        gd_btnRemoveSelected.widthHint = 120;
        btnRemoveSelected.setLayoutData(gd_btnRemoveSelected);
        btnRemoveSelected.setText("Remove Selected");
        btnRemoveSelected.addListener(SWT.MouseUp, (event) -> {
            TreeItem[] selectedItems = groupingsAndRulesTree.getSelection();
            IStyleguide activeStyleguide = config.getActiveStyleguide();
            for (TreeItem item : selectedItems) {
                if (item.getData(ItemDataInfo.DATA_TYPE_KEY).equals(ItemDataInfo.DATA_GROUP_VALUE)) {
                    UUID id;
                    id = UUID.fromString(item.getData(ItemDataInfo.DATA_ID_KEY).toString());
                    activeStyleguide.removeGrouping(id);
                }
                if (item.getData(ItemDataInfo.DATA_TYPE_KEY).equals(ItemDataInfo.DATA_RULE_VALUE)) {
                    UUID id;
                    id = UUID.fromString(item.getData(ItemDataInfo.DATA_ID_KEY).toString());
                    activeStyleguide.removeRule(id);
                }
                item.dispose();
            }
        });

        newActiveStyleguideButton.addListener(SWT.MouseUp, event -> {
            InputDialog inputDialog = new InputDialog(getShell(), "New Styleguide", "Name", "", null);
            inputDialog.open();
            String input = inputDialog.getValue();
            if (input != null && !input.isEmpty()) {
                activeStyleguideCombo.add(input);
                config.addStyleguide(input);
            }
        });
    }

    private void adjustImportedStyleguideName(IStyleguide importedStyleguide) {
        Boolean duplicationEliminated = false;
        while (!duplicationEliminated) {
            duplicationEliminated = true;
            for (String item : activeStyleguideCombo.getItems()) {
                if (item.equals(importedStyleguide.getName())) {
                    importedStyleguide.setName(importedStyleguide.getName() + " copy");
                    duplicationEliminated = false;
                }
            }
        }
    }

    private void openPreferencePage(String pPageId) {
        PreferenceDialog preferenceDialog = PreferencesUtil.createPreferenceDialogOn(getShell(), pPageId, new String[] {
                                                                                                                         IdHelper.CTYLECHECKER_PREFERENCES_PAGE_ID,
                                                                                                                         IdHelper.CODAN_PREFERENCES_PAGE_ID },
                null);
        if (preferenceDialog != null) {
            preferenceDialog.open();
        }
    }

    private void openPropertyPage(String pPageId) {
        IConfigurationService configService = new ConfigurationService();
        Optional<IProject> oProject = configService.getActiveProject();
        if (oProject.isPresent()) {
            PreferenceDialog preferenceDialog = PreferencesUtil.createPropertyDialogOn(getShell(), oProject.get(), pPageId, new String[] {
                                                                                                                                           IdHelper.CTYLECHECKER_PROPERTIES_PAGE_ID,
                                                                                                                                           IdHelper.CODAN_PROPERTIES_PAGE_ID },
                    null);
            if (preferenceDialog != null) {
                preferenceDialog.open();
            }
        } else {
            CtylecheckerRuntime.showMessage("Info", "Resource for property page couldn't be retrieved.");
        }
    }

    private void refresh() {
        if (propertyPage != null) {
            config = propertyPage.loadConfigurationCallback(config.getSetting());
            refreshActiveStyleguideCombo();
            refreshGroupingsAndRulesTree();
        }
    }

    public void refreshActiveStyleguideCombo() {
        if (activeStyleguideCombo != null) {
            activeStyleguideCombo.removeAll();
            for (IStyleguide styleguide : config.getStyleguides()) {
                activeStyleguideCombo.add(styleguide.getName());
            }
            String[] items = activeStyleguideCombo.getItems();
            for (int i = 0; i < items.length; i++) {
                if (items[i].equals(config.getActiveStyleguide().getName())) {
                    activeStyleguideCombo.select(i);
                }
            }
        }
    }

    public void refreshGroupingsAndRulesTree() {
        if (groupingsAndRulesTree != null) {
            groupingsAndRulesTree.removeAll();
            Pattern pattern = Pattern.compile(groupingsAndRulesFilterText.getText(), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Image groupImage = CtylecheckerRuntime.imageDescriptorFromPlugin("/resources/group_img.jpg").createImage();
            Image ruleImage = CtylecheckerRuntime.imageDescriptorFromPlugin("/resources/rule_img.jpg").createImage();
            for (IGrouping grouping : config.getActiveStyleguide().getGroupings()) {
                TreeItem item = new TreeItem(groupingsAndRulesTree, SWT.CHECK);
                item.setText(grouping.getName());
                item.setData(ItemDataInfo.DATA_TYPE_KEY, ItemDataInfo.DATA_GROUP_VALUE);
                item.setData(ItemDataInfo.DATA_ID_KEY, grouping.getId());
                item.setChecked(grouping.isEnabled());
                item.setImage(groupImage);
                for (IRule rule : grouping.getRules()) {
                    if (!pattern.matcher(rule.getName()).find()) {
                        continue;
                    }
                    TreeItem subItem = new TreeItem(item, SWT.CHECK);
                    subItem.setText(rule.getName());
                    subItem.setData(ItemDataInfo.DATA_TYPE_KEY, ItemDataInfo.DATA_RULE_VALUE);
                    subItem.setData(ItemDataInfo.DATA_ID_KEY, rule.getId());
                    subItem.setChecked(rule.isEnabled());
                    subItem.setImage(ruleImage);
                }
                item.setExpanded(true);
                if (item.getItemCount() == 0 && !pattern.matcher(grouping.getName()).find()) {
                    item.dispose();
                }
            }
            for (IRule rule : config.getActiveStyleguide().getRules()) {
                if (!pattern.matcher(rule.getName()).find()) {
                    continue;
                }
                TreeItem item = new TreeItem(groupingsAndRulesTree, SWT.CHECK);
                item.setText(rule.getName());
                item.setData(ItemDataInfo.DATA_TYPE_KEY, ItemDataInfo.DATA_RULE_VALUE);
                item.setData(ItemDataInfo.DATA_ID_KEY, rule.getId());
                item.setChecked(rule.isEnabled());
                item.setImage(ruleImage);
            }
            groupingsAndRulesTree.addListener(SWT.Selection, (event) -> {
                if (event.detail == SWT.CHECK) {
                    String id = event.item.getData(ItemDataInfo.DATA_ID_KEY).toString();
                    if (event.item.getData(ItemDataInfo.DATA_TYPE_KEY).equals(ItemDataInfo.DATA_GROUP_VALUE)) {
                        IGrouping grouping = config.getActiveStyleguide().getGrouping(UUID.fromString(id));
                        grouping.isEnabled(((TreeItem) event.item).getChecked());
                    }
                    if (event.item.getData(ItemDataInfo.DATA_TYPE_KEY).equals(ItemDataInfo.DATA_RULE_VALUE)) {
                        IRule rule = config.getActiveStyleguide().getRule(UUID.fromString(id));
                        rule.isEnabled(((TreeItem) event.item).getChecked());
                    }
                }
            });
        }
    }
}
