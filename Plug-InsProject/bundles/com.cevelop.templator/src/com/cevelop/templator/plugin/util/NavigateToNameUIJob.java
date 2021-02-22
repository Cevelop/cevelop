package com.cevelop.templator.plugin.util;

import org.eclipse.cdt.internal.ui.util.EditorUtility;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.ITextEditor;

import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.plugin.logger.TemplatorLogger;


@SuppressWarnings("restriction")
public class NavigateToNameUIJob extends UIJob {

    private final IPath path;
    private final int   offset;
    private final int   length;

    public NavigateToNameUIJob(String name, IPath path, int offset, int length) {
        super(name);
        this.path = path;
        this.offset = offset;
        this.length = length;
    }

    @Override
    public IStatus runInUIThread(IProgressMonitor monitor) {
        try {
            open();
        } catch (CoreException e) {
            CUIPlugin.log(e);
        }
        return null;
    }

    protected void open() throws CoreException {
        IEditorPart editor = EditorUtility.openInEditor(path, null);
        ITextEditor textEditor = EditorUtility.getTextEditor(editor);
        if (textEditor != null) {
            textEditor.selectAndReveal(offset, length);
        } else {
            TemplatorLogger.logError(new TemplatorException("Could not open Declaration" + path));
        }
    }
}
