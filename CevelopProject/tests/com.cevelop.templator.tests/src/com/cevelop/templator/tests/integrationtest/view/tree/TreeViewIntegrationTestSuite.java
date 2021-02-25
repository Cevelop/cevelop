package com.cevelop.templator.tests.integrationtest.view.tree;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
	RemoveConnectionThatCausesColumnChangeTest.class,
	RemoveConnectionThatCausesRowChangeTest.class,
	AddAndRemoveConnectionsTest.class,
	AddAndRemoveConnectionsAndPortalsTest.class,
	RemoveEntryTest.class,
	RemoveEntryWithPortalsTest.class,
	RemoveSubEntriesTest.class,
	RemoveSubEntriesWithPortalsTest.class
//@formatter:on
})

public class TreeViewIntegrationTestSuite {}
