package com.cevelop.conanator.buildinfomanager;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.BackingStoreException;

import com.cevelop.conanator.Activator;


public class ConanBuildInfoPrefStore {

    private static final String DELIMITER = ";";
    private IEclipsePreferences projectNode;

    public ConanBuildInfoPrefStore(IProject project) {
        IScopeContext projectScope = new ProjectScope(project);
        projectNode = projectScope.getNode(Activator.PLUGIN_ID);
    }

    public Collection<String> getStoredBuildInfo(String key) {
        if (projectNode != null) {
            String nodeValue = projectNode.get(key, "");
            return Arrays.asList(nodeValue.split(DELIMITER));
        }
        return Collections.emptyList();
    }

    public void storeBuildInfo(String key, Collection<String> values) throws BackingStoreException {
        if (projectNode != null) {
            projectNode.put(key, String.join(DELIMITER, values));
            projectNode.flush();
        }

    }
}
