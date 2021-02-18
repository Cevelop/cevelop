package com.cevelop.codeanalysator.core.tests.unittests.stubs.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;

import ch.hsr.ifs.iltis.core.core.preferences.PropertyAndPreferenceHelper;


public class PropertyAndPreferenceHelperStub extends PropertyAndPreferenceHelper {

   private final IPreferenceStore workspacePreferenceStore;
   private final Map<IProject, IPreferenceStore> preferenceStoreByProject;

   public PropertyAndPreferenceHelperStub() {
      workspacePreferenceStore = new PreferenceStore();
      preferenceStoreByProject = new HashMap<>();
   }

   @Override
   public IPreferenceStore getWorkspacePreferences() {
      return workspacePreferenceStore;
   }

   @Override
   public IPreferenceStore getProjectPreferences(IProject project) {
      return preferenceStoreByProject.computeIfAbsent(project, p -> new PreferenceStore());
   }

   @Override
   public String getPreferenceIdQualifier() {
      return "testing.preference";
   }
}
