package com.cevelop.includator.optimizer.directlyincludereferenceddeclaration;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.includator.optimizer.SuggestionInitializationData;
import com.cevelop.includator.resources.IncludatorFile;


public class DirectlyInitializationData extends SuggestionInitializationData {

    IASTTranslationUnit ast;

    public DirectlyInitializationData(IncludatorFile file, IASTTranslationUnit ast) {
        super(file);
        this.ast = ast;
    }

    @Override
    public void dispose() {
        ast = null;
        super.dispose();
    }
}
