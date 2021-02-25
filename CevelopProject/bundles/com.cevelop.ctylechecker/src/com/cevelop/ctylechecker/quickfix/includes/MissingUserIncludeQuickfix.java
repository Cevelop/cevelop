package com.cevelop.ctylechecker.quickfix.includes;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import ch.hsr.ifs.iltis.cpp.core.includes.IncludeInsertionUtil;


public class MissingUserIncludeQuickfix extends AbstractMissingIncludeQuickfix {

    @Override
    public void insertInclude(IASTTranslationUnit ast, String headerName) {
        IncludeInsertionUtil.insertUserIncludeIfNeeded(ast, headerName);
    }
}
