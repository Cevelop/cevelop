/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.helpertests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
      //@formatter:off
			HelperTest6FindHeaderFileIStatus1.class,
			HelperTest7FileHelper.class,
			HelperTest7FileHelper2.class,
			HelperTest7FileHelper3.class,
			HelperTest10ExternalTuProviderTest.class,
			HelperTest11ExternalEditorFileTest.class,
			HelperTest12MainFinderTest1.class,
			HelperTest13IncludePathSubDirProperty.class,
			HelperTest14IncludePathSubDirProperty2.class,
			HelperTest15IndexToASTNameHelper1.class,
			HelperTest16IndexToASTNameHelper2.class,
			HelperTest17IndexToASTNameHelper3.class,
			HelperTest18IndexToASTNameHelper4.class,
			HelperTest19UnsavedTuProviderTest.class,
			HelperTest1FindHeaderFile1.class,
			HelperTest20InsertFwdOffsetProviderTest.class,
			HelperTest21IsFwd.class,
			HelperTest22InsertFwdOffsetProviderTestNextNodeTest.class,
			HelperTest23DontSubstituteTopLevelHeader.class,
			HelperTest24IncludeInsertOffsetCheck.class,
			HelperTest2FindHeaderFile2.class,
			HelperTest3FindHeaderFile3.class,
			HelperTest4FindHeaderFile4.class,
			HelperTest5FindHeaderFile5.class,
			HelperTest8QuickFixProcessor.class,
			HelperTest9LocationTuProviderTest.class,
			//@formatter:on
})
public class HelperTests {}
