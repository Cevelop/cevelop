package com.cevelop.includator.optimizer;

import java.util.Arrays;
import java.util.List;

import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.startingpoints.AlgorithmStartingPoint;


public class FileScopedAlgorithm extends ScopedAlgorithm {

    @Override
    protected List<IncludatorFile> getAffectedFiles(AlgorithmStartingPoint startingPoint) {
        return Arrays.asList(new IncludatorFile[] { startingPoint.getFile() });
    }

    @Override
    public AlgorithmScope getScope() {
        return AlgorithmScope.EDITOR_SCOPE;
    }
}
