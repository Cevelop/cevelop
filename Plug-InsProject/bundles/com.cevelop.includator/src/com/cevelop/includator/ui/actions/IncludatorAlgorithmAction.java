package com.cevelop.includator.ui.actions;

import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.action.IAction;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.AlgorithmScopeHelper;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.helpers.IncludatorStatus;
import com.cevelop.includator.helpers.ProgressMonitorHelper;
import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.SuggestionOperationMapProvider;
import com.cevelop.includator.preferences.IncludatorPropertyManager;
import com.cevelop.includator.resources.IncludatorProject;
import com.cevelop.includator.startingpoints.AlgorithmStartingPoint;
import com.cevelop.includator.startingpoints.AlgorithmStartingPointFactory;
import com.cevelop.includator.ui.OptimizationRunner;
import com.cevelop.includator.ui.SuggestionDialogRunnable;


public abstract class IncludatorAlgorithmAction extends IncludatorAction {

    protected abstract Algorithm getAlgorithmToRun();

    /**
     * Subclasses can override this to have the user asked if index should be adapted before algorithm is run (only if index is not adapted yet.
     *
     * @return {@code false} as a default, override in subclass
     */
    protected boolean shouldSuggestToAdaptIndexBeforeRun() {
        return false;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        selection = CUIPlugin.getActivePage().getSelection();
        window = CUIPlugin.getActiveWorkbenchWindow();
        performAnalysis(getAlgorithmToRun());
        return null;
    }

    @Override
    public void run(IAction action) {
        performAnalysis(getAlgorithmToRun());
    }

    private void performAnalysis(final Algorithm algorithm) {
        includatorAnalysisJob = new IncludatorJob("Includator Static Include Analysis Job", window) {

            @Override
            public IStatus runWithWorkbenchWindow(IProgressMonitor monitor) {
                adaptIndexIfRequired(monitor);
                return runAnalysisJob(monitor, algorithm);
            }
        };
        includatorAnalysisJob.schedule();
    }

    protected void adaptIndexIfRequired(IProgressMonitor monitor) {
        if (shouldSuggestToAdaptIndexBeforeRun()) {
            monitor.setTaskName("Waiting for index adaption to complete...");
            AdaptIndexAction adaptIndexAction = new AdaptIndexAction();
            adaptIndexAction.init(window);
            adaptIndexAction.selectionChanged(null, selection);
            adaptIndexAction.setActivePart(null, activePart);
            adaptIndexAction.setShouldAsk();
            adaptIndexAction.setIsSubtask();
            adaptIndexAction.run(null);
            try {
                adaptIndexAction.getIncludatorAnalysationJob().join();
            } catch (InterruptedException e) {
                AlgorithmStartingPoint startingPoint = AlgorithmStartingPointFactory.getStartingPoint(selection, window);
                String mgs = "Incluator job was interrupted while adapting index.";
                IncludatorPlugin.logStatus(new IncludatorStatus(mgs, e), startingPoint.getProject().getPath().toOSString());
            }
        }
    }

    public IStatus runAnalysisJob(IProgressMonitor monitor, Algorithm algorithm) {
        MultiStatus status = new MultiStatus(IncludatorPlugin.PLUGIN_ID, IncludatorStatus.STATUS_CODE_GROUPS_BASE, INCLUDATOR_STATUS_NAME, null);
        try {
            int algWork = ProgressMonitorHelper.ALG_WORK;
            int work = ProgressMonitorHelper.PRE_WORK + algWork + ProgressMonitorHelper.POST_WORK;
            monitor.beginTask("Includator Static Include Analysis", work);

            AlgorithmStartingPoint startingPoint = AlgorithmStartingPointFactory.getStartingPoint(selection, window);
            IncludatorPlugin.initPreferredLinkageID(FileHelper.getPreferredLinkageID(startingPoint.getProject().getCProject()));
            Algorithm scopedAlg = AlgorithmScopeHelper.getScopedAlgorithm(startingPoint, algorithm);
            if (scopedAlg == null) {
                String path = FileHelper.pathToStringPath(startingPoint.getProject().getPath());
                String algScopeName = algorithm.getScope().name();
                String startingScopeName = startingPoint.getScope().name();
                String msg = "You are trying to run an algorithm with " + algScopeName + " and have selected a resource with " + startingScopeName +
                             ".";
                IncludatorPlugin.logStatus(new IncludatorStatus(msg), path);
            }
            algorithm = scopedAlg;
            if (!startingPointValidForAlgorithms(startingPoint, algorithm)) {
                return status;
            }
            OptimizationRunner runner = new OptimizationRunner(startingPoint, SubMonitor.convert(monitor, algWork), algorithm,
                    getSuggestionOperationMapProvider());
            if (shouldShowDialog(startingPoint.getProject())) {
                runner.setShouldPerformCustomMapOperation(true);
            }
            monitor.worked(ProgressMonitorHelper.PRE_WORK);
            boolean wasAlgRun = runner.run();
            if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }
            monitor.worked(ProgressMonitorHelper.POST_WORK);
            if (status.isOK() && wasAlgRun) {
                runner.performUserAction();
            }
        } catch (IncludatorException e) {
            IncludatorPlugin.logStatus(new IncludatorStatus(e), e.getAffectedPath());
        } catch (Exception e) {
            IncludatorPlugin.logStatus(new IncludatorStatus("Error while performing static analysis.", e), (String) null);
        } finally {
            IncludatorPlugin.collectStatus(status);
            monitor.setTaskName("Cleaning cached data");
            IncludatorPlugin.getDefault().cleanWorkspace(monitor);
            SubMonitor.done(monitor);
        }
        return status;
    }

    private boolean startingPointValidForAlgorithms(AlgorithmStartingPoint startingPoint, Algorithm algorithm) {
        if (startingPoint.getFile() != null) {
            return true;
        }
        if (AlgorithmScope.EDITOR_SCOPE.equals(algorithm.getScope())) {
            String path = FileHelper.pathToStringPath(startingPoint.getProject().getPath());
            String msg = "The algorithm your trying to run must be run on a C/C++ source or header file which is currently not the case.";
            IncludatorPlugin.logStatus(new IncludatorStatus(msg), path);
            return false;
        }
        return true;
    }

    protected boolean shouldShowDialog(IncludatorProject project) {
        return !IncludatorPropertyManager.getBooleanProperty(project, IncludatorPropertyManager.NEVER_SHOW_SUGGESTION_DIALOG);
    }

    protected SuggestionOperationMapProvider getSuggestionOperationMapProvider() {
        return new SuggestionDialogRunnable(window);
    }
}
