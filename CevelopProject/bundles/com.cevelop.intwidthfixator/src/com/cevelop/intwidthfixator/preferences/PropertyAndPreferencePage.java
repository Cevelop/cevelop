package com.cevelop.intwidthfixator.preferences;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.cevelop.intwidthfixator.IntwidthfixatorPlugin;
import com.cevelop.intwidthfixator.helpers.IdHelper;
import com.cevelop.intwidthfixator.helpers.IdHelper.WidthId;

import ch.hsr.ifs.iltis.core.core.preferences.IPropertyAndPreferenceHelper;
import ch.hsr.ifs.iltis.core.core.preferences.fieldeditor.GroupFieldEditor;
import ch.hsr.ifs.iltis.core.core.preferences.fieldeditor.GroupingFieldEditor;

import ch.hsr.ifs.iltis.cpp.core.preferences.CFieldEditorPropertyAndPreferencePage;


/**
 * @author tstauber
 */
public class PropertyAndPreferencePage extends CFieldEditorPropertyAndPreferencePage {

    private static final String          U_ICON_PATH                  = "resources/icons/logo.png";
    private static final ImageDescriptor IMAGE_DESCRIPTOR_FROM_PLUGIN = AbstractUIPlugin.imageDescriptorFromPlugin(IdHelper.DEFAULT_QUALIFIER,
            U_ICON_PATH);

    public PropertyAndPreferencePage() {
        super(IntwidthfixatorPlugin.PLUGIN_NAME, IMAGE_DESCRIPTOR_FROM_PLUGIN, GRID);
    }

    @Override
    protected String getPageId() {
        return IdHelper.PROPERTY_AND_PREFERENCE_PAGE_QUALIFIER;
    }

    @Override
    protected IPropertyAndPreferenceHelper createPropertyAndPreferenceHelper() {
        return PropAndPrefHelper.getInstance();
    }

    @Override
    protected void createFieldEditors() {
        final GroupingFieldEditor groupFieldEditor = new GroupFieldEditor(IdHelper.P_GENERAL_SETTINGS, Messages.L_Caption, getFieldEditorParent(), 1,
                1);

        createRadioGrp(IdHelper.P_CHAR_PLATFORM_SIGNED_UNSIGNED, Messages.L_SignedQualifier, groupFieldEditor);
        createComboBox(IdHelper.P_CHAR_MAPPING, Messages.L_Char, groupFieldEditor);
        createComboBox(IdHelper.P_SHORT_MAPPING, Messages.L_Short, groupFieldEditor);
        createComboBox(IdHelper.P_INT_MAPPING, Messages.L_Int, groupFieldEditor);
        createComboBox(IdHelper.P_LONG_MAPPING, Messages.L_Long, groupFieldEditor);
        createComboBox(IdHelper.P_LONGLONG_MAPPING, Messages.L_LongLong, groupFieldEditor);
        addField(groupFieldEditor);
    }

    private void createComboBox(final String prefName, final String caption, final GroupingFieldEditor gfe) {
      // @formatter:off
      final String[][] labelAndValues = new String[][] { { Messages.L_Size8, WidthId.WIDTH_8.id },
         { Messages.L_Size16, WidthId.WIDTH_16.id }, { Messages.L_Size32, WidthId.WIDTH_32.id },
         { Messages.L_Size64, WidthId.WIDTH_64.id } };
         // @formatter:on

        gfe.addField(new ComboFieldEditor(prefName, NLS.bind(Messages.L_TargetSize, caption), labelAndValues, gfe.getFieldEditorParent()));
    }

    private void createRadioGrp(final String prefName, final String caption, final GroupingFieldEditor gfe) {
      // @formatter:off
      final String[][] labelAndValues = new String[][] { { Messages.L_Val_Signed, IdHelper.V_CHAR_PLATFORM_SIGNED },
         { Messages.L_Val_Unsigned, IdHelper.V_CHAR_PLATFORM_UNSIGNED } };
         // @formatter:on

        gfe.addField(new RadioGroupFieldEditor(prefName, caption, 2, labelAndValues, gfe.getFieldEditorParent()));
    }

}
