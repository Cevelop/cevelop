package com.cevelop.includator.preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.IIncludeReference;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.resources.IncludatorProject;
import com.cevelop.includator.ui.helpers.ResourceWrapperStore;
import com.cevelop.includator.ui.solutionoperations.SuggestionSolutionOperation;


public class IncludatorPropertyManager extends AbstractPreferenceInitializer {

    public static final String PARSE_INDEX_UPFRONT_SUBDIRS_PROPERTY_NAME           =
                                                                         "com.cevelop.includator.projectproperty.parseIndexUpfrontSubDirs";
    public static final String EXCLUDE_RESOURCES_PROPERTY_NAME                     = "com.cevelop.includator.projectproperty.excludeResources";
    public static final String SUPPRESSED_SUGGESTIONS_PROPERTY_NAME                = "com.cevelop.includator.projectproperty.ignoredIncludes";
    public static final String PARSE_INDEX_UPFRONT_SUBDIRS_PREFERENCE_NAME         =
                                                                           "com.cevelop.includator.preference.defaultParseIndexUpfrontSubDirs";
    public static final String SUGGEST_REMOVAL_OF_COVERED_INCLUDES_PREFERENCE_NAME = "com.cevelop.includator.preference.suggestRemoveCoveredIncludes";
    public static final String NEVER_SHOW_SUGGESTION_DIALOG                        = "com.cevelop.includator.preference.neverShowSuggestionDialog";
    public static final String DONT_SUGGEST_REMOVAL_OF_NAME_CORRELATING_HEADER     =
                                                                               "com.cevelop.includator.preference.dontSuggestRemovalOfNameCorrelatingHeader";
    public static final String ASK_TO_ADAPT_INDEX                                  = "com.cevelop.includator.preference.askToAdaptIndex";
    public static final String WARN_ABOUT_IGNORED_EXTENSION_LESS_FILES             =
                                                                       "com.cevelop.includator.preference.warnAboutIgnoredExtensionLessFiles";

    public static final String DEFAULT_SUGGESTION_DIALOG_OPERATION              =
                                                                   "com.cevelop.includator.projectproperty.defaultOperationForSuggestionDialog";
    public static final String EXCLUDE_SYMBOL_IN_PROJECT_PROPERTY               = "com.cevelop.includator.property.excludeSymbolInProject";
    public static final String EXCLUDE_SYMBOL_IN_FILE_PROPERTY                  = "com.cevelop.includator.property.excludeSymbolInFile";
    public static final String P_EXCLUDE_SYMBOL_IN_WORKSPACE_PREFERENCE         = "com.cevelop.includator.preference.excludeSymbolInWorkspace";
    public static final String ADD_INCLUDES_TO_OTHER_PROJECTS_AS_SYSTEM_INCLUDE =
                                                                                "com.cevelop.includator.preference.addIncludesToOtherProjectsAsSystemIncludes";

    public static void addIncludePathSubDirs(Set<String> includePathsSubDirs, ICProject project) {
        includePathsSubDirs = new LinkedHashSet<>(includePathsSubDirs);
        includePathsSubDirs.addAll(getIncludePathSubDirs(project));

        String newProperty = includePathsSubDirs.toString();
        newProperty = newProperty.substring(1, newProperty.length() - 1);
        if (!newProperty.isEmpty()) {
            setProperty(project.getProject(), PARSE_INDEX_UPFRONT_SUBDIRS_PROPERTY_NAME, newProperty);
        }
    }

    public static void resetIncludePathSubDirs(ICProject project) {
        setProperty(project.getProject(), PARSE_INDEX_UPFRONT_SUBDIRS_PROPERTY_NAME, null);
    }

    public static Set<String> getIncludePathSubDirs(ICProject project) {
        Set<String> result = new LinkedHashSet<>();
        String existingProperty = getProperty(project.getProject(), PARSE_INDEX_UPFRONT_SUBDIRS_PROPERTY_NAME);
        if (existingProperty == "") {
            existingProperty = initParseIndexUpfrontSubdirsProjectProperty(project);
        }
        String[] existingIncludePathSubDir = existingProperty.split(",\\s*");
        result.addAll(Arrays.asList(existingIncludePathSubDir));
        return result;
    }

    public static String initParseIndexUpfrontSubdirsProjectProperty(ICProject project) {
        String projectProperty = getParseIndexUpfrontSubdirsDefaultProperty(project);
        setProperty(project.getProject(), PARSE_INDEX_UPFRONT_SUBDIRS_PROPERTY_NAME, projectProperty);
        return projectProperty;
    }

    public static String getParseIndexUpfrontSubdirsDefaultProperty(ICProject project) {
        IPreferenceStore store = IncludatorPlugin.getDefault().getPreferenceStore();
        String defaultDirsStr = store.getString(PARSE_INDEX_UPFRONT_SUBDIRS_PREFERENCE_NAME);
        Set<String> defaultDirs = new LinkedHashSet<>(Arrays.asList(defaultDirsStr.split(",\\s*")));
        Set<String> existingDirs = getExistingSubset(defaultDirs, project);
        return existingDirs.toString().substring(1, existingDirs.toString().length() - 1);
    }

    public static String getWorkspacePreference(String preferenceKey) {
        IPreferenceStore store = IncludatorPlugin.getDefault().getPreferenceStore();
        return store.getString(preferenceKey);
    }

