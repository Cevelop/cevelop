package com.cevelop.includator.optimizer;

import java.util.List;

import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.startingpoints.AlgorithmStartingPoint;


public class ProjectScopedAlgorithm extends ScopedAlgorithm {

    @Override
    protected List<IncludatorFile> getAffectedFiles(AlgorithmStartingPoint startingPoint) {
        return startingPoint.getProject().getAffectedFiles();
    }

    @Override
    public AlgorithmScope getScope() {
        return AlgorithmScope.PROJECT_SCOPE;
    }
}
