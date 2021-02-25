package com.cevelop.includator.helpers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.comparators.StatusComparator;


public class IncludatorStatusCollector {

    private static final Map<Integer, String> groupNames = new HashMap<>();

    private final StatiGroups groupedStati;

    static {
        groupNames.put(IStatus.INFO, "Infos:");
        groupNames.put(IStatus.WARNING, "Warnings:");
        groupNames.put(IStatus.ERROR, "Errors:");
    }

    public IncludatorStatusCollector() {
        groupedStati = new StatiGroups();
    }

    public void clear() {
        groupedStati.clear();
    }

    public void collectStatus(MultiStatus status) {
        for (StatusSeverityGroup curGroup : groupedStati.values()) {
            status.add(curGroup.toStatus());
        }
        clear();
    }

    public void logStatus(IStatus status, String affectedPath) {
        groupedStati.get(status.getSeverity()).get(affectedPath).add(status);
    }

    private static class StatiGroups extends LinkedHashMap<Integer, StatusSeverityGroup> {

        private static final long serialVersionUID = -7812856765792708486L;

        @Override
        public StatusSeverityGroup get(Object key) {
            int intKey = (Integer) key;
            if (!containsKey(key)) {
                put(intKey, new StatusSeverityGroup(intKey));
            }
            return super.get(key);
        }
    }

    private static class StatusSeverityGroup extends LinkedHashMap<String, StatusFileGroup> {

        private static final long serialVersionUID = 620216723721842040L;
        private final String      categoryName;

        public StatusSeverityGroup(int id) {
            categoryName = groupNames.get(id);
        }

        @Override
        public StatusFileGroup get(Object key) {
            String strKey = (String) key;
            if (!containsKey(key)) {
                put(strKey, new StatusFileGroup(strKey));
            }
            return super.get(key);
        }

        public IStatus toStatus() {
            MultiStatus result = new MultiStatus(IncludatorPlugin.PLUGIN_ID, IncludatorStatus.STATUS_CODE_SEVERITY_GROUP, categoryName, null);
            for (StatusFileGroup curFileGroup : values()) {
                result.add(curFileGroup.toStatus());
            }
            return result;
        }
    }

    private static class StatusFileGroup extends TreeSet<IStatus> {

        private static final long serialVersionUID = -3274975248147749828L;
        private final String      path;

        public StatusFileGroup(String path) {
            super(new StatusComparator());
            this.path = (path != null) ? path : "File unspecified";
        }

        public IStatus toStatus() {
            MultiStatus result = new MultiStatus(IncludatorPlugin.PLUGIN_ID, IncludatorStatus.STATUS_CODE_FILE_GROUP, path, null);
            for (IStatus curStatus : this) {
                result.add(curStatus);
            }
            return result;
        }
    }
}
