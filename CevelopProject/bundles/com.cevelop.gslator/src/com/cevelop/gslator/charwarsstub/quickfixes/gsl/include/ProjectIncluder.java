/*
 * To avoid a Plugin Dependency Files with this header are copied from the Eclipse CUTE
 * Plugin "CharWars". In order to avoid unnecessary Class Dependencies some lines may be
 * commented out and additionally the package (and packages of other imported "CharWars"
 * Classes) got renamed from ch.hsr.ifs.cute.charwars to ch.hsr.ifs.cute.ccglator.charwarsstub
 * Some needed Adapter-Classes are found in ch.hsr.ifs.cute.ccglator.charwarsstub.stubadapter
 * and are not from the Plugin "CharWars" but created to further reduce the amount of copied files.
 */
package com.cevelop.gslator.charwarsstub.quickfixes.gsl.include;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.settings.model.CIncludePathEntry;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICFolderDescription;
import org.eclipse.cdt.core.settings.model.ICLanguageSetting;
import org.eclipse.cdt.core.settings.model.ICLanguageSettingEntry;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICSettingEntry;
import org.eclipse.cdt.managedbuilder.core.ManagedCProjectNature;
//import org.eclipse.cdt.utils.Platform;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;

import com.cevelop.gslator.charwarsstub.constants.PreferenceKeys;
import com.cevelop.gslator.charwarsstub.stubadapter.CharWarsPlugin;


public class ProjectIncluder {

    public static final String[] gslIncludeNames = new String[] { "gsl.h", "gsl_assert.h", "gsl_util.h", "span.h", "string_span.h", "gslrefactor.h" };

    public static void createAndLinkProject(IASTTranslationUnit unit) {
        IPreferenceStore prefs = CharWarsPlugin.getDefault().getPreferenceStore();

        if (prefs.getBoolean(PreferenceKeys.GENERATE_GSL_INCLUDE)) {
            try {
                ProjectIncluder.createProject();
                ProjectIncluder.createLinkToProject(unit);
            } catch (CoreException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void createProject() throws CoreException {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject project = root.getProject(getGslProjectName());
        if (project.exists()) {
            return;
        }

        IProgressMonitor monitor = new NullProgressMonitor();
        project.create(monitor);
        project.open(monitor);
        CProjectNature.addCNature(project, monitor);
        CCProjectNature.addCCNature(project, monitor);
        ManagedCProjectNature.addManagedBuilder(project, monitor);
        addGslToProject(project, monitor);

    }

    private static void addGslToProject(IProject project, IProgressMonitor monitor) throws CoreException {
        for (String includeName : gslIncludeNames) {
            try {
                URL gsl = Platform.getBundle("com.cevelop.gslator").getResource("/resources/gsl/" + includeName); // Modified for ch.hsr.ifs.cute.ccglator.charwarsstub
                project.getFile(includeName).create(gsl.openStream(), false, monitor);
            } catch (IOException e1) {
                throw new RuntimeException("Static GSL ressources not found", e1);
            } catch (CoreException re) {
                // If file already exists --> ignore
            }
        }
    }

    public static void createLinkToProject(IASTTranslationUnit iastTranslationUnit) throws CoreException {
        ITranslationUnit iTranslationUnit = iastTranslationUnit.getOriginatingTranslationUnit();
        ICProject cProject = iTranslationUnit.getCProject();
        IProject project = cProject.getProject();
        ICProjectDescription projectDescription = CoreModel.getDefault().getProjectDescription(project, true);
        ICConfigurationDescription[] configDecriptions = projectDescription.getConfigurations();
        linkBuildConfigurations(configDecriptions);
        CoreModel.getDefault().setProjectDescription(project, projectDescription);
    }

    private static void linkBuildConfigurations(ICConfigurationDescription[] configDecriptions) {
        for (ICConfigurationDescription configDescription : configDecriptions) {
            ICFolderDescription projectRoot = configDescription.getRootFolderDescription();
            ICLanguageSetting[] settings = projectRoot.getLanguageSettings();
            linkCppLanguageInclueds(settings);
        }
    }

    private static void linkCppLanguageInclueds(ICLanguageSetting[] settings) {
        for (ICLanguageSetting setting : settings) {
            if (!"org.eclipse.cdt.core.g++".equals(setting.getLanguageId())) {
                continue;
            }
            addLinkIfNeeded(setting);
        }
    }

    private static void addLinkIfNeeded(ICLanguageSetting setting) {
        List<ICLanguageSettingEntry> includes = new ArrayList<>();
        includes.addAll(setting.getSettingEntriesList(ICSettingEntry.INCLUDE_PATH));
        String linkString = "/" + getGslProjectName();
        if (containsLink(linkString, includes)) {
            return;
        }
        includes.add(new CIncludePathEntry(linkString, ICSettingEntry.VALUE_WORKSPACE_PATH));
        setting.setSettingEntries(ICSettingEntry.INCLUDE_PATH, includes);
    }

    private static boolean containsLink(String link, List<ICLanguageSettingEntry> includes) {
        for (ICLanguageSettingEntry entry : includes) {
            if (entry instanceof CIncludePathEntry) {
                CIncludePathEntry includePath = (CIncludePathEntry) entry;
                boolean isworkspace = includePath.isValueWorkspacePath();
                boolean isGSLProject = includePath.getValue().equals(link);
                if (isworkspace && isGSLProject) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String getGslProjectName() {
        IPreferenceStore prefs = CharWarsPlugin.getDefault().getPreferenceStore();
        return prefs.getString(PreferenceKeys.GSL_PROJECT_NAME);
    }
}
