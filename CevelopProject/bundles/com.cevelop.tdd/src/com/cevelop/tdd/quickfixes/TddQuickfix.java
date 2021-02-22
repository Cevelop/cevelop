/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.quickfixes;

import org.eclipse.cdt.codan.ui.AbstractCodanCMarkerResolution;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ui.IMarkerResolution2;

import ch.hsr.ifs.iltis.core.functional.functions.Function2;

import ch.hsr.ifs.iltis.cpp.core.codan.marker.IInfoMarkerResolution;
import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;
import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.SelectionRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringContext;

import com.cevelop.tdd.refactorings.TddRefactoringContext;


public abstract class TddQuickfix<I extends MarkerInfo<I>> extends AbstractCodanCMarkerResolution implements IInfoMarkerResolution<I>,
        IMarkerResolution2 {

    protected I info;

    @Override
    public void apply(IMarker marker, IDocument document) {
        if (!isApplicable(marker)) {
            return;
        }

        ITranslationUnit tu = getTranslationUnitViaEditor(marker);

        SelectionRefactoring<I> refactoring = getRefactoringConstructor().apply(tu, info);

        CRefactoringContext context = new TddRefactoringContext(refactoring);
        try {
            refactoring.checkFinalConditions(new NullProgressMonitor());
            Change change = refactoring.createChange(new NullProgressMonitor());
            change.perform(new NullProgressMonitor());
            change.dispose();
        } catch (CoreException e) {
            CUIPlugin.log(e);
        } finally {
            context.dispose();
        }
    }

    protected abstract Function2<ICElement, I, ? extends SelectionRefactoring<I>> getRefactoringConstructor();

    @Override
    public I getInfo() {
        return info;
    }

    @Override
    public void configure(I info) {
        this.info = info;
    }
}
