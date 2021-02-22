/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.suggestionstoretests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
      //@formatter:off
			SuggestionStoreTest1StoreEmpty.class,
			SuggestionStoreTest2OneStoreElement.class,
			SuggestionStoreTest3ManyElements.class,
			SuggestionStoreTest4WrongPositions.class,
			SuggestionStoreTest5QuickFixPositions.class,
			SuggestionStoreTest6QuickFixPositionsManyElements.class,
			SuggestionStoreTest7QuickFixPositionsDocumentChanged.class,
			SuggestionStoreTest8OverlappigFixes.class
			//@formatter:on
})
public class SuggestionStoreTests {}
