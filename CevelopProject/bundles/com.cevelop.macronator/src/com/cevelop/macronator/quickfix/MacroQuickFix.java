package com.cevelop.macronator.quickfix;

import java.net.URI;

import org.eclipse.cdt.codan.ui.AbstractAstRewriteQuickFix;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.cevelop.macronator.MacronatorPlugin;
import com.cevelop.macronator.transform.MacroTransformation;


public abstract class MacroQuickFix extends AbstractAstRewriteQuickFix {

    private IMarker   marker;
    private IDocument document;

    @Override
    public void modifyAST(final IIndex index, final IMarker marker) {
        try {
            this.marker = marker;
            final IASTTranslationUnit ast = getTranslationUnitViaEditor(marker).getAST(index, ITranslationUnit.AST_SKIP_INDEXED_HEADERS |
                                                                                              ITranslationUnit.AST_PARSE_INACTIVE_CODE);
            final int macroLength = document.getLineLength(getMarkerLineNumber());
            final IASTNode macroNode = ast.getNodeSelector(null).findFirstContainedNode(getMarkerOffset(), macroLength);
            if (isMultilineMacro(macroNode)) {
                apply((IASTPreprocessorMacroDefinition) macroNode.getParent());
            } else {
                apply((IASTPreprocessorMacroDefinition) macroNode);
            }
        } catch (final Exception e) {
            MacronatorPlugin.log(e, "Quickfix could not be applied: no macro definition found.");
        }
    }

    public abstract void apply(IASTPreprocessorMacroDefinition macroDefinition);

    @Override
    public final void apply(final IMarker marker, final IDocument document) {
        this.document = document;
        super.apply(marker, document);
    }

    private boolean isMultilineMacro(final IASTNode macroNode) {
        return (macroNode.getParent() != null && macroNode.getParent() instanceof IASTPreprocessorMacroDefinition);
    }

    protected void applyTransformation(final IASTPreprocessorMacroDefinition macro, final MacroTransformation transformation) {
        try {
            final String replacementText = transformation.getCode();
            final int macroBeginningOffset = getMarkerOffset();
            final int length = getMacroTotalLength(macroBeginningOffset, macro);
            final TextFileChange change = new TextFileChange("Apply acro quickfix", (IFile) marker.getResource());
            change.setEdit(new ReplaceEdit(macroBeginningOffset, length, replacementText));
            change.perform(new NullProgressMonitor());
        } catch (final Exception e) {
            MacronatorPlugin.log(e, "Quickfix could not be applied");
        }
    }

    private int getMacroTotalLength(final int macroStartOffset, final IASTPreprocessorMacroDefinition macroDefinition) throws BadLocationException {
        final int macroEndOffset = macroStartOffset + macroDefinition.getRawSignature().length();
        final int macroStartLineNumber = document.getLineOfOffset(macroStartOffset);
        final int macroEndLine = document.getLineOfOffset(macroEndOffset);
        final int diff = macroStartOffset - document.getLineOffset(macroStartLineNumber);
        int macroLength = -diff;
        for (int i = macroStartLineNumber; i < macroEndLine; i++) {
            macroLength += document.getLineLength(i);
        }
        macroLength += document.getLineLength(macroEndLine);
        return macroLength;
    }

    private Integer getMarkerLineNumber() throws PartInitException, BadLocationException {
        final ITextEditor editor = getTextEditor();
        final IDocumentProvider documentProvider = editor.getDocumentProvider();
        final IDocument document = documentProvider.getDocument(editor.getEditorInput());
        final Integer actualMarkerOffset = getMarkerOffset();
        return document.getLineOfOffset(actualMarkerOffset);
    }

    private Integer getMarkerOffset() throws PartInitException {
        final ITextEditor editor = getTextEditor();
        final IDocumentProvider documentProvider = editor.getDocumentProvider();
        final IAnnotationModel model = documentProvider.getAnnotationModel(editor.getEditorInput());
        final AbstractMarkerAnnotationModel markerModel = (AbstractMarkerAnnotationModel) model;
        final Position position = markerModel.getMarkerPosition(marker);
        return position.getOffset();
    }

    private ITextEditor getTextEditor() throws PartInitException {
        final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        return getEditor(marker.getResource().getLocationURI(), activeWorkbenchWindow);
    }

    private ITextEditor getEditor(final URI fileUri, final IWorkbenchWindow window) throws PartInitException {
        final IWorkbenchPage activePage = window.getActivePage();
        final IEditorReference[] editors = activePage.getEditorReferences();
        for (final IEditorReference reference : editors) {
            final IEditorInput editorInput = reference.getEditorInput();
            if (editorInput instanceof IFileEditorInput) {
                if (fileUri.equals(((IFileEditorInput) editorInput).getFile().getLocationURI())) {
                    final IEditorPart editor = reference.getEditor(false);
                    if ((editor != null) && (editor instanceof ITextEditor)) {
                        return (ITextEditor) editor;
                    }
                }
            }
        }
        throw new RuntimeException("Could not obtain TextEditor");
    }
}
