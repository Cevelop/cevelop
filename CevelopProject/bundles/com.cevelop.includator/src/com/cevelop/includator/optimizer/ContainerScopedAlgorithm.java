package com.cevelop.includator.optimizer;

import java.util.List;

import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.startingpoints.AlgorithmStartingPoint;


public class ContainerScopedAlgorithm extends ScopedAlgorithm {

    @Override
    protected List<IncludatorFile> getAffectedFiles(AlgorithmStartingPoint startingPoint) {
        return startingPoint.getAffectedFiles();
    }

    @Override
    public AlgorithmScope getScope() {
        return AlgorithmScope.CONTAINER_SCOPE;
    }
}
