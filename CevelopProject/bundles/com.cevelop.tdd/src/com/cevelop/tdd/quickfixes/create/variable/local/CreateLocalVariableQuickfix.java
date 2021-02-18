/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.quickfixes.create.variable.local;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.ui.CDTSharedImages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

import com.cevelop.tdd.infos.LocalVariableInfo;
import com.cevelop.tdd.quickfixes.TddQuickfix;
import com.cevelop.tdd.refactorings.create.variable.local.CreateLocalVariableRefactoring;

import ch.hsr.ifs.iltis.core.core.functional.functions.Function2;


public class CreateLocalVariableQuickfix extends TddQuickfix<LocalVariableInfo> {

    @Override
    public String getLabel() {
        return NLS.bind(Messages.CreateLocalVariableQuickfixLabel, info.name);
    }

    @Override
    public Image getImage() {
        return CDTSharedImages.getImage(CDTSharedImages.IMG_OBJS_LOCAL_VARIABLE);
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    protected Function2<ICElement, LocalVariableInfo, CreateLocalVariableRefactoring> getRefactoringConstructor() {
        return CreateLocalVariableRefactoring::new;
    }
}
