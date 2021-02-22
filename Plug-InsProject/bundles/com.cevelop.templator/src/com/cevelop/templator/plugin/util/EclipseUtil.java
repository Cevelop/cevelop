package com.cevelop.templator.plugin.util;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexManager;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.ui.CDTUITools;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

import com.cevelop.templator.plugin.asttools.ASTTools;
import com.cevelop.templator.plugin.asttools.IIndexAction;
import com.cevelop.templator.plugin.asttools.IndexAction;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.plugin.logger.TemplatorLogger;


public final class EclipseUtil {

    private EclipseUtil() {}

    /**
     * Gets the active editor, the caller does not need to run this method in the UI thread. This will be handled by the
     * method itself.
     *
     * @return the active editor
     */
    public static IEditorPart getActiveEditor() {
        IEditorPart activeEditor = null;
        Thread uiThread = Display.getDefault().getThread();
        Thread currentThread = Thread.currentThread();

        // call getActiveEditor directly if we are already in the UI thread.
        // Running the UI job would result in a deadlock/endless loop or
        // something like that
        try {
            if (uiThread == currentThread) {
                activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
            } else {
                GetActiveEditorUIJob getEditorJob = new GetActiveEditorUIJob("Get active editor");
                getEditorJob.schedule();
                getEditorJob.join();
                activeEditor = getEditorJob.getActiveEditor();
            }
        } catch (Exception e) {
            TemplatorLogger.logError("Failed to get the active editor, maybe none is open", e);
        }

        return activeEditor;
    }

    public static IRegion getCursorPosition(IEditorPart editorPart) {
        ITextSelection textSelection = (ITextSelection) editorPart.getSite().getSelectionProvider().getSelection();
        return new Region(textSelection.getOffset(), textSelection.getLength());
    }

    public static IASTName getNameUnderCursor(IASTTranslationUnit ast) {
        IEditorPart editorPart = getActiveEditor();
        IRegion region = getCursorPosition(editorPart);

        return ASTTools.getNameAtRegion(region, ast);
    }

    public static ICProject getCProjectForCurrentFile() {
        IProject project = getActiveFile().getProject();
        return CoreModel.getDefault().create(project);
    }

    public static IFile getActiveFile() {
        IEditorInput editorInput = getActiveEditor().getEditorInput();
        if (editorInput instanceof IFileEditorInput) {
            IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
            return fileEditorInput.getFile();
        }
        return null;
    }

    public static void navigateToNode(IASTNode node) {
        IASTFileLocation fileloc = node.getFileLocation();

        if (fileloc == null) {
            return;
        }
        final IPath path = new Path(fileloc.getFileName());
        final int offset = fileloc.getNodeOffset();
        final int length = fileloc.getNodeLength();

        NavigateToNameUIJob navigateJob = new NavigateToNameUIJob("Navigating to node " + node.getRawSignature(), path, offset, length);
        navigateJob.runInUIThread(new NullProgressMonitor());
    }

    public static IASTTranslationUnit getASTFromIndex(IIndex index) throws TemplatorException {
        IEditorPart activeEditor = getActiveEditor();
        ITranslationUnit tu = (ITranslationUnit) CDTUITools.getEditorInputCElement(activeEditor.getEditorInput());
        return EclipseUtil.getASTFromIndex(tu, index);
    }

    public static IASTTranslationUnit getASTFromIndex(final ITranslationUnit tu, IIndex index) throws TemplatorException {
        IASTTranslationUnit ast = null;

        ast = IndexAction.<IASTTranslationUnit>perform(index, new IIndexAction() {

            @SuppressWarnings("unchecked")
            @Override
            public IASTTranslationUnit doAction(IIndex index) throws CoreException {
                return tu.getAST(index, 0);
            }
        });

        return ast;
    }

    public static IIndex getIndexFromCurrentCProject() {
        IIndex index = null;
        ICProject project = getCProjectForCurrentFile();
        if (project != null) {
            index = getIndexFromProject(project);
        }
        return index;
    }

    public static IIndex getIndexFromProject(ICProject project) {
        IIndex index = null;
        try {
            index = CCorePlugin.getIndexManager().getIndex(project, IIndexManager.ADD_DEPENDENCIES | IIndexManager.ADD_DEPENDENT |
                                                                    IIndexManager.ADD_EXTENSION_FRAGMENTS_NAVIGATION);
        } catch (CoreException e) {
            TemplatorLogger.logError("could not get index for CProject named " + project.getElementName(), e);
        }
        return index;
    }

    public static int getTextFontSize() {
        return JFaceResources.getTextFont().getFontData()[0].getHeight();
    }
}
