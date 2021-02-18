/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.findunusedtests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.includator.tests.findunusedtests.complexstructure.RemoveUnusedComplexTests;


@RunWith(Suite.class)
@SuiteClasses({
      //@formatter:off
			RemoveUnused10MacroDefine.class,
			RemoveUnused11DoubleReference.class,
			RemoveUnused12SemanticalError.class,
			RemoveUnused13WholeProjectAction.class,
			RemoveUnused14SeveralSameDefinitions.class,
			RemoveUnused15SeveralSameDefinitionsUnreachable.class,
			RemoveUnused16DuplicateInclude.class,
			RemoveUnused17InactiveInclude.class,
			RemoveUnused1RemoveNone.class,
			RemoveUnused2RemoveSingleInclude.class,
			RemoveUnused3PathTest.class,
			RemoveUnused4SeveralPathsOptimalSelection.class,
			RemoveUnused5Complex.class,
			RemoveUnused6Complex2DeclRefs.class,
			RemoveUnused7ManyPathsOneInclude.class,
			RemoveUnused8Complex3.class,
			RemoveUnused9WholeProject.class,
			RemoveUnused18UseOfBaseClassMember1.class,
			RemoveUnused19UseOfBaseClassMember2.class,
			RemoveUnused20CoveredIncludesDisabled.class,
			RemoveUnused21ExcludedClassReference.class,
			RemoveUnused22OffsetTest.class,
			RemoveUnused23AutoDeleteZeroSizeMarker.class,
			RemoveUnused24NameCorrelationSimple.class,
			RemoveUnused25NameCorrelationComplex.class,
			RemoveUnused26CorrelatingFileNamesNoSuggestionTest.class,
		RemoveUnused27MissingPathTest.class,
			RemoveUnusedComplexTests.class,
			//@formatter:on
})
public class RemoveUnusedTests {}
