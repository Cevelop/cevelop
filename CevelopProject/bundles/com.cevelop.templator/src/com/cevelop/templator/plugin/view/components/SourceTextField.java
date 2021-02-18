package com.cevelop.templator.plugin.view.components;

import org.eclipse.cdt.internal.ui.editor.CSourceViewer;
import org.eclipse.cdt.internal.ui.preferences.CSourcePreviewerUpdater;
import org.eclipse.cdt.internal.ui.text.CTextTools;
import org.eclipse.cdt.internal.ui.text.SimpleCSourceViewerConfiguration;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.PreferenceConstants;
import org.eclipse.cdt.ui.text.ICPartitions;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


@SuppressWarnings("restriction")
public class SourceTextField {

    private IDocument     document;
    private CSourceViewer viewer;

    public SourceTextField(Composite parent) {
        document = new Document();

        CTextTools tools = CUIPlugin.getDefault().getTextTools();
        tools.setupCDocumentPartitioner(document, ICPartitions.C_PARTITIONING, null);
        IPreferenceStore store = CUIPlugin.getDefault().getCombinedPreferenceStore();
        viewer = new CSourceViewer(parent, null, null, false, SWT.NONE, store);
        SimpleCSourceViewerConfiguration configuration = new SimpleCSourceViewerConfiguration(tools.getColorManager(), store, null,
                ICPartitions.C_PARTITIONING, false);
        viewer.configure(configuration);
        viewer.setEditable(false);
        viewer.setDocument(document);

        Font font = JFaceResources.getFont(PreferenceConstants.EDITOR_TEXT_FONT);
        viewer.getTextWidget().setFont(font);
        new CSourcePreviewerUpdater(viewer, configuration, store);

        Control control = viewer.getControl();
        GridData data = new GridData(GridData.FILL_BOTH);
        control.setLayoutData(data);
    }

    public int getTextHeight() {
        int lineCount = viewer.getTextWidget().getLineCount();
        int lineHeight = viewer.getTextWidget().getLineHeight();
        int borderWidth = viewer.getTextWidget().getBorderWidth() * 2;
        return lineHeight * lineCount + borderWidth;
    }

    public StyledText getTextWidget() {
        return viewer.getTextWidget();
    }

    public void dispose() {
        viewer.getTextWidget().dispose();
    }

    public void setText(String text) {
        document.set(text);
    }

    public CSourceViewer getViewer() {
        return viewer;
    }

    public void packSourcePart(boolean changed) {
        StyledText textWidget = viewer.getTextWidget();
        if (textWidget != null) {
            textWidget.pack(changed);
        }
    }
}
