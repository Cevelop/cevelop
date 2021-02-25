package com.cevelop.codeanalysator.core.tests.unittests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.SafeRunnable;
import org.junit.Before;
import org.junit.Test;

import com.cevelop.codeanalysator.core.guideline.GuidelinePreferencesImpl;
import com.cevelop.codeanalysator.core.guideline.IGuidelinePreferences;
import com.cevelop.codeanalysator.core.tests.unittests.stubs.ProjectStub;
import com.cevelop.codeanalysator.core.tests.unittests.stubs.guideline.GuidelineStub;
import com.cevelop.codeanalysator.core.tests.unittests.stubs.preferences.PropertyAndPreferenceHelperStub;


public class GuidelinePreferencesImplTest {

   private PropertyAndPreferenceHelperStub propAndPrefHelper;
   private GuidelinePreferencesImpl        guidelinePreferences;

   private GuidelineStub firstGuideline;
   private GuidelineStub secondGuideline;
   private GuidelineStub thirdGuideline;
   private GuidelineStub fourthGuideline;

   private ProjectStub usingWorkspaceSettingsProject;
   private ProjectStub secondThirdGuidelineEnabledProject;
   private ProjectStub firstSecondGuidelineEnabledProject;

   @Before
   public void setUp() {
      propAndPrefHelper = new PropertyAndPreferenceHelperStub();
      guidelinePreferences = new GuidelinePreferencesImpl();
      guidelinePreferences.startListening(propAndPrefHelper);

      firstGuideline = new GuidelineStub("testing.firstGuideline");
      secondGuideline = new GuidelineStub("testing.secondGuideline");
      thirdGuideline = new GuidelineStub("testing.thirdGuideline");
      fourthGuideline = new GuidelineStub("testing.fourthGuideline");

      usingWorkspaceSettingsProject = new ProjectStub();
      secondThirdGuidelineEnabledProject = new ProjectStub();
      firstSecondGuidelineEnabledProject = new ProjectStub();

      setPreferenceListForWorkspace(firstGuideline.getId(), thirdGuideline.getId(), fourthGuideline.getId());
      setPreferenceListForProject(secondThirdGuidelineEnabledProject, secondGuideline.getId(), thirdGuideline.getId());
      setPreferenceListForProject(firstSecondGuidelineEnabledProject, firstGuideline.getId(), secondGuideline.getId());

      propAndPrefHelper.setProjectSpecificPreferences(secondThirdGuidelineEnabledProject, true);
      propAndPrefHelper.setProjectSpecificPreferences(firstSecondGuidelineEnabledProject, true);
   }

   @Test(expected = IllegalArgumentException.class)
   public void startListeningThrowsOnNullPreferenceStore() {
      guidelinePreferences.startListening(null);
   }

   @Test(expected = IllegalStateException.class)
   public void startListeningThrowsOnRepeatedStart() {
      guidelinePreferences.startListening(propAndPrefHelper);
   }

   @Test(expected = IllegalArgumentException.class)
   public void isGuidelineEnabledForWorkspaceThrowsOnNullGuideline() {
      guidelinePreferences.isGuidelineEnabledForWorkspace(null);
   }

   @Test(expected = IllegalArgumentException.class)
   public void isGuidelineEnabledForProjectThrowsOnNullGuideline() {
      guidelinePreferences.isGuidelineEnabledForProject(null, null);
   }

   @Test(expected = IllegalArgumentException.class)
   public void isGuidelineEnabledForProjectThrowsOnNullProject() {
      guidelinePreferences.isGuidelineEnabledForProject(firstGuideline, null);
   }

   @Test
   public void isGuidelineEnabledForProjectFalseBeforeListening() {
      GuidelinePreferencesImpl notListeningGuidelinePreferences = new GuidelinePreferencesImpl();
      assertFalse(notListeningGuidelinePreferences.isGuidelineEnabledForProject(firstGuideline, usingWorkspaceSettingsProject));
      assertFalse(notListeningGuidelinePreferences.isGuidelineEnabledForProject(firstGuideline, secondThirdGuidelineEnabledProject));
      assertFalse(notListeningGuidelinePreferences.isGuidelineEnabledForProject(firstGuideline, firstSecondGuidelineEnabledProject));
   }

   @Test
   public void guidelinesEnabledForWorkspaceIfWorkspacePreferenceListContainsGuidelineId() {
      assertTrue(guidelinePreferences.isGuidelineEnabledForWorkspace(firstGuideline));
      assertFalse(guidelinePreferences.isGuidelineEnabledForWorkspace(secondGuideline));
      assertTrue(guidelinePreferences.isGuidelineEnabledForWorkspace(thirdGuideline));
      assertTrue(guidelinePreferences.isGuidelineEnabledForWorkspace(fourthGuideline));
   }

