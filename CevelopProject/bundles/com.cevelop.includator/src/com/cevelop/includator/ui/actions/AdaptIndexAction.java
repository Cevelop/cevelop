package com.cevelop.includator.ui.actions;

import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.helpers.IncludatorStatus;
import com.cevelop.includator.helpers.index.IncludatorIndexAdapter;
import com.cevelop.includator.startingpoints.AlgorithmStartingPoint;
import com.cevelop.includator.startingpoints.AlgorithmStartingPointFactory;
import com.cevelop.includator.ui.SuggestionDialogRunnable;
import com.cevelop.includator.ui.helpers.BooleanResultContainer;


public class AdaptIndexAction extends IncludatorAction {

    private final IncludatorIndexAdapter indexAdapter;
    private boolean                      isSubtask = false;

    public AdaptIndexAction() {
        indexAdapter = new IncludatorIndexAdapter();
    }

    public void setShouldAsk() {
        indexAdapter.setConfirator(() -> {
            final BooleanResultContainer result = new BooleanResultContainer();
            PlatformUI.getWorkbench().getDisplay().syncExec(() -> {
                String msg =
                           "The CDT index has not been adapted for Includator. This means that Includator might not know all symbols contained in the current project. Do you want to fix that before continuing?";
                result.setResult(MessageDialog.openQuestion(window.getShell(), "Adapt CDT index first?", msg));
            });
            return result.getResult();
        });
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        selection = CUIPlugin.getActivePage().getSelection();
        window = CUIPlugin.getActiveWorkbenchWindow();
        includatorAnalysisJob = new IncludatorJob("Extending Symbol Awareness", window) {

            @Override
            public IStatus runWithWorkbenchWindow(IProgressMonitor monitor) {
                try {
                    AlgorithmStartingPoint startingPoint = AlgorithmStartingPointFactory.getStartingPoint(selection, window);
                    indexAdapter.run(monitor, startingPoint.getProject());
                    MultiStatus status = new MultiStatus(IncludatorPlugin.PLUGIN_ID, IncludatorStatus.STATUS_CODE_GROUPS_BASE, INCLUDATOR_STATUS_NAME,
                            null);
                    if (!isSubtask) {
                        IncludatorPlugin.collectStatus(status);
                    }
                    if (hasNonErrorStatusContent(status)) {
                        String msg = "Extending symbol awareness completed successful. Includator encountered problems.";
                        // we abuse the SuggestionDialog here to show only warnings. Need to do this because Eclipse will show status only if it
                        // contains errors.
                        new SuggestionDialogRunnable(window, status, msg).performCustomOperation();
                    }
                    return status;
                } catch (IncludatorException e) {
                    return new IncludatorStatus("Index adaption job failed.", e);
                }
            }
        };
        includatorAnalysisJob.schedule();
        return null;
    }

    @Override
    public void run(IAction action) {

        includatorAnalysisJob = new IncludatorJob("Extending Symbol Awareness", window) {

            @Override
            public IStatus runWithWorkbenchWindow(IProgressMonitor monitor) {
                try {
                    AlgorithmStartingPoint startingPoint = AlgorithmStartingPointFactory.getStartingPoint(selection, window);
                    indexAdapter.run(monitor, startingPoint.getProject());
                    MultiStatus status = new MultiStatus(IncludatorPlugin.PLUGIN_ID, IncludatorStatus.STATUS_CODE_GROUPS_BASE, INCLUDATOR_STATUS_NAME,
                            null);
                    if (!isSubtask) {
                        IncludatorPlugin.collectStatus(status);
                    }
                    if (hasNonErrorStatusContent(status)) {
                        String msg = "Extending symbol awareness completed successful. Includator encountered problems.";
                        // we abuse the SuggestionDialog here to show only warnings. Need to do this because Eclipse will show status only if it
                        // contains errors.
                        new SuggestionDialogRunnable(window, status, msg).performCustomOperation();
                    }
                    return status;
                } catch (IncludatorException e) {
                    return new IncludatorStatus("Index adaption job failed.", e);
                }
            }
        };
        includatorAnalysisJob.schedule();
    }

    private boolean hasNonErrorStatusContent(MultiStatus status) {
        int severity = status.getSeverity();
        return severity == IStatus.WARNING || severity == IStatus.INFO;
    }

    public void setIsSubtask() {
        isSubtask = true;
    }

}
