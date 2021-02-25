package com.cevelop.includator.optimizer.findunusedincludes;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.eclipse.cdt.core.index.IIndexInclude;
import org.eclipse.core.runtime.CoreException;


public class IncludeCoverageInfo {

    private final Map<IIndexInclude, Collection<IIndexInclude>> coverageData;

    public IncludeCoverageInfo() {
        coverageData = new HashMap<>();
    }

    public void setCoveredBy(IIndexInclude coveringInclude, IIndexInclude coveredInclude) {
        if (!coverageData.containsKey(coveredInclude)) {
            coverageData.put(coveredInclude, new TreeSet<>(new IndexComparator()));
        }
        coverageData.get(coveredInclude).add(coveringInclude);
    }

    public Collection<IIndexInclude> getCoveringIncludes(IIndexInclude coveredIncludes) {
        return coverageData.get(coveredIncludes);
    }

    public boolean isCoveredByOtherIncludes(IIndexInclude candidate) {
        return coverageData.containsKey(candidate);
    }

    private static class IndexComparator implements Comparator<IIndexInclude> {

        @Override
        public int compare(IIndexInclude o1, IIndexInclude o2) {
            try {
                return o1.getFullName().compareTo(o2.getFullName());
            } catch (CoreException e) {
                return o1.hashCode() - o2.hashCode();
            }
        }
    }
}
