/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.quickfixes.create.variable.member;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.ui.CDTSharedImages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

import com.cevelop.tdd.infos.MemberVariableInfo;
import com.cevelop.tdd.quickfixes.TddQuickfix;
import com.cevelop.tdd.refactorings.create.variable.member.CreateMemberVariableRefactoring;

import ch.hsr.ifs.iltis.core.functional.functions.Function2;


public class CreateMemberVariableQuickfix extends TddQuickfix<MemberVariableInfo> {

    @Override
    public String getLabel() {
        return NLS.bind(Messages.CreateMemberVariableQuickfixLabel, info.name);
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public Image getImage() {
        return CDTSharedImages.getImage(CDTSharedImages.IMG_OBJS_PUBLIC_METHOD);
    }

    @Override
    protected Function2<ICElement, MemberVariableInfo, CreateMemberVariableRefactoring> getRefactoringConstructor() {
        return CreateMemberVariableRefactoring::new;
    }
}
