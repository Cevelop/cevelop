package com.cevelop.includator.tests.mocks;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.optimizer.organizeincludes.OrganizeIncludesAlgorithm;
import com.cevelop.includator.optimizer.organizeincludes.OtherFileCoverageData;
import com.cevelop.includator.resources.IncludatorFile;


public class OrganizeIncludesCoverageDataMockAlg extends OrganizeIncludesAlgorithm {

    public OrganizeIncludesCoverageDataMockAlg(IncludatorFile activeFile) {
        file = activeFile;
        initSuggestionList();
        this.setSuppressionList(new HashMap<String, Set<String>>());
    }

    public List<OtherFileCoverageData> getIncludeCoverageDatas() {
        return addedSuggestionsCoverageData;
    }

    @Override
    public void handleUnincludedDeclRefDependency(DeclarationReferenceDependency dependency) {
        super.handleUnincludedDeclRefDependency(dependency);
    }

    @Override
    public void initCoverageInfo() {
        super.initCoverageInfo();
    }
}