   @Test
   public void guidelinesEnabledForProjectIfWorkspacePreferenceListContainsGuidelineIdAndUsingWorkspaceSettings() {
      assertTrue(guidelinePreferences.isGuidelineEnabledForProject(firstGuideline, usingWorkspaceSettingsProject));
      assertFalse(guidelinePreferences.isGuidelineEnabledForProject(secondGuideline, usingWorkspaceSettingsProject));
      assertTrue(guidelinePreferences.isGuidelineEnabledForProject(thirdGuideline, usingWorkspaceSettingsProject));
      assertTrue(guidelinePreferences.isGuidelineEnabledForProject(fourthGuideline, usingWorkspaceSettingsProject));
   }

   @Test
   public void guidelinesEnabledForProjectIfProjectPreferenceListContainsGuidelineIdAndUsingProjectSettings() {
      assertFalse(guidelinePreferences.isGuidelineEnabledForProject(firstGuideline, secondThirdGuidelineEnabledProject));
      assertTrue(guidelinePreferences.isGuidelineEnabledForProject(secondGuideline, secondThirdGuidelineEnabledProject));
      assertTrue(guidelinePreferences.isGuidelineEnabledForProject(thirdGuideline, secondThirdGuidelineEnabledProject));

      assertTrue(guidelinePreferences.isGuidelineEnabledForProject(firstGuideline, firstSecondGuidelineEnabledProject));
      assertTrue(guidelinePreferences.isGuidelineEnabledForProject(secondGuideline, firstSecondGuidelineEnabledProject));
      assertFalse(guidelinePreferences.isGuidelineEnabledForProject(thirdGuideline, firstSecondGuidelineEnabledProject));
   }

   @Test(expected = IllegalArgumentException.class)
   public void getGuidelinePriorityForWorkspaceThrowsOnNullGuideline() {
      guidelinePreferences.getGuidelinePriorityForWorkspace(null);
   }

   @Test(expected = IllegalArgumentException.class)
   public void getGuidelinePriorityForProjectThrowsOnNullGuideline() {
      guidelinePreferences.getGuidelinePriorityForProject(null, null);
   }

   @Test(expected = IllegalArgumentException.class)
   public void getGuidelinePriorityForProjectThrowsOnNullProject() {
      guidelinePreferences.getGuidelinePriorityForProject(null, null);
   }

   @Test
   public void getGuidelinePriorityForProjectMinusOneBeforeListening() {
      GuidelinePreferencesImpl notListeningGuidelinePreferences = new GuidelinePreferencesImpl();
      assertEquals(-1, notListeningGuidelinePreferences.getGuidelinePriorityForProject(firstGuideline, usingWorkspaceSettingsProject));
      assertEquals(-1, notListeningGuidelinePreferences.getGuidelinePriorityForProject(firstGuideline, secondThirdGuidelineEnabledProject));
      assertEquals(-1, notListeningGuidelinePreferences.getGuidelinePriorityForProject(firstGuideline, firstSecondGuidelineEnabledProject));
   }

   @Test
   public void guidelinePrioritiesForWorkspaceEqualToWorkspacePreferenceListIndexOfGuidelineId() {
      assertEquals(0, guidelinePreferences.getGuidelinePriorityForWorkspace(firstGuideline));
      assertEquals(-1, guidelinePreferences.getGuidelinePriorityForWorkspace(secondGuideline));
      assertEquals(1, guidelinePreferences.getGuidelinePriorityForWorkspace(thirdGuideline));
      assertEquals(2, guidelinePreferences.getGuidelinePriorityForWorkspace(fourthGuideline));
   }

   @Test
   public void guidelinePrioritiesForProjectEqualToWorkspacePreferenceListIndexOfGuidelineIdWhenUsingWorkspaceSettings() {
      assertEquals(0, guidelinePreferences.getGuidelinePriorityForProject(firstGuideline, usingWorkspaceSettingsProject));
      assertEquals(-1, guidelinePreferences.getGuidelinePriorityForProject(secondGuideline, usingWorkspaceSettingsProject));
      assertEquals(1, guidelinePreferences.getGuidelinePriorityForProject(thirdGuideline, usingWorkspaceSettingsProject));
      assertEquals(2, guidelinePreferences.getGuidelinePriorityForProject(fourthGuideline, usingWorkspaceSettingsProject));
   }

