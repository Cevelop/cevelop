package com.cevelop.includator.viewer.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import com.cevelop.includator.ui.actions.IncludatorJob;
import com.cevelop.includator.viewer.views.model.ModelGraph;


public class IncludeView extends ViewPart {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "com.cevelop.includator.viewer.views.IncludeView";

    private ModelGraph<String>        model;
    private ViewUpdater               changeNotifier;
    private IPartService              partService;
    private Graph                     graph;
    private final List<IncludatorJob> pendingJobs = Collections.synchronizedList(new ArrayList<IncludatorJob>());

    public ModelGraph<String> getModel() {
        return model;
    }

    @Override
    public void dispose() {
        partService.removePartListener(changeNotifier);
        super.dispose();
    }

    @Override
    public void createPartControl(Composite parent) {
        graph = new Graph(parent, SWT.NONE);
        graph.setNodeStyle(ZestStyles.NODES_NO_LAYOUT_ANIMATION);
        final TreeLayoutAlgorithm layout = new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
        graph.setLayoutAlgorithm(layout, false);
        model = new ModelGraph<>(graph);
    }

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        IWorkbenchWindow workbenchWindow = site.getWorkbenchWindow();

        changeNotifier = new ViewUpdater(this);
        partService = workbenchWindow.getPartService();
        partService.addPartListener(changeNotifier);
    }

    public void drawIncludes(final ITextEditor editor) {
        IWorkbenchWindow window = editor.getSite().getWorkbenchWindow();
        ISelection selection = editor.getSelectionProvider().getSelection();
        ViewUpdateJob updateViewJob = new ViewUpdateJob(this, window, selection, editor);
        pendingJobs.add(updateViewJob);
        updateViewJob.schedule();
    }

    @Override
    public void setFocus() {
        graph.setFocus();
    }

    public void updateHighlighting(ITextEditor editor) {
        IWorkbenchWindow window = editor.getSite().getWorkbenchWindow();
        ISelectionProvider selectionProvider = editor.getSelectionProvider();
        if (selectionProvider == null) {
            return;
        }
        final ISelection selection = selectionProvider.getSelection();
        HighlightJob highlightJob = new HighlightJob(window, this, selection, editor);
        highlightJob.schedule();

    }

    boolean continueUpdate(HighlightJob job) {
        while (pendingJobs.size() > 1) {
            pendingJobs.remove(0);
        }
        return pendingJobs.get(0) == job;
    }
}
