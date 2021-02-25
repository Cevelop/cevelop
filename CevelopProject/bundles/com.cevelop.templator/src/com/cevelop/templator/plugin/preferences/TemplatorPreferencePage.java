package com.cevelop.templator.plugin.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.cevelop.templator.plugin.TemplatorPlugin;


public class TemplatorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public TemplatorPreferencePage() {
        super(GRID);
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(TemplatorPlugin.getDefault().getPreferenceStore());
    }

    @Override
    protected void createFieldEditors() {
        addField(new BooleanFieldEditor(TemplatorPreference.HAS_MAX_WIDTH.getKey(), TemplatorPreference.HAS_MAX_WIDTH.getDescription(),
                getFieldEditorParent()));
        addField(new BooleanFieldEditor(TemplatorPreference.HAS_MAX_HEIGHT.getKey(), TemplatorPreference.HAS_MAX_HEIGHT.getDescription(),
                getFieldEditorParent()));
        addField(new BooleanFieldEditor(TemplatorPreference.RECTANGLE_ALWAYS_VISIBLE.getKey(), TemplatorPreference.RECTANGLE_ALWAYS_VISIBLE
                .getDescription(), getFieldEditorParent()));
        addField(new BooleanFieldEditor(TemplatorPreference.DISABLE_NORMAL_FUNCTIONS.getKey(), TemplatorPreference.DISABLE_NORMAL_FUNCTIONS
                .getDescription(), getFieldEditorParent()));
        addField(new BooleanFieldEditor(TemplatorPreference.DISABLE_NORMAL_CLASSES.getKey(), TemplatorPreference.DISABLE_NORMAL_CLASSES
                .getDescription(), getFieldEditorParent()));
        addField(new BooleanFieldEditor(TemplatorPreference.DISABLE_AUTO_SPECIFIER.getKey(), TemplatorPreference.DISABLE_AUTO_SPECIFIER
                .getDescription(), getFieldEditorParent()));
        addField(new ColorFieldEditor(TemplatorPreference.HOVER_COLOR.getKey(), TemplatorPreference.HOVER_COLOR.getDescription(),
                getFieldEditorParent()));
        addField(new ColorFieldEditor(TemplatorPreference.RECTANGLE_COLOR.getKey(), TemplatorPreference.RECTANGLE_COLOR.getDescription(),
                getFieldEditorParent()));
    }
}