   @Test
   public void guidelinePrioritiesForProjectEqualToProjectPreferenceListIndexOfGuidelineIdWhenUsingProjectSettings() {
      assertEquals(-1, guidelinePreferences.getGuidelinePriorityForProject(firstGuideline, secondThirdGuidelineEnabledProject));
      assertEquals(0, guidelinePreferences.getGuidelinePriorityForProject(secondGuideline, secondThirdGuidelineEnabledProject));
      assertEquals(1, guidelinePreferences.getGuidelinePriorityForProject(thirdGuideline, secondThirdGuidelineEnabledProject));
      assertEquals(-1, guidelinePreferences.getGuidelinePriorityForProject(fourthGuideline, secondThirdGuidelineEnabledProject));

      assertEquals(0, guidelinePreferences.getGuidelinePriorityForProject(firstGuideline, firstSecondGuidelineEnabledProject));
      assertEquals(1, guidelinePreferences.getGuidelinePriorityForProject(secondGuideline, firstSecondGuidelineEnabledProject));
      assertEquals(-1, guidelinePreferences.getGuidelinePriorityForProject(thirdGuideline, firstSecondGuidelineEnabledProject));
      assertEquals(-1, guidelinePreferences.getGuidelinePriorityForProject(fourthGuideline, firstSecondGuidelineEnabledProject));
   }

   @Test
   public void guidelineWorkspacePreferencesInitializedAfterStartingListening() {
      PropertyAndPreferenceHelperStub localPropAndPrefHelper = new PropertyAndPreferenceHelperStub();
      GuidelinePreferencesImpl localGuidelinePreferences = new GuidelinePreferencesImpl();
      IPreferenceStore localWorkspacePreferenceStore = localPropAndPrefHelper.getWorkspacePreferences();
      localWorkspacePreferenceStore.setValue(IGuidelinePreferences.GUIDELINE_PREFERENCE_NAME, firstGuideline.getId());

      localGuidelinePreferences.startListening(localPropAndPrefHelper);

      assertTrue(localGuidelinePreferences.isGuidelineEnabledForWorkspace(firstGuideline));
      assertEquals(0, localGuidelinePreferences.getGuidelinePriorityForWorkspace(firstGuideline));
   }

   @Test
   public void guidelineWorkspacePreferencesNotUpdatedAfterStoppingListening() {
      guidelinePreferences.stopListening();

      setPreferenceListForWorkspace(secondGuideline.getId());

      assertFalse(guidelinePreferences.isGuidelineEnabledForWorkspace(secondGuideline));
      assertEquals(-1, guidelinePreferences.getGuidelinePriorityForWorkspace(secondGuideline));
   }

   @Test
   public void guidelineProjectPreferencesUpdatedOnWorkspacePreferenceListChange() {
      assertFalse(guidelinePreferences.isGuidelineEnabledForProject(secondGuideline, usingWorkspaceSettingsProject));
      assertEquals(-1, guidelinePreferences.getGuidelinePriorityForProject(secondGuideline, usingWorkspaceSettingsProject));

      setPreferenceListForWorkspace(secondGuideline.getId());

      assertTrue(guidelinePreferences.isGuidelineEnabledForProject(secondGuideline, usingWorkspaceSettingsProject));
      assertEquals(0, guidelinePreferences.getGuidelinePriorityForProject(secondGuideline, usingWorkspaceSettingsProject));
   }

   @Test
   public void guidelineProjectPreferencesUpdatedOnProjectPreferenceListChange() {
      assertFalse(guidelinePreferences.isGuidelineEnabledForProject(firstGuideline, secondThirdGuidelineEnabledProject));
      assertEquals(-1, guidelinePreferences.getGuidelinePriorityForProject(firstGuideline, secondThirdGuidelineEnabledProject));

      setPreferenceListForProject(secondThirdGuidelineEnabledProject, firstGuideline.getId());

      assertTrue(guidelinePreferences.isGuidelineEnabledForProject(firstGuideline, secondThirdGuidelineEnabledProject));
      assertEquals(0, guidelinePreferences.getGuidelinePriorityForProject(firstGuideline, secondThirdGuidelineEnabledProject));
   }

   @Test
   public void guidelineProjectPreferencesUpdatedOnUseProjectSettingsChange() {
      assertFalse(guidelinePreferences.isGuidelineEnabledForProject(firstGuideline, secondThirdGuidelineEnabledProject));
      assertEquals(-1, guidelinePreferences.getGuidelinePriorityForProject(firstGuideline, secondThirdGuidelineEnabledProject));

      propAndPrefHelper.setProjectSpecificPreferences(secondThirdGuidelineEnabledProject, false);

      assertTrue(guidelinePreferences.isGuidelineEnabledForProject(firstGuideline, secondThirdGuidelineEnabledProject));
      assertEquals(0, guidelinePreferences.getGuidelinePriorityForProject(firstGuideline, secondThirdGuidelineEnabledProject));
   }

