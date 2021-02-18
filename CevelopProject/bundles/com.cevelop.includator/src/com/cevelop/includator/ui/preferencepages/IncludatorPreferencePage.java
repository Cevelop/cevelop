package com.cevelop.includator.ui.preferencepages;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.preferences.IncludatorPropertyManager;
import com.cevelop.includator.ui.components.LblFlowingBooleanFieldEditor;


/**
 * This class represents a preference page that is contributed to the Preferences dialog. By subclassing {@link FieldEditorPreferencePage}, we
 * can use the field support built into JFace that allows us to create a page that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store that belongs to the main plug-in class. That way, preferences
 * can be accessed directly via the preference store.
 */

public class IncludatorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public IncludatorPreferencePage() {
        super(GRID);
        setPreferenceStore(IncludatorPlugin.getDefault().getPreferenceStore());
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
        RadioGroupFieldEditor uncheckedActionSelector = new RadioGroupFieldEditor(propertyName, lbl, 3, labelAndValues, group);
        addField(uncheckedActionSelector);
    }

    private void addCheckboxSettingsGroup() {
        Group settingsGroup = new Group(getFieldEditorParent(), SWT.SHADOW_ETCHED_IN);
        settingsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        settingsGroup.setText("General Includator Settings:");

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

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    @Override
    public void init(IWorkbench workbench) {}

}
