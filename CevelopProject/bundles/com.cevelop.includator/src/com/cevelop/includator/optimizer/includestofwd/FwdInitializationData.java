package com.cevelop.includator.optimizer.includestofwd;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;

import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.optimizer.SuggestionInitializationData;
import com.cevelop.includator.resources.IncludatorFile;


public class FwdInitializationData extends SuggestionInitializationData {

    IASTPreprocessorIncludeStatement     includeToRemove;
    List<DeclarationReferenceDependency> insertFwdCandidates;

    public FwdInitializationData(IncludatorFile file, IASTPreprocessorIncludeStatement includeToRemove,
                                 List<DeclarationReferenceDependency> insertFwdCandidates) {
        super(file);
        this.includeToRemove = includeToRemove;
        this.insertFwdCandidates = insertFwdCandidates;
    }

    @Override
    public void dispose() {
        includeToRemove = null;
        if (insertFwdCandidates != null) {
            insertFwdCandidates.clear();
        }
        insertFwdCandidates = null;
        super.dispose();
    }
}
