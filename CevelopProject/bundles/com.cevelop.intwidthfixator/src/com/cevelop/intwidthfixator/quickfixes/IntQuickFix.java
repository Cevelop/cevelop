package com.cevelop.intwidthfixator.quickfixes;

import org.eclipse.cdt.codan.ui.AbstractAstRewriteQuickFix;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeSelector;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.TextFileChange;

import com.cevelop.intwidthfixator.helpers.ConversionHelper;
import com.cevelop.intwidthfixator.helpers.Includes;
import com.cevelop.intwidthfixator.refactorings.conversion.ConversionInfo;

import ch.hsr.ifs.iltis.cpp.core.codan.marker.IInfoMarkerResolution;
import ch.hsr.ifs.iltis.cpp.core.includes.IncludeInsertionUtil;


/**
 * @author tstauber
 */
public class IntQuickFix extends AbstractAstRewriteQuickFix implements IInfoMarkerResolution<ConversionInfo> {

    private static final int NOT_FOUND = -1;
    private ConversionInfo   info;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLabel() {
        return info.mrLabel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modifyAST(final IIndex index, final IMarker marker) {
        try {
            final IASTNode node = findNode(index, marker);
            if (node instanceof ICPPASTSimpleDeclSpecifier && marker.getResource() instanceof IFile) {
                ConversionHelper.createASTRewrite(node).rewriteAST().perform(new NullProgressMonitor());
                IncludeInsertionUtil.includeIfNotYetIncluded(node.getTranslationUnit(), Includes.CSTDINT, true, TextFileChange.FORCE_SAVE);
            }
            marker.delete();
        } catch (final CoreException e) {
            e.printStackTrace();
        }
    }

    protected IASTNode findNode(final IIndex index, final IMarker marker) throws CoreException {
        final ITranslationUnit tu = getTranslationUnitViaEditor(marker);
        final IASTTranslationUnit ast = tu.getAST(index, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);
        final IASTNodeSelector selector = ast.getNodeSelector(ast.getFilePath());
        return selector.findEnclosingNode(getOffset(marker, getDocument()), getLength(marker, getDocument()));
    }

    /**
     * Get length from marker. If {@code CHAR_END} attribute is not set for marker,
     * line and document would be used. {@code CHAR_START} is obtained by using
     * {@link #getOffset(IMarker,IDocument)}.
     * 
     * @param marker
     *        The marker for which the length needs to be calculated
     * @param doc
     *        The document to which the marker belongs
     * @return the markers length
     */
    public int getLength(final IMarker marker, final IDocument doc) {
        final int startOffset = getOffset(marker, doc);
        int endOffset = marker.getAttribute(IMarker.CHAR_END, NOT_FOUND);

        if (endOffset < 0) {
            final int line = marker.getAttribute(IMarker.LINE_NUMBER, NOT_FOUND) - 1;
            try {
                endOffset = (doc.getLineLength(line) - 1) - doc.getLineOffset(line);
            } catch (final BadLocationException e) {
                return 1;
            }
        }
        return endOffset - startOffset;
    }

    @Override
    public ConversionInfo getInfo() {
        return info;
    }

    @Override
    public void configure(ConversionInfo info) {
        this.info = info;
    }

}
