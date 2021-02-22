/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.quickfixes.create.function.constructor;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.ui.CDTSharedImages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

import com.cevelop.tdd.infos.ConstructorInfo;
import com.cevelop.tdd.quickfixes.TddQuickfix;
import com.cevelop.tdd.refactorings.create.function.constructor.CreateConstructorRefactoring;

import ch.hsr.ifs.iltis.core.functional.functions.Function2;


public class CreateConstructorQuickfix extends TddQuickfix<ConstructorInfo> {

    @Override
    public String getLabel() {
        return NLS.bind(Messages.CreateConstructorQuickfixLabel, info.typeName);
    }

    @Override
    public Image getImage() {
        return CDTSharedImages.getImage(CDTSharedImages.IMG_OBJS_FUNCTION);
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    protected Function2<ICElement, ConstructorInfo, CreateConstructorRefactoring> getRefactoringConstructor() {
        return CreateConstructorRefactoring::new;
    }
}
