package com.cevelop.includator.viewer.views;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.helpers.IncludatorStatus;
import com.cevelop.includator.helpers.IncludeHelper;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.resources.IncludatorProject;
import com.cevelop.includator.startingpoints.ActiveEditorStartingPoint;
import com.cevelop.includator.startingpoints.AlgorithmStartingPoint;
import com.cevelop.includator.viewer.views.model.Node;


final class ViewUpdateJob extends HighlightJob {

    private static final String VIEW_UPDATE_JOB_NAME = "Include View: Include Update Job";

    ViewUpdateJob(IncludeView modelView, IWorkbenchWindow window, ISelection selection, ITextEditor editor) {
        super(VIEW_UPDATE_JOB_NAME, window, modelView, editor, selection);
        setUser(false);
        setSystem(true);
    }

    @Override
    public IStatus runWithWorkbenchWindow(final IProgressMonitor monitor) {

        synchronized (model) {
            if (!modelView.continueUpdate(this)) {
                IStatus cancelStatus = new IncludatorStatus(IStatus.CANCEL, "Include View: Update job cancelled.");
                return cancelStatus;
            }

            monitor.beginTask(VIEW_UPDATE_JOB_NAME, 5);

            resetModel(monitor);

            try {
                IWorkbenchWindow window = editor.getSite().getWorkbenchWindow();
                AlgorithmStartingPoint startingPoint = new ActiveEditorStartingPoint(window);
                IncludatorPlugin.initPreferredLinkageID(FileHelper.getPreferredLinkageID(startingPoint.getProject().getCProject()));
                IncludatorProject project = startingPoint.getProject();
                try {
                    project.acquireIndexReadLock();
                    buildModel(monitor, startingPoint);
                    drawModel(monitor);

                    calculateDependencies(monitor, startingPoint);
                } finally {
                    project.releaseIndexReadLock();
                    IncludatorPlugin.getDefault().cleanWorkspace();
                }
            } catch (IncludatorException e) {
                monitor.done();
                return collectState();
            } finally {

            }
            highlightSelectedDependencies(monitor);
            monitor.done();
            return collectState();
        }
    }

    private MultiStatus collectState() {
        MultiStatus status = new MultiStatus(IncludatorPlugin.PLUGIN_ID, -1, VIEW_UPDATE_JOB_NAME, null);
        IncludatorPlugin.collectStatus(status);
        return status;
    }

    @Override
    protected void highlightSelectedDependencies(final IProgressMonitor monitor) {
        if (modelView.continueUpdate(this)) {
            super.highlightSelectedDependencies(monitor);
        }
    }

    @Override
    protected void calculateDependencies(final IProgressMonitor monitor, final AlgorithmStartingPoint startingPoint) {
        if (modelView.continueUpdate(this)) {
            super.calculateDependencies(monitor, startingPoint);
        }
    }

    private void drawModel(final IProgressMonitor monitor) {
        monitor.subTask("Include View: Drawing Include Dependencies");
        if (modelView.continueUpdate(this)) model.draw();
        monitor.worked(1);
    }

    private void buildModel(final IProgressMonitor monitor, final AlgorithmStartingPoint startingPoint) {
        monitor.subTask("Include View: Building Include Dependencies");
        if (modelView.continueUpdate(this)) buildModel(startingPoint);
        monitor.worked(1);
    }

    private void resetModel(final IProgressMonitor monitor) {
        monitor.subTask("Include View: Clearing Include Dependencies");
        if (modelView.continueUpdate(this)) model.clear();
        monitor.worked(1);
    }

    protected void buildModel(final AlgorithmStartingPoint startingPoint) {
        if (startingPoint != null) {
            final IncludatorFile file = startingPoint.getFile();
            if (file != null) {
                updateGraph(file);
            }
        }
    }

    private void updateGraph(final IncludatorFile activeFile) {
        handleFile(activeFile);
    }

    private void handleFile(IncludatorFile includatorFile) {

        final Node<String> startNode = getNodeForFile(includatorFile);
        for (IASTPreprocessorIncludeStatement includeDirective : includatorFile.getIncludes()) {
            if (!modelView.continueUpdate(this)) return;
            IncludatorFile includeFile = IncludeHelper.findIncludedFile(includeDirective, includatorFile.getProject());
            String includeFilePath = includeFile.getFilePath();

            if (!model.nodeExists(includeFilePath)) {
                if (!includeDirective.isSystemInclude()) {
                    handleFile(includeFile);
                }
            }

            final Node<String> endNode = getNodeForFile(includeFile);
            model.createConnection(startNode, endNode);
        }
    }

    private Node<String> getNodeForFile(IncludatorFile includatorFile) {
        String path = includatorFile.getFilePath();
        Node<String> node = model.getNode(path);
        if (node == null) {
            final String label = includatorFile.getSmartPath();
            return model.createNode(label, path);
        }
        return node;
    }
}
