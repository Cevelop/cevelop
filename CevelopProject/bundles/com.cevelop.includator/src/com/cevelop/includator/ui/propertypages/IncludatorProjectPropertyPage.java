package com.cevelop.includator.ui.propertypages;

import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbenchPropertyPage;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.preferences.IncludatorPropertyManager;
import com.cevelop.includator.ui.components.LblFlowingBooleanFieldEditor;
import com.cevelop.includator.ui.helpers.ResourceWrapperStore;


public class IncludatorProjectPropertyPage extends FieldEditorPreferencePage implements IWorkbenchPropertyPage {

    IAdaptable                         element;
    private final ResourceWrapperStore store;

    public IncludatorProjectPropertyPage() {
        super(GRID);
        store = new ResourceWrapperStore();
        setPreferenceStore(store);
        setDescription("General Includator Settings:");
    }

    @Override
    public void createFieldEditors() {
        addCheckboxSettingsGroup();

        addDefaultActionForSuggestionsInSuggestionDialog();
    }

    private void addDefaultActionForSuggestionsInSuggestionDialog() {
        Group group = new Group(getFieldEditorParent(), SWT.SHADOW_ETCHED_IN);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        String lbl = "Default Suggestion-Dialog Action:";
        String addMarkerText = Suggestion.SOLUTION_OPERATION_ADD_MARKER.getColumnDispalyName();
        String addMarkerValue = Suggestion.SOLUTION_OPERATION_ADD_MARKER.getClass().getName();
        String applyChangeText = Suggestion.SOLUTION_OPERATION_APPLY_DEFAULT_CHANGE.getColumnDispalyName();
        String applyChangeValue = Suggestion.SOLUTION_OPERATION_APPLY_DEFAULT_CHANGE.getClass().getName();
        String doNothingText = Suggestion.SOLUTION_OPERATION_DO_NOTHING.getColumnDispalyName();
        String doNothingValue = Suggestion.SOLUTION_OPERATION_DO_NOTHING.getClass().getName();
        String[][] labelAndValues = new String[][] { { addMarkerText, addMarkerValue }, { applyChangeText, applyChangeValue }, { doNothingText,
                                                                                                                                 doNothingValue } };
        String propertyName = IncludatorPropertyManager.DEFAULT_SUGGESTION_DIALOG_OPERATION;
        addField(new RadioGroupFieldEditor(propertyName, lbl, 3, labelAndValues, group));
    }

    private void addCheckboxSettingsGroup() {
        Group settingsGroup = new Group(getFieldEditorParent(), SWT.SHADOW_ETCHED_IN);
        settingsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        //settingsGroup.setText("General Includator Settings:");

        String removeCoveredLabel = "Suggest to remove includes that are transitively covered through others.";
        addFlowingBooleanField(IncludatorPropertyManager.SUGGEST_REMOVAL_OF_COVERED_INCLUDES_PREFERENCE_NAME, removeCoveredLabel, settingsGroup);

        String neverShowSuggestionDialogLabel = "Never show suggestion-dialog (directly execute the default action chosen bellow).";
        addFlowingBooleanField(IncludatorPropertyManager.NEVER_SHOW_SUGGESTION_DIALOG, neverShowSuggestionDialogLabel, settingsGroup);

        String dontRemoveCorrelatingHeaderLabel =
                                                "Do never suggest to remove an include to a file-name-correlating header (don't remove '#include \"Foo.h\"' from 'Foo.cpp').";
        String correlatingHeaderName = IncludatorPropertyManager.DONT_SUGGEST_REMOVAL_OF_NAME_CORRELATING_HEADER;
        addFlowingBooleanField(correlatingHeaderName, dontRemoveCorrelatingHeaderLabel, settingsGroup);

        String addIncludesToOtherProjectHeadersAsSystemIncludes =
                                                                "Add includes to headers of other project as system includes ('#include <..>' instead of '#include \"...\"'.";
        addFlowingBooleanField(IncludatorPropertyManager.ADD_INCLUDES_TO_OTHER_PROJECTS_AS_SYSTEM_INCLUDE,
                addIncludesToOtherProjectHeadersAsSystemIncludes, settingsGroup);
    }

    private void addFlowingBooleanField(String name, String label, Composite parent) {
        addField(new LblFlowingBooleanFieldEditor(name, label, parent));
    }

    @Override
    public IAdaptable getElement() {
        return element;
    }

    @Override
    public void setElement(IAdaptable element) {
        this.element = element;
        store.setResource(getProject());
        IPreferenceStore defaultStore = IncludatorPlugin.getDefault().getPreferenceStore();

        boolean removeCoveredDefaultValue = defaultStore.getBoolean(IncludatorPropertyManager.SUGGEST_REMOVAL_OF_COVERED_INCLUDES_PREFERENCE_NAME);
        store.setDefault(IncludatorPropertyManager.SUGGEST_REMOVAL_OF_COVERED_INCLUDES_PREFERENCE_NAME, removeCoveredDefaultValue);

        boolean noDialogDefaultValue = defaultStore.getBoolean(IncludatorPropertyManager.NEVER_SHOW_SUGGESTION_DIALOG);
        store.setDefault(IncludatorPropertyManager.NEVER_SHOW_SUGGESTION_DIALOG, noDialogDefaultValue);

        boolean dontRemoveNameCorrelatingHeaderDefaultValue = defaultStore.getBoolean(
                IncludatorPropertyManager.DONT_SUGGEST_REMOVAL_OF_NAME_CORRELATING_HEADER);
        store.setDefault(IncludatorPropertyManager.DONT_SUGGEST_REMOVAL_OF_NAME_CORRELATING_HEADER, dontRemoveNameCorrelatingHeaderDefaultValue);

        boolean otherProjAsSystemIncludeDefaultValue = defaultStore.getBoolean(
                IncludatorPropertyManager.ADD_INCLUDES_TO_OTHER_PROJECTS_AS_SYSTEM_INCLUDE);
        store.setDefault(IncludatorPropertyManager.ADD_INCLUDES_TO_OTHER_PROJECTS_AS_SYSTEM_INCLUDE, otherProjAsSystemIncludeDefaultValue);

        String defaultDialogActionDefaultValue = defaultStore.getString(IncludatorPropertyManager.DEFAULT_SUGGESTION_DIALOG_OPERATION);
        store.setDefault(IncludatorPropertyManager.DEFAULT_SUGGESTION_DIALOG_OPERATION, defaultDialogActionDefaultValue);
    }

    private IProject getProject() {
        IAdaptable element = getElement();
        if (element instanceof ICProject) {
            return ((ICProject) element).getProject();
        } else if (element instanceof IProject) {
            return (IProject) element;
        }
        return null;
    }

    @Override
    protected void performDefaults() {
        // setting all values to null is required here, otherwise pressing ok after reset to defaults will not have an effect.
        store.setValue(IncludatorPropertyManager.SUGGEST_REMOVAL_OF_COVERED_INCLUDES_PREFERENCE_NAME, null);
        store.setValue(IncludatorPropertyManager.NEVER_SHOW_SUGGESTION_DIALOG, null);
        store.setValue(IncludatorPropertyManager.DONT_SUGGEST_REMOVAL_OF_NAME_CORRELATING_HEADER, null);
        store.setValue(IncludatorPropertyManager.ADD_INCLUDES_TO_OTHER_PROJECTS_AS_SYSTEM_INCLUDE, null);
        store.setValue(IncludatorPropertyManager.DEFAULT_SUGGESTION_DIALOG_OPERATION, null);
        super.performDefaults();
    }
}
