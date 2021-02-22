package com.cevelop.ctylechecker.preferences;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.cevelop.ctylechecker.ids.IdHelper;

import ch.hsr.ifs.iltis.core.preferences.IPropertyAndPreferenceHelper;
import ch.hsr.ifs.iltis.core.preferences.fieldeditor.GroupFieldEditor;
import ch.hsr.ifs.iltis.core.preferences.fieldeditor.GroupingFieldEditor;

import ch.hsr.ifs.iltis.cpp.core.preferences.CFieldEditorPropertyAndPreferencePage;


/**
 * @author tstauber
 */
public class PropertyAndPreferencePage extends CFieldEditorPropertyAndPreferencePage {

    private static final String          U_ICON_PATH                  = "resources/icons/logo.png";
    private static final ImageDescriptor IMAGE_DESCRIPTOR_FROM_PLUGIN = AbstractUIPlugin.imageDescriptorFromPlugin(IdHelper.DEFAULT_QUALIFIER,
            U_ICON_PATH);

    public PropertyAndPreferencePage() {
        super(IdHelper.PLUGIN_NAME, IMAGE_DESCRIPTOR_FROM_PLUGIN, GRID);
    }

    @Override
    protected String getPageId() {
        return IdHelper.P_PATTERNS_PROP_AND_PREF_PAGE_QUALIFIER;
    }

    @Override
    protected IPropertyAndPreferenceHelper createPropertyAndPreferenceHelper() {
        return PropAndPrefHelper.getInstance();
    }

    @Override
    protected void createFieldEditors() {
        final GroupingFieldEditor groupFieldEditor = new GroupFieldEditor(IdHelper.P_GENERAL_SETTINGS, "Expressions", getFieldEditorParent(), 1, 1);
        addField(groupFieldEditor);
    }
}
