/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.ui.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.ui.solutionoperations.SuggestionSolutionOperation;


public class SuggestionDialog extends TitleAreaDialog {

    private static final int DLG_HEIGHT = 500;
    private static final int DLG_WIDTH  = 800;

    private MultiStatus                     status;
    private int                             problemCount;
    private final SuggestionContentProvider contentProvider;
    private int                             suggestionCount;
    private String                          customMsg;

    public SuggestionDialog(Shell parentShell) {
        super(parentShell);
        setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
        contentProvider = new SuggestionContentProvider();
    }

    public SuggestionDialog(Shell parentShell, MultiStatus status, String msg) {
        this(parentShell);
        this.status = status;
        customMsg = msg;
    }

    private void initStatus() {
        if (status == null) {
            status = new MultiStatus(IncludatorPlugin.PLUGIN_ID, -1, "Includator Algorithm Execution", null);
            IncludatorPlugin.collectStatus(status);
        }
        problemCount = getProblemCount();
    }

    @Override
    public void create() {
        initStatus();
        super.create();
        getShell().setText("Includator Suggestions");
        setTitle("Includator Static Include Analysis");

        if (customMsg != null) {
            setMessage(customMsg, IMessageProvider.WARNING);
        } else if (problemCount == 0) {
            setMessage("Includator has " + suggestionCount + " suggestions.");
        } else {
            String msg = "Includator encountered " + problemCount + " problems during analysis and has " + suggestionCount + " suggestions.";
            setMessage(msg, IMessageProvider.WARNING);
        }
        getButton(IDialogConstants.OK_ID).setText(suggestionCount != 0 ? "Apply" : "Close");
        if (suggestionCount == 0) {
            getButton(IDialogConstants.CANCEL_ID).setEnabled(false);
        }
    }

    private int getProblemCount() {
        int result = 0;
        for (IStatus curSeverityGroup : status.getChildren()) {
            for (IStatus curFileGroup : curSeverityGroup.getChildren()) {
                result += curFileGroup.getChildren().length;
            }
        }
        return result;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite gridLayoutArea = (Composite) super.createDialogArea(parent);

        Composite area = new Composite(gridLayoutArea, SWT.BORDER);
        area.setLayoutData(new GridData(GridData.FILL_BOTH));
        area.setLayout(new FillLayout());

        SashForm sashForm = null;
        if (hasContent()) {
            sashForm = new SashForm(area, SWT.SMOOTH | SWT.VERTICAL);
            sashForm.setSashWidth(6);
            area = sashForm;
        }
        SuggestionTree treeViewer = null;
        if (suggestionCount != 0) {
            treeViewer = new SuggestionTree(area, contentProvider, new SuggestionLabelProvider(area));
        }
        if (problemCount != 0) {
            createProblemsTree(area);
        }
        adjustSashWeights(sashForm);
        adjustSuggestionTreeColumnWidths(treeViewer);
        return area;
    }

    private void adjustSashWeights(SashForm sashForm) {
        if (hasContent()) {
            int totalWeight = 100;
            int minWeight = 25;
            int maxWeight = totalWeight - minWeight;
            float suggestionPartRatio = totalWeight * (float) suggestionCount / (suggestionCount + problemCount);
            int weightTop = Math.min(maxWeight, Math.max(minWeight, (int) (suggestionPartRatio)));
            int weightBottom = Math.min(maxWeight, Math.max(minWeight, totalWeight - weightTop));
            sashForm.setWeights(new int[] { weightTop, weightBottom });
        }
    }

    private void adjustSuggestionTreeColumnWidths(SuggestionTree treeViewer) {
        if (suggestionCount != 0) {
            treeViewer.packColumns();
            treeViewer.shrinkToSeeAllColumns(DLG_WIDTH);
        }
    }

    private boolean hasContent() {
        return problemCount != 0 && suggestionCount != 0;
    }

    private void createProblemsTree(Composite parent) {
        Group grpProblems = new Group(parent, SWT.NONE);
        grpProblems.setText("Problems:");
        grpProblems.setLayout(new FillLayout());
        new IncludatorProblemTree(grpProblems, status);
    }

    public void getResults(Map<SuggestionSolutionOperation, List<Suggestion<?>>> mapToAddTo) {
        collectResults(contentProvider.getTopElement(), mapToAddTo);
    }

    private void collectResults(SuggestionTreeElement element, Map<SuggestionSolutionOperation, List<Suggestion<?>>> resultMap) {
        if (element.getSuggestion() != null) {
            SuggestionSolutionOperation selectedOperation = element.getSelectedOperation();
            if (selectedOperation != null) {
                addToResult(element.getSuggestion(), selectedOperation, resultMap);
            }
        }
        for (SuggestionTreeElement curChild : element.getChildren()) {
            collectResults(curChild, resultMap);
        }
    }

    private void addToResult(Suggestion<?> suggestion, SuggestionSolutionOperation operation,
            Map<SuggestionSolutionOperation, List<Suggestion<?>>> resultMap) {
        if (!resultMap.containsKey(operation)) {
            resultMap.put(operation, new ArrayList<Suggestion<?>>());
        }
        resultMap.get(operation).add(suggestion);
    }

    public void setSuggestions(List<Suggestion<?>> suggestions, SuggestionSolutionOperation defaultOperation) {
        contentProvider.setSuggestion(suggestions, defaultOperation);
        suggestionCount = suggestions.size();
    }

    @Override
    protected Point getInitialSize() {
        return new Point(DLG_WIDTH, DLG_HEIGHT);
    }
}
