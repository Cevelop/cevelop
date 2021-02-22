package com.cevelop.includator.optimizer.findunusedincludes;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;

import com.cevelop.includator.optimizer.SuggestionInitializationData;
import com.cevelop.includator.resources.IncludatorFile;


public class UnusedIncludeInitializationData extends SuggestionInitializationData {

    IASTPreprocessorIncludeStatement include;

    public UnusedIncludeInitializationData(IncludatorFile file, IASTPreprocessorIncludeStatement include) {
        super(file);
        this.include = include;
    }

    @Override
    public void dispose() {
        include = null;
        super.dispose();
    }
}
