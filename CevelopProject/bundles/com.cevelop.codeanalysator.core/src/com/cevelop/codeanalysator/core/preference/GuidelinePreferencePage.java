package com.cevelop.codeanalysator.core.preference;

import com.cevelop.codeanalysator.core.CodeAnalysatorRuntime;
import com.cevelop.codeanalysator.core.helper.CoreIdHelper;
import com.cevelop.codeanalysator.core.helper.TranslationHelper;

import ch.hsr.ifs.iltis.core.preferences.IPropertyAndPreferenceHelper;

import ch.hsr.ifs.iltis.cpp.core.preferences.CFieldEditorPropertyAndPreferencePage;


public class GuidelinePreferencePage extends CFieldEditorPropertyAndPreferencePage {

    public GuidelinePreferencePage() {
        super(GRID);
    }

    @Override
    public void createFieldEditors() {
        addField(new GuidelineListEditor(CoreIdHelper.GUIDELINE_SETTING_ID, TranslationHelper.get("preferences.guidelineList"),
                getFieldEditorParent()));
    }

    @Override
    protected String getPageId() {
        return CoreIdHelper.GUIDELINE_SETTING_PAGE_ID;
    }

    @Override
    protected IPropertyAndPreferenceHelper createPropertyAndPreferenceHelper() {
        return CodeAnalysatorRuntime.getDefault().getPropAndPrefHelper();
    }
}
