package com.cevelop.includator.tests.markertests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.includator.tests.markertests.cleanup.CleanupTests;


@RunWith(Suite.class)
@SuiteClasses({
      //@formatter:off
			MarkerTest10MarkerCountTest2.class,
			MarkerTest12PositionChangeTestUserTypingSaved.class,
			MarkerTest13PositionChangeTestUserTypingDiscarded.class,
			MarkerTest14TwoMarkersAtBeginning.class,
			MarkerTest15DirectApplySeveralInDifferentFile.class,
			MarkerTest16DirectlyApplyFirstAndAddMarkerSecond.class,
			MarkerTest1OneMarker.class,
			MarkerTest2TwoMarkerManyIncludes.class,
			MarkerTest3ZeroMarkers.class,
			MarkerTest4RemoveMarkerAction.class,
			MarkerTest5UndoMarkerTest1.class,
			MarkerTest6PartOfMarkersDeleted.class,
			MarkerTest7UndoMarkerTest2WithUserTyping.class,
			MarkerTest9MarkerCountTest1.class,
			CleanupTests.class
			//@formatter:on
})
public class MarkerTests {}
