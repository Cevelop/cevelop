package com.cevelop.gslator.quickfixes;

import java.util.HashSet;

import org.eclipse.cdt.codan.ui.AbstractAstRewriteQuickFix;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPNodeFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.CompositeChange;

import ch.hsr.ifs.iltis.cpp.core.codan.marker.IInfoMarkerResolution;

import com.cevelop.gslator.charwarsstub.asttools.ASTModifier;
import com.cevelop.gslator.infos.GslatorInfo;
import com.cevelop.gslator.quickfixes.utils.ASTRewriteStore;
import com.cevelop.gslator.utils.CCGlatorCPPNodeFactory;


@SuppressWarnings("restriction")
public abstract class BaseQuickFix extends AbstractAstRewriteQuickFix implements IInfoMarkerResolution<GslatorInfo> {

    protected static final String FAIL = "There was something wrong";

    protected IMarker         marker;
    protected CPPNodeFactory  factory;
    protected ASTRewriteStore astRewriteStore;

    protected HashSet<String> newHeaders = new HashSet<>();
    protected GslatorInfo     info;

    public BaseQuickFix() {
        super();
        factory = CCGlatorCPPNodeFactory.getCPPNodeFactory();
    }

    @Override
    public void configure(GslatorInfo info) {
        this.info = info;
    }

    @Override
    public GslatorInfo getInfo() {
        return info;
    }

    @Override
    public void apply(IMarker marker, IDocument document) {
        super.apply(marker, document);
        openEditor(marker).doSave(new NullProgressMonitor()); // Fixes bug where some quickfixes didn't work after another ... TODO: pull up to cdt codan?
    }

    @Override
    public boolean isApplicable(final IMarker marker) {
        this.marker = marker;
        return super.isApplicable(marker);
    }

    @Override
    public void modifyAST(final IIndex index, final IMarker marker) {
        this.marker = marker;
        populateASTRewriteStore(index, marker);
        final IASTTranslationUnit ast = astRewriteStore.getASTTranslationUnit(getTranslationUnitViaWorkspace(marker));
        final IASTNode markedNode = getMarkedNode(ast, marker);

        handleMarkedNode(markedNode, astRewriteStore.getASTRewrite(markedNode));
        CompositeChange change = (CompositeChange) astRewriteStore.getChange();
        if (newHeaders.size() > 0) {
            ASTModifier.includeHeaders(newHeaders, change, (IFile) marker.getResource(), ast, getDocument());
        }
        performChange(change, marker);
    }

    protected void performChange(CompositeChange change, final IMarker marker) {
        try {
            change.perform(new NullProgressMonitor());
            marker.delete();
        } catch (final CoreException e) {
            // ErrorLogger.log(ErrorMessages.UNABLE_TO_DELETE_MARKER, e);
        }
    }

    private void populateASTRewriteStore(final IIndex paramIndex, final IMarker marker) {
        IIndex index = paramIndex;
        if (index == null) {
            try {
                index = getIndexFromMarker(marker);
                index.acquireReadLock();
                astRewriteStore = new ASTRewriteStore(index);
            } catch (CoreException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (index != null) {
                    index.releaseReadLock();
                }
            }
        } else {
            astRewriteStore = new ASTRewriteStore(index);
        }
    }

    protected IASTNode getMarkedNode(final IMarker marker) {
        this.marker = marker;
        populateASTRewriteStore(null, marker);

        final IASTTranslationUnit ast = astRewriteStore.getASTTranslationUnit(getTranslationUnitViaWorkspace(marker));
        return getMarkedNode(ast, marker);
    }

    protected IASTNode getMarkedNode(final IASTTranslationUnit astTranslationUnit, final IMarker marker) {
        if (astTranslationUnit == null) {
            return null;
        }
        final int start = marker.getAttribute(IMarker.CHAR_START, -1);
        final int end = marker.getAttribute(IMarker.CHAR_END, -1);
        return astTranslationUnit.getNodeSelector(null).findNode(start, end - start);
    }

    protected abstract void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite);

    protected boolean hasInclude(String includeName, IASTTranslationUnit tu) {
        IASTPreprocessorIncludeStatement[] includes = tu.getIncludeDirectives();
        for (IASTPreprocessorIncludeStatement include : includes) {
            IASTName name = include.getName();
            if (name.toString().equals(includeName) || name.toString().endsWith("/" + includeName)) {
                return true;
            }
        }
        return false;
    }

}
