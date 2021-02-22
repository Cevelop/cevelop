package com.cevelop.ctylechecker.quickfix.includes;

import org.eclipse.cdt.codan.internal.core.model.CodanProblemMarker;
import org.eclipse.cdt.codan.ui.AbstractCodanCMarkerResolution;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;

import ch.hsr.ifs.iltis.cpp.core.codan.marker.IInfoMarkerResolution;

import com.cevelop.ctylechecker.Activator;
import com.cevelop.ctylechecker.infos.CtyleCheckerInfo;


@SuppressWarnings("restriction")
public abstract class AbstractMissingIncludeQuickfix extends AbstractCodanCMarkerResolution implements IInfoMarkerResolution<CtyleCheckerInfo> {

    private CtyleCheckerInfo info;

    @Override
    public String getLabel() {
        return "Add missing '" + info.headerName + "' include.";
    }

    @Override
    public CtyleCheckerInfo getInfo() {
        return info;
    }

    @Override
    public void configure(CtyleCheckerInfo info) {
        this.info = info;
    }

    public abstract void insertInclude(IASTTranslationUnit ast, String headerName);

    @Override
    public void apply(IMarker marker, IDocument document) {
        String[] arguments = CodanProblemMarker.getProblemArguments(marker);
        if (arguments.length < 1) {
            return;
        }
        String headerName = arguments[0];
        ITranslationUnit tu = getTranslationUnitViaEditor(marker);
        try {
            IASTTranslationUnit ast = tu.getAST(null, ITranslationUnit.AST_SKIP_ALL_HEADERS);
            insertInclude(ast, headerName);
            marker.delete();
        } catch (CoreException e) {
            Activator.log(e);
        }
    }
}
