/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.quickfixes.visibility;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

import com.cevelop.tdd.infos.VisibilityInfo;
import com.cevelop.tdd.quickfixes.TddQuickfix;
import com.cevelop.tdd.refactorings.visibility.VisibilityRefactoring;

import ch.hsr.ifs.iltis.core.core.functional.functions.Function2;


public class VisibilityQuickfix extends TddQuickfix<VisibilityInfo> {

    @Override
    public String getLabel() {
        return NLS.bind(Messages.ChangeVisibilityQuickfixLabel, info.memberName);
    }

    @Override
    public Image getImage() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    protected Function2<ICElement, VisibilityInfo, VisibilityRefactoring> getRefactoringConstructor() {
        return VisibilityRefactoring::new;
    }
}
