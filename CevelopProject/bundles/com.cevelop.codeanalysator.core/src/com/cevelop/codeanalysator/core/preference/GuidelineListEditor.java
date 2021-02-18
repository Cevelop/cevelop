package com.cevelop.codeanalysator.core.preference;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ListDialog;

import com.cevelop.codeanalysator.core.CodeAnalysatorRuntime;
import com.cevelop.codeanalysator.core.guideline.IGuideline;
import com.cevelop.codeanalysator.core.guideline.GuidelineRegistry;
import com.cevelop.codeanalysator.core.guideline.IGuidelinePreferences;
import com.cevelop.codeanalysator.core.helper.TranslationHelper;


public class GuidelineListEditor extends ListEditor {

    private GuidelineRegistry guidelineRegistry;

    public GuidelineListEditor(String name, String labelText, Composite parent) {
        super(name, labelText, parent);
        getAddButton().setText(TranslationHelper.get("preferences.addGuidelineBtn"));
        guidelineRegistry = CodeAnalysatorRuntime.getDefault().getGuidelineRegistry();
    }

    @Override
    protected String getNewInputObject() {
        List<String> selectedItems = Arrays.asList(this.getList().getItems());
        String[] availableGuidelines = guidelineRegistry.getGuidelines().stream() //
                .map(IGuideline::getName) //
                .filter(guidelineName -> !selectedItems.contains(guidelineName)) //
                .toArray(String[]::new);

        ListDialog dlg = new ListDialog(getShell());
        dlg.setAddCancelButton(true);
        dlg.setContentProvider(new ArrayContentProvider());
        dlg.setLabelProvider(new LabelProvider());
        dlg.setInput(availableGuidelines);
        dlg.setTitle(TranslationHelper.get("preferences.guidelineDialogTitle"));

        if (dlg.open() == IDialogConstants.OK_ID && dlg.getResult() != null && dlg.getResult().length > 0) {
            return (String) dlg.getResult()[0];
        }

        return null;
    }

    @Override
    protected String[] parseString(String preferenceList) {
        List<IGuideline> guidelines = guidelineRegistry.getGuidelines();

        String[] guidelineIds = preferenceList.split(IGuidelinePreferences.GUIDELINE_PREFERENCE_ID_SEPARATOR);
        String[] guidelineNames = Arrays.stream(guidelineIds) //
                .map(id -> guidelines.stream() //
                        .filter(guideline -> guideline.getId().equals(id)) //
                        .findFirst()) //
                .filter(Optional::isPresent) //
                .map(Optional::get) //
                .map(IGuideline::getName) //
                .toArray(String[]::new);
        return guidelineNames;
    }

    @Override
    protected String createList(String[] guidelineNames) {
        List<IGuideline> guidelines = guidelineRegistry.getGuidelines();

        String preferenceList = Arrays.stream(guidelineNames) //
                .map(name -> guidelines.stream() //
                        .filter(guideline -> guideline.getName().equals(name)) //
                        .findFirst()) //
                .filter(Optional::isPresent) //
                .map(Optional::get) //
                .map(IGuideline::getId) //
                .collect(Collectors.joining(IGuidelinePreferences.GUIDELINE_PREFERENCE_ID_SEPARATOR));
        return preferenceList;
    }
}
