/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.quickfixes.argument;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.swt.graphics.Image;

import com.cevelop.tdd.infos.ArgumentMismatchInfo;
import com.cevelop.tdd.quickfixes.TddQuickfix;
import com.cevelop.tdd.refactorings.argument.ArgumentRefactoring;

import ch.hsr.ifs.iltis.core.functional.functions.Function2;


public class ArgumentMismatchQuickfix extends TddQuickfix<ArgumentMismatchInfo> {

    private final String label;
    private final Image  image;

    public ArgumentMismatchQuickfix(String label, Image image) {
        this.label = label;
        this.image = image;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    protected Function2<ICElement, ArgumentMismatchInfo, ArgumentRefactoring> getRefactoringConstructor() {
        return ArgumentRefactoring::new;
    }
}
