/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.ui;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.SuggestionOperationMapProvider;
import com.cevelop.includator.ui.components.SuggestionDialog;
import com.cevelop.includator.ui.solutionoperations.SuggestionSolutionOperation;


public class SuggestionDialogRunnable implements Runnable, SuggestionOperationMapProvider {

    private Map<SuggestionSolutionOperation, List<Suggestion<?>>> suggestionOperationMap;
    private final SuggestionDialog                                dlg;

    public SuggestionDialogRunnable(IWorkbenchWindow window) {
        dlg = new SuggestionDialog(window.getShell());
    }

    public SuggestionDialogRunnable(IWorkbenchWindow window, MultiStatus status, String msg) {
        dlg = new SuggestionDialog(window.getShell(), status, msg);
    }

    private void initMap() {
        suggestionOperationMap = SuggestionSolutionOperation.makeEmptyMap();
    }

    @Override
    public void run() {
        dlg.create();
        dlg.open();
        if (dlg.getReturnCode() != Window.CANCEL) {
            makeMap();
        } else {
            initMap();
        }
    }

    private void makeMap() {
        if (suggestionOperationMap == null) {
            initMap();
            dlg.getResults(suggestionOperationMap);
        }
    }

    @Override
    public void performCustomOperation() {
        PlatformUI.getWorkbench().getDisplay().syncExec(this);
    }

    @Override
    public Map<SuggestionSolutionOperation, List<Suggestion<?>>> getSuggestionOperationMap() {
        makeMap();
        return suggestionOperationMap;
    }

    @Override
    public void setSuggestions(List<Suggestion<?>> suggestions, SuggestionSolutionOperation defaultOperation) {
        dlg.setSuggestions(suggestions, defaultOperation);
    }
}
