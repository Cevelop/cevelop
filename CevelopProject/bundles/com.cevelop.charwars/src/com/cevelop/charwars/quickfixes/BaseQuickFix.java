package com.cevelop.charwars.quickfixes;

import java.util.HashSet;

import org.eclipse.cdt.codan.ui.AbstractAstRewriteQuickFix;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;

import ch.hsr.ifs.iltis.cpp.core.codan.marker.IInfoMarkerResolution;

import com.cevelop.charwars.asttools.ASTAnalyzer;
import com.cevelop.charwars.asttools.ASTModifier;
import com.cevelop.charwars.asttools.ASTRewriteCache;
import com.cevelop.charwars.constants.ErrorMessages;
import com.cevelop.charwars.dialogs.ErrorRefactoring;
import com.cevelop.charwars.dialogs.ErrorRefactoringWizard;
import com.cevelop.charwars.info.CharwarsInfo;
import com.cevelop.charwars.utils.ErrorLogger;


public abstract class BaseQuickFix extends AbstractAstRewriteQuickFix implements IInfoMarkerResolution<CharwarsInfo> {

    protected HashSet<String> headers = new HashSet<>();

    protected IMarker currentMarker = null;

    protected CharwarsInfo info;

    public void configure(CharwarsInfo info) {
        this.info = info;
    }

    public CharwarsInfo getInfo() {
        return info;
    }

    public boolean isApplicable(IMarker marker) {
        currentMarker = marker;
        return super.isApplicable(marker);
    }

    protected IIndex getIndexFromMarker(IMarker marker) throws CoreException {
        CCorePlugin.getIndexManager().joinIndexer(1000, new NullProgressMonitor());
        return super.getIndexFromMarker(marker);
    }

    public void modifyAST(IIndex index, IMarker marker) {
        try {
            currentMarker = marker;
            final ASTRewriteCache rewriteCache = new ASTRewriteCache(index);
            final IASTTranslationUnit astTranslationUnit = rewriteCache.getASTTranslationUnit(getTranslationUnitViaEditor(marker));
            IASTNode markedNode = getMarkedNode(astTranslationUnit, marker);
            if (markedNode instanceof IASTName) {
                markedNode = markedNode.getParent();
            }

            final ASTRewrite mainRewrite = getRewrite(rewriteCache, markedNode);
            handleMarkedNode(markedNode, mainRewrite, rewriteCache);

            final CompositeChange change = (CompositeChange) rewriteCache.getChange();
            final IFile file = (IFile) marker.getResource();
            ASTModifier.includeHeaders(headers, change, file, astTranslationUnit, getDocument());
            performChange(change, marker);
        } catch (final Exception e) {
            e.printStackTrace();
            final ErrorRefactoring refactoring = new ErrorRefactoring(getErrorMessage());
            final ErrorRefactoringWizard refactoringWizard = new ErrorRefactoringWizard(refactoring, 0);
            final RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(refactoringWizard);
            ErrorLogger.log(e.getMessage(), e);

            try {
                op.run(null, ErrorMessages.ALERT_BOX_TITLE);
            } catch (final InterruptedException e1) {
                ErrorLogger.log(ErrorMessages.UNABLE_TO_SHOW_ALERT_BOX, e1);
            }
        }
    }

    private IASTNode getMarkedNode(IASTTranslationUnit astTranslationUnit, IMarker marker) {
        final int start = marker.getAttribute(IMarker.CHAR_START, -1);
        final int end = marker.getAttribute(IMarker.CHAR_END, -1);
        return ASTAnalyzer.getMarkedNode(astTranslationUnit, start, end - start);
    }

    protected abstract void handleMarkedNode(IASTNode markedNode, ASTRewrite rewrite, ASTRewriteCache rewriteCache);

    protected abstract String getErrorMessage();

    private void performChange(Change change, IMarker marker) {
        try {
            change.perform(new NullProgressMonitor());
            marker.delete();
        } catch (final CoreException e) {
            ErrorLogger.log(ErrorMessages.UNABLE_TO_DELETE_MARKER, e);
        }
    }

    private ASTRewrite getRewrite(ASTRewriteCache rewriteCache, IASTNode node) {
        return rewriteCache.getASTRewrite(node.getTranslationUnit().getOriginatingTranslationUnit());
    }
}
