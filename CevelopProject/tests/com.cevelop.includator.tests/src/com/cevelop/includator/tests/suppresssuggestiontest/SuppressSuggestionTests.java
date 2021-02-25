/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.suppresssuggestiontest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
      //@formatter:off
			SuppressSuggestionTest1IgnoredDuplicateInclude.class,
			SuppressSuggestionTest2OneIgnoredAndOneSuggestedDuplicateInclude.class,
			SuppressSuggestionTest3IgnoredUnusedInclude.class,
			SuppressSuggestionTest4OneIgnoredAndOneSuggestedUnusedInclude.class,
			SuppressSuggestionTest5IgnoredCoveredInclude.class,
			SuppressSuggestionTest6OneIgnoredAndOneSuggestedCoveredInclude.class,
			SuppressSuggestionTest7IgnoredMissingInclude.class,
			SuppressSuggestionTest8OneIgnoredAndOneSuggestedMissingInclude.class,
			SuppressSuggestionTest9IgnoredSubDirInclude.class,
			SuppressSuggestionTest10Suppress1Of3Suggestions.class,
			SuppressSuggestionTest11ExternalIncludePathSuppression.class,
			SuppressSuggestionTest12ReferencedProjectSuppression.class,
			//@formatter:on
})
public class SuppressSuggestionTests {}
