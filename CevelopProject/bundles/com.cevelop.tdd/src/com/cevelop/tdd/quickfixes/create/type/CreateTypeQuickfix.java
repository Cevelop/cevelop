/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.quickfixes.create.type;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.corext.fix.LinkedProposalPositionGroup.Proposal;
import org.eclipse.cdt.ui.CDTSharedImages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

import ch.hsr.ifs.iltis.core.functional.functions.Function2;

import com.cevelop.tdd.infos.TypeInfo;
import com.cevelop.tdd.quickfixes.TddQuickfix;
import com.cevelop.tdd.refactorings.create.type.CreateTypeRefactoring;


/**
 * Interface between UI and refactoring. Controls the CreateClassRefactoring and sets up linked mode editing after the changes have been performed.
 */
public class CreateTypeQuickfix extends TddQuickfix<TypeInfo> {

    public static Proposal[] getTypeProposals() {
        return new Proposal[] { new Proposal("class", CDTSharedImages.getImage(CDTSharedImages.IMG_OBJS_CLASS), 0), new Proposal("struct",
                CDTSharedImages.getImage(CDTSharedImages.IMG_OBJS_STRUCT), 0), new Proposal("enum", CDTSharedImages.getImage(
                        CDTSharedImages.IMG_OBJS_ENUMERATION), 0) };
    }

    @Override
    public String getLabel() {
        return NLS.bind(Messages.CreateTypeQuickfixLabel, info.typeName);
    }

    @Override
    public Image getImage() {
        return CDTSharedImages.getImage(CDTSharedImages.IMG_OBJS_STRUCT);
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    protected Function2<ICElement, TypeInfo, CreateTypeRefactoring> getRefactoringConstructor() {
        return CreateTypeRefactoring::new;
    }
}
