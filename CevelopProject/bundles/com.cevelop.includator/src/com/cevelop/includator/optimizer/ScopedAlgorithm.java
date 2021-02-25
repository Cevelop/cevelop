package com.cevelop.includator.optimizer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.SubMonitor;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.ProgressMonitorHelper;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.startingpoints.AlgorithmStartingPoint;
import com.cevelop.includator.startingpoints.FileStartingPoint;


public abstract class ScopedAlgorithm extends Algorithm {

    private Algorithm wrappedAlg;

    protected abstract List<IncludatorFile> getAffectedFiles(AlgorithmStartingPoint startingPoint);

    @Override
    protected void run() {
        Collection<IncludatorFile> affectedFiles = getAffectedFiles(startingPoint);

        if (affectedFiles.size() < 1) {
            return;
        }
        int workPerFile = ProgressMonitorHelper.ALG_WORK / affectedFiles.size();
        for (IncludatorFile curFile : affectedFiles) {
            if (monitor.isCanceled()) {
                return;
            }
            SubMonitor subMonitor = SubMonitor.convert(monitor, workPerFile);
            wrappedAlg.start(new FileStartingPoint(startingPoint.getActiveWorkbenchWindow(), curFile), subMonitor);
            addAllSuggestions(wrappedAlg.getSuggestions());
            IncludatorPlugin.releaseCDTResources();
            wrappedAlg.reset();
            startingPoint.getProject().releaseIndexReadLock();
            startingPoint.getProject().acquireIndexReadLock();
        }
    }

    @Override
    public Set<Class<? extends Algorithm>> getInvolvedAlgorithmTypes() {
        return wrappedAlg.getInvolvedAlgorithmTypes();
    }

    @Override
    public String getInitialProgressMonitorMessage(String resourceName) {
        return wrappedAlg.getInitialProgressMonitorMessage(resourceName);
    }

    public void setWrappedAlg(Algorithm wrappedAlg) {
        this.wrappedAlg = wrappedAlg;
    }

    @Override
    public void reset() {
        wrappedAlg.reset();
        wrappedAlg = null;
        super.reset();
    }

    @Override
    public void setSuppressionList(Map<String, Set<String>> suppressedSuggestionList) {
        wrappedAlg.setSuppressionList(suppressedSuggestionList);
    }
}
