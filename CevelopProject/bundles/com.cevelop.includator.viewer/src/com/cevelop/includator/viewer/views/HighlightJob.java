package com.cevelop.includator.viewer.views;

import java.util.Collection;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNodeSelector;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndexInclude;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.dependency.FullIncludePath;
import com.cevelop.includator.helpers.DeclarationReferenceHelper;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorStatus;
import com.cevelop.includator.helpers.IncludeHelper;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.resources.IncludatorProject;
import com.cevelop.includator.startingpoints.ActiveEditorStartingPoint;
import com.cevelop.includator.startingpoints.AlgorithmStartingPoint;
import com.cevelop.includator.ui.actions.IncludatorJob;
import com.cevelop.includator.viewer.views.model.Connection;
import com.cevelop.includator.viewer.views.model.ModelGraph;
import com.cevelop.includator.viewer.views.model.Node;


public class HighlightJob extends IncludatorJob {

    protected final ModelGraph<String> model;
    protected final IncludeView        modelView;
    protected final ISelection         selection;
    protected final ITextEditor        editor;
    private static final String        HIGHLIGHT_JOB_NAME = "Include View: Highlight Job";

    public HighlightJob(String name, IWorkbenchWindow window, IncludeView modelView, ITextEditor editor, ISelection selection) {
        super(name, window);
        this.modelView = modelView;
        this.model = modelView.getModel();
        this.editor = editor;
        this.selection = selection;
    }

    public HighlightJob(IWorkbenchWindow window, IncludeView modelView, ISelection selection, ITextEditor editor) {
        this(HIGHLIGHT_JOB_NAME, window, modelView, editor, selection);
    }

    @Override
    public IStatus runWithWorkbenchWindow(IProgressMonitor monitor) {

        synchronized (model) {
            MultiStatus status = new MultiStatus(IncludatorPlugin.PLUGIN_ID, -1, HIGHLIGHT_JOB_NAME, null);
            try {
                if (window.getActivePage().getActiveEditor() != editor) {
                    IStatus cancelStatus = new IncludatorStatus(IStatus.CANCEL, "Include View: Update job cancelled.");
                    return cancelStatus;
                }

                monitor.beginTask(HIGHLIGHT_JOB_NAME, 2);

                IWorkbenchWindow window = editor.getSite().getWorkbenchWindow();
                AlgorithmStartingPoint startingPoint = new ActiveEditorStartingPoint(window);
                IncludatorPlugin.initPreferredLinkageID(FileHelper.getPreferredLinkageID(startingPoint.getProject().getCProject()));
                IncludatorProject project = startingPoint.getProject();
                try {
                    project.acquireIndexReadLock();
                    calculateDependencies(monitor, startingPoint);
                } finally {
                    project.releaseIndexReadLock();
                    IncludatorPlugin.getDefault().cleanWorkspace();
                }

                highlightSelectedDependencies(monitor);

                monitor.done();
            } catch (Exception e) {}
            IncludatorPlugin.collectStatus(status);
            return status;
        }
    }

    protected void highlightSelectedDependencies(final IProgressMonitor monitor) {
        monitor.subTask("Include View: Drawing Include Dependencies");
        model.highlightItems();
        monitor.worked(1);
    }

    protected void calculateDependencies(final IProgressMonitor monitor, final AlgorithmStartingPoint startingPoint) {
        monitor.subTask("Include View: Building Reference Dependencies");
        if (selection instanceof TextSelection) {
            createHighlighting((TextSelection) selection, startingPoint);
        }
        monitor.worked(1);
    }

    protected void createHighlighting(final TextSelection selection, final AlgorithmStartingPoint startingPoint) {
        final TextSelection textSelection = selection;
        final IncludatorFile file = startingPoint.getFile();

        IASTTranslationUnit tu = file.getTranslationUnit();
        if (tu == null) {
            return;
        }
        IASTNodeSelector nodeSelector = tu.getNodeSelector(null);
        IASTName selectedName = nodeSelector.findEnclosingName(textSelection.getOffset(), textSelection.getLength());

        if (selectedName != null) {
            highlightDependencies(selectedName, file);
        }

    }

    private void highlightDependencies(final IASTName selectedName, final IncludatorFile file) {
        Collection<DeclarationReference> references = DeclarationReferenceHelper.findDeclReferences(selectedName, file);
        for (DeclarationReference declarationReference : references) {
            highlighDependency(declarationReference, file);
        }
    }

    private void highlighDependency(DeclarationReference declarationReference, final IncludatorFile file) {
        for (DeclarationReferenceDependency dependency : DeclarationReferenceHelper.getDependencies(declarationReference)) {
            List<FullIncludePath> includePaths = dependency.getIncludePaths();
            for (FullIncludePath fullIncludePath : includePaths) {
                Collection<IIndexInclude> includes = fullIncludePath.getAllIncludes();
                highlightPath(includes, file);
            }
        }
    }

    private void highlightPath(final Collection<IIndexInclude> includes, final IncludatorFile file) {
        Node<String> ancestorIncludeNode = model.getNode(file.getFilePath());

        for (IIndexInclude iIndexInclude : includes) {
            try {
                IncludatorFile includedFile = IncludeHelper.findIncludedFile(iIndexInclude, file.getProject());
                Node<String> includedNode = model.getNode(includedFile.getFilePath());
                model.highlight(includedNode);
                if (ancestorIncludeNode != null) {
                    markConnectionFromTo(ancestorIncludeNode, includedNode);
                }

                ancestorIncludeNode = includedNode;

            } catch (CoreException e) {
                IncludatorPlugin.log("Problem while highlighting nodes in Include View.", e);
            }
        }
    }

    private void markConnectionFromTo(final Node<String> ancestorIncludeNode, final Node<String> includedNode) {

        List<Connection<String>> connections = ancestorIncludeNode.getConnections();
        for (Connection<String> connection : connections) {
            if (connection.getEnd() == includedNode) {
                model.highlight(connection);
            }
        }
    }

}
