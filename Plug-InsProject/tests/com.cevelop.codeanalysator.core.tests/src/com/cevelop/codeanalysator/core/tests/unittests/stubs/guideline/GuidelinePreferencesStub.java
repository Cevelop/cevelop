package com.cevelop.codeanalysator.core.tests.unittests.stubs.guideline;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;

import com.cevelop.codeanalysator.core.guideline.IGuideline;
import com.cevelop.codeanalysator.core.guideline.IGuidelinePreferences;


public class GuidelinePreferencesStub implements IGuidelinePreferences {

   private Set<String>                         disabledGuidelineIdsForWorkspace  = new HashSet<>();
   private Map<IProject, Set<String>>          disabledGuidelineIdsByProject     = new HashMap<>();
   private Map<String, Integer>                priorityByGuidelineIdForWorkspace = new HashMap<>();
   private Map<IProject, Map<String, Integer>> priorityByGuidelineIdByProject    = new HashMap<>();
   private IPreferencesChangeListener          changeListener;

   @Override
   public boolean isGuidelineEnabledForWorkspace(IGuideline guideline) {
      return false;
   }

   @Override
   public boolean isGuidelineEnabledForProject(IGuideline guideline, IProject project) {
      return !disabledGuidelineIdsByProject.getOrDefault(project, disabledGuidelineIdsForWorkspace).contains(guideline.getId());
   }

   @Override
   public int getGuidelinePriorityForWorkspace(IGuideline guideline) {
      return -1;
   }

   @Override
   public int getGuidelinePriorityForProject(IGuideline guideline, IProject project) {
      return priorityByGuidelineIdByProject.getOrDefault(project, priorityByGuidelineIdForWorkspace).getOrDefault(guideline.getId(), -1);
   }

   public void disableGuideline(IGuideline guideline) {
      disabledGuidelineIdsForWorkspace.add(guideline.getId());

      firePreferencesChangeEvent();
   }

   public void disableGuidelineForProject(IGuideline guideline, IProject project) {
      Set<String> disabledGuidelinesForProject = disabledGuidelineIdsByProject.computeIfAbsent(project, p -> new HashSet<>());
      disabledGuidelinesForProject.add(guideline.getId());
   }

   public void setWorkspacePriority(GuidelineStub guideline, int priority) {
      priorityByGuidelineIdForWorkspace.put(guideline.getId(), priority);

      firePreferencesChangeEvent();
   }

   public void setProjectPriority(IProject project, GuidelineStub guideline, int priority) {
      Map<String, Integer> priorityByGuidelineIdForProject = priorityByGuidelineIdByProject.computeIfAbsent(project, p -> new HashMap<>());
      priorityByGuidelineIdForProject.put(guideline.getId(), priority);
   }

   private void firePreferencesChangeEvent() {
      if (changeListener != null) {
         changeListener.guidelinePreferencesChange();
      }
   }

   @Override
   public void addPreferencesChangeListener(IPreferencesChangeListener listener) {
      changeListener = listener;
   }
}