   @Test
   public void guidelineProjectPreferencesNotUpdatedAfterStoppingListening() {
      assertFalse(guidelinePreferences.isGuidelineEnabledForProject(firstGuideline, secondThirdGuidelineEnabledProject));
      assertEquals(-1, guidelinePreferences.getGuidelinePriorityForProject(firstGuideline, secondThirdGuidelineEnabledProject));
      guidelinePreferences.stopListening();

      setPreferenceListForProject(secondThirdGuidelineEnabledProject, firstGuideline.getId());

      assertFalse(guidelinePreferences.isGuidelineEnabledForProject(firstGuideline, secondThirdGuidelineEnabledProject));
      assertEquals(-1, guidelinePreferences.getGuidelinePriorityForProject(firstGuideline, secondThirdGuidelineEnabledProject));
   }

   @Test
   public void guidelinePreferencesChangeListenerFiredOnWorkspacePreferenceListChange() {
      AtomicBoolean hasFired = new AtomicBoolean();
      guidelinePreferences.addPreferencesChangeListener(() -> hasFired.set(true));

      setPreferenceListForWorkspace(firstGuideline.getId());

      assertTrue(hasFired.get());
   }

   @Test
   public void guidelinePreferencesChangeListenerFiredOnProjectPreferenceListChange() {
      guidelinePreferences.isGuidelineEnabledForProject(firstGuideline, secondThirdGuidelineEnabledProject);
      AtomicBoolean hasFired = new AtomicBoolean();
      guidelinePreferences.addPreferencesChangeListener(() -> hasFired.set(true));

      setPreferenceListForProject(secondThirdGuidelineEnabledProject, firstGuideline.getId());

      assertTrue(hasFired.get());
   }

   @Test
   public void guidelinePreferencesChangeListenerFiredOnProjectPreferenceListChangeAfterStartingToListen() {
      PropertyAndPreferenceHelperStub localPropAndPrefHelper = new PropertyAndPreferenceHelperStub();
      GuidelinePreferencesImpl localGuidelinePreferences = new GuidelinePreferencesImpl();
      IPreferenceStore localProjectPreferenceStore = localPropAndPrefHelper.getProjectPreferences(secondThirdGuidelineEnabledProject);
      localProjectPreferenceStore.setValue(IGuidelinePreferences.GUIDELINE_PREFERENCE_NAME, firstGuideline.getId());

      localGuidelinePreferences.isGuidelineEnabledForProject(firstGuideline, secondThirdGuidelineEnabledProject);
      AtomicBoolean hasFired = new AtomicBoolean();
      localGuidelinePreferences.addPreferencesChangeListener(() -> hasFired.set(true));

      localGuidelinePreferences.startListening(localPropAndPrefHelper);

      assertTrue(hasFired.get());
   }

   @Test
   public void guidelinePreferencesChangeEventFiredInSafeRunnable() {
      guidelinePreferences.addPreferencesChangeListener(() -> {
         throw new RuntimeException();
      });
      AtomicBoolean hasSecondFired = new AtomicBoolean();
      guidelinePreferences.addPreferencesChangeListener(() -> hasSecondFired.set(true));
      SafeRunnable.setIgnoreErrors(true);
      SafeRunnable.setRunner(code -> {
         try {
            code.run();
         } catch (Exception e) {}
      });

      setPreferenceListForWorkspace(firstGuideline.getId());

      SafeRunnable.setIgnoreErrors(false);
      SafeRunnable.setRunner(null);
      assertTrue(hasSecondFired.get());
   }

   private void setPreferenceListForWorkspace(String... guidelineIds) {
      String preferenceList = String.join(IGuidelinePreferences.GUIDELINE_PREFERENCE_ID_SEPARATOR, guidelineIds);
      propAndPrefHelper.setValue(IGuidelinePreferences.GUIDELINE_PREFERENCE_NAME, preferenceList, null);
   }

   private void setPreferenceListForProject(IProject project, String... guidelineIds) {
      String preferenceList = String.join(IGuidelinePreferences.GUIDELINE_PREFERENCE_ID_SEPARATOR, guidelineIds);
      propAndPrefHelper.setProjectValue(IGuidelinePreferences.GUIDELINE_PREFERENCE_NAME, preferenceList, project);
   }
}
