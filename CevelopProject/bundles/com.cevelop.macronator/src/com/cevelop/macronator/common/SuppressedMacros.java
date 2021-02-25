package com.cevelop.macronator.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.cevelop.macronator.MacronatorPlugin;


public class SuppressedMacros {

    private Set<String>       suppressedMacros;
    private final Preferences projectPreferences;

    public SuppressedMacros(final IProject project) {
        projectPreferences = getProjectNode(project);
        suppressedMacros = new HashSet<>();
    }

    public void add(final String macroName) {
        if (macroName.length() > 0) {
            suppressedMacros.add(macroName);
        }
        persistSuppressedMacros();
    }

    public void remove(final String macroName) {
        suppressedMacros.remove(macroName);
        persistSuppressedMacros();
    }

    public boolean isSuppressed(final String macroName) {
        readSuppressedMacros();
        return suppressedMacros.contains(macroName);
    }

    private void readSuppressedMacros() {
        final String suppressedMacroProperty = projectPreferences.get(MacronatorPlugin.SUPPRESSED_MACROS_PREF_KEY, "");
        final String[] split = suppressedMacroProperty.split(";");
        suppressedMacros = new HashSet<>(Arrays.asList(split));
    }

    private Preferences getProjectNode(final IProject project) {
        final IScopeContext projectScope = new ProjectScope(project);
        final Preferences projectNode = projectScope.getNode(MacronatorPlugin.PLUGIN_ID);
        return projectNode;
    }

    private void persistSuppressedMacros() {
        final StringBuilder suppressedMacroProperty = new StringBuilder();
        for (final String macro : suppressedMacros) {
            suppressedMacroProperty.append(macro + ";");
        }
        projectPreferences.put(MacronatorPlugin.SUPPRESSED_MACROS_PREF_KEY, suppressedMacroProperty.toString());
        try {
            projectPreferences.flush();
        } catch (final BackingStoreException e) {
            e.printStackTrace();
        }
    }
}
