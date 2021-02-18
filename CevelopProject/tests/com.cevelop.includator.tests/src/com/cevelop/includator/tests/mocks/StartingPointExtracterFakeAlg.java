package com.cevelop.includator.tests.mocks;

import java.util.HashSet;
import java.util.Set;

import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.startingpoints.AlgorithmStartingPoint;


public class StartingPointExtracterFakeAlg extends Algorithm {

    private AlgorithmStartingPoint point;
    private final AlgorithmScope   scope;

    public StartingPointExtracterFakeAlg(AlgorithmScope scope) {
        this.scope = scope;
    }

    @Override
    protected void run() {
        this.point = startingPoint;
    }

    @Override
    public AlgorithmScope getScope() {
        return scope;
    }

    @Override
    public Set<Class<? extends Algorithm>> getInvolvedAlgorithmTypes() {
        HashSet<Class<? extends Algorithm>> set = new HashSet<>();
        set.add(getClass());
        return set;
    }

    @Override
    public String getInitialProgressMonitorMessage(String resourceName) {
        return "StartingPointExtracterFakeAlg";
    }

    public AlgorithmStartingPoint getStartingPoint() {
        return point;
    }
}