    private static Set<String> getExistingSubset(Set<String> defaultDirs, ICProject project) {
        Set<String> existingDirs = new LinkedHashSet<>();
        try {
            for (IIncludeReference curIncludeFolder : project.getIncludeReferences()) {
                for (String curSubDir : defaultDirs) {
                    File folder = new File(curIncludeFolder.getPath().toOSString() + File.separator + curSubDir);
                    if (folder.exists() && folder.isDirectory()) {
                        existingDirs.add(curSubDir);
                    }
                }
            }
        } catch (CModelException e) {
            existingDirs.addAll(defaultDirs);
        }
        return existingDirs;
    }

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = IncludatorPlugin.getDefault().getPreferenceStore();
        store.setDefault(PARSE_INDEX_UPFRONT_SUBDIRS_PREFERENCE_NAME, "boost, sys, tr1");
        store.setDefault(SUGGEST_REMOVAL_OF_COVERED_INCLUDES_PREFERENCE_NAME, true);
        store.setDefault(NEVER_SHOW_SUGGESTION_DIALOG, false);
        store.setDefault(DONT_SUGGEST_REMOVAL_OF_NAME_CORRELATING_HEADER, true);
        store.setDefault(DEFAULT_SUGGESTION_DIALOG_OPERATION, Suggestion.SOLUTION_OPERATION_ADD_MARKER.getClass().getName());
        store.setDefault(P_EXCLUDE_SYMBOL_IN_WORKSPACE_PREFERENCE, "");
        store.setDefault(ASK_TO_ADAPT_INDEX, true);
        store.setDefault(WARN_ABOUT_IGNORED_EXTENSION_LESS_FILES, true);
    }

    public static List<String> getExcludedResources(IncludatorProject project) {
        String excludedResources = getProperty(project, EXCLUDE_RESOURCES_PROPERTY_NAME);
        List<String> excludedResourcesList = splitCommaSeparatedResources(excludedResources);
        return excludedResourcesList;
    }

    private static List<String> splitCommaSeparatedResources(String stringList) {
        if (stringList == null || stringList.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(stringList.split(",\\s*"));
    }

    public static Map<String, Set<String>> getSuppressedSuggestions(IncludatorProject project) {
        String ignoredIncludes = getProperty(project, SUPPRESSED_SUGGESTIONS_PROPERTY_NAME);

        List<String> ignoredIncludeEntries = splitCommaSeparatedResources(ignoredIncludes);
        Map<String, Set<String>> ignoredIncludesPerFile = createIncludeIgnoreMap(ignoredIncludeEntries);
        return ignoredIncludesPerFile;
    }

    public static List<String> getExcludedSymbols(IncludatorProject project) {
        String excludedSymbolsProperty = getProperty(project, P_EXCLUDE_SYMBOL_IN_WORKSPACE_PREFERENCE);
        List<String> ignoredIncludeEntries = splitCommaSeparatedResources(excludedSymbolsProperty);
        return ignoredIncludeEntries;
    }

    private static String getProperty(IncludatorProject project, String property) {
        return getProperty(project.getCProject().getProject(), property);
    }

    private static String getProperty(IProject project, String propertyName) {
        ResourceWrapperStore store = new ResourceWrapperStore();
        store.setResource(project);
        if (store.contains(propertyName)) {
            return store.getString(propertyName);
        } else {
            return IncludatorPlugin.getDefault().getPreferenceStore().getString(propertyName);
        }
    }

    public static void setProperty(IProject project, String property, String value) {
        ResourceWrapperStore store = new ResourceWrapperStore();
        store.setResource(project);
        store.setValue(property, value);
    }

    private static Map<String, Set<String>> createIncludeIgnoreMap(List<String> ignoredIncludeEntries) {
        Map<String, Set<String>> ignoredIncludesPerFile = new HashMap<>();
        for (String ignoredEntry : ignoredIncludeEntries) {
            String[] ignoredParts = ignoredEntry.split(": ");
            if (ignoredParts.length > 1) {
                String file = ignoredParts[0];
                if (!ignoredIncludesPerFile.containsKey(file)) {
                    ignoredIncludesPerFile.put(file, new TreeSet<String>());
                }
                Set<String> ignoredIncludesOfFile = ignoredIncludesPerFile.get(file);
                ignoredIncludesOfFile.add(ignoredParts[1]);
            }
        }
        return ignoredIncludesPerFile;
    }

    public static void addIgnoredInclude(IProject project, String filePath, String include) {
        String ignoredIncludes = getProperty(project, SUPPRESSED_SUGGESTIONS_PROPERTY_NAME);
        String newEntry = filePath + ": " + include;
        if (ignoredIncludes.contains(newEntry)) {
            return;
        }
        if (!ignoredIncludes.isEmpty()) {
            ignoredIncludes = ignoredIncludes + ",";
        }
        ignoredIncludes = ignoredIncludes + newEntry;
        setProperty(project, SUPPRESSED_SUGGESTIONS_PROPERTY_NAME, ignoredIncludes);
    }

    public static SuggestionSolutionOperation getDefaultDialogOperation(IncludatorProject project) {
        String defaultOperationClassName = getProperty(project, IncludatorPropertyManager.DEFAULT_SUGGESTION_DIALOG_OPERATION);
        for (SuggestionSolutionOperation curOp : Suggestion.DEFAULT_SUGGESTION_SOLUTION_OPERATIONS) {
            if (defaultOperationClassName.equals(curOp.getClass().getName())) {
                return curOp;
            }
        }
        return null;
    }

    public static boolean getBooleanProperty(IProject project, String propertyName) {
        ResourceWrapperStore store = new ResourceWrapperStore();
        store.setResource(project);
        if (store.contains(propertyName)) {
            return store.getBoolean(propertyName);
        } else {
            return IncludatorPlugin.getDefault().getPreferenceStore().getBoolean(propertyName);
        }
    }

    public static boolean getBooleanProperty(IncludatorProject project, String propertyName) {
        return getBooleanProperty(project.getCProject().getProject(), propertyName);
    }
}
