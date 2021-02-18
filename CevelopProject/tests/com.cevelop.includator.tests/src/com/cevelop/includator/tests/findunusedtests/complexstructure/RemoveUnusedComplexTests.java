/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.findunusedtests.complexstructure;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
			ComplexRemoveUnused1UnrequiredDiamond.class,
			ComplexRemoveUnused2FullHouseInclude.class,
			ComplexRemoveUnused3ThreeTimesThree.class,
			ComplexRemoveUnused4OneCoveredByFourOther.class,
			ComplexRemoveUnused5AdditionalCollectingHeader.class
			//@formatter:on
})
public class RemoveUnusedComplexTests {}
