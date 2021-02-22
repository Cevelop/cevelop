package com.cevelop.codeanalysator.core.quickfix;

import org.eclipse.cdt.codan.ui.AbstractAstRewriteQuickFix;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;
import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.IBetterFactory;

import com.cevelop.codeanalysator.core.util.ASTRewriteStore;


public abstract class BaseQuickFix extends AbstractAstRewriteQuickFix {

    /**
     * Utility node factory instance for derived quick fix classes
     */
    protected static final IBetterFactory factory = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();

    private final String label;

    /**
     * Initializes a new instance of BaseQuickFix.
     *
     * @param label
     * a short label describing what the quick fix does
     */
    protected BaseQuickFix(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void apply(IMarker marker, IDocument document) {
        super.apply(marker, document);
        openEditor(marker).doSave(new NullProgressMonitor()); // Fixes bug where some quickfixes didn't work after another ... TODO: pull up to cdt codan?
    }

    @Override
    public void modifyAST(final IIndex index, final IMarker marker) {
        ASTRewriteStore astRewriteStore = new ASTRewriteStore(index);
        IASTTranslationUnit ast = astRewriteStore.getASTTranslationUnit(getTranslationUnitViaWorkspace(marker));
        IASTNode markedNode = getMarkedNode(ast, marker);

        handleMarkedNode(markedNode, astRewriteStore.getASTRewrite(markedNode));
        Change change = astRewriteStore.getChange();
        try {
            change.perform(new NullProgressMonitor());
            marker.delete();
        } catch (final CoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return label;
    }

    /**
     * Resolve rule violation, i.e. perform the quick fix.
     *
     * @param markedNode
     * the offending node
     * @param hRewrite
     * rewriter to record necessary changes to the AST
     */
    protected abstract void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite);

    private IASTNode getMarkedNode(final IASTTranslationUnit astTranslationUnit, final IMarker marker) {
        int start = marker.getAttribute(IMarker.CHAR_START, -1);
        int end = marker.getAttribute(IMarker.CHAR_END, -1);
        return astTranslationUnit.getNodeSelector(null).findNode(start, end - start);
    }
}
