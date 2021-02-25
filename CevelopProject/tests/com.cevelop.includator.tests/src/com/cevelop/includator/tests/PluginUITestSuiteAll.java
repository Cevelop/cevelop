/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.includator.tests.commenthandling.CommentHandlingTests;
import com.cevelop.includator.tests.datastructure.DataStructureTests;
import com.cevelop.includator.tests.declarationstoretests.DeclarationStoreTests;
import com.cevelop.includator.tests.declreftests.DeclRefTests;
import com.cevelop.includator.tests.directincludedeclarationstests.DirectIncludeDeclarationTests;
import com.cevelop.includator.tests.findunusedfiles.FindUnusedFileTests;
import com.cevelop.includator.tests.findunusedtests.RemoveUnusedTests;
import com.cevelop.includator.tests.firstlastelementpathstoretests.FirstLastElementPath1DuplicateTest;
import com.cevelop.includator.tests.helpertests.HelperTests;
import com.cevelop.includator.tests.includepathtests.IncludePathTests;
import com.cevelop.includator.tests.includestoretests.IncludeStoreTests;
import com.cevelop.includator.tests.includetofwd.IncludeToFwdTests;
import com.cevelop.includator.tests.indexadaptiontests.IndexAdaptionTests;
import com.cevelop.includator.tests.markertests.MarkerTests;
import com.cevelop.includator.tests.multipledecltests.MultipleDeclTests;
import com.cevelop.includator.tests.organizeincludes.OrganizeIncludesTests;
import com.cevelop.includator.tests.referencedProjectsTest.ReferencedProjectsTests;
import com.cevelop.includator.tests.returnstatustests.ReturnStatusTests;
import com.cevelop.includator.tests.startingpointtests.StartingPointTests;
import com.cevelop.includator.tests.suggestionstoretests.SuggestionStoreTests;
import com.cevelop.includator.tests.suppresssuggestiontest.SuppressSuggestionTests;
import com.cevelop.includator.tests.syntaxerrortests.SyntaxErrorTests;
import com.cevelop.includator.tests.testframeworktest.TestFrameworkTests;


@Ignore
@RunWith(Suite.class)
@SuiteClasses({
      //@formatter:off
			HelperTests.class,
			TestFrameworkTests.class,
			StartingPointTests.class,
//			CoverageTests.class,
			DataStructureTests.class,
			DeclarationStoreTests.class,
			DeclRefTests.class,
			DirectIncludeDeclarationTests.class,
			FindUnusedFileTests.class,
			FirstLastElementPath1DuplicateTest.class,
			IncludeStoreTests.class,
			IncludePathTests.class,
			IncludeToFwdTests.class,
			IndexAdaptionTests.class,
			MarkerTests.class,
			MultipleDeclTests.class,
			OrganizeIncludesTests.class,
			RemoveUnusedTests.class,
			ReturnStatusTests.class,
			SuggestionStoreTests.class,
			SyntaxErrorTests.class,
			ReferencedProjectsTests.class,
			CommentHandlingTests.class,
			SuppressSuggestionTests.class,
			//@formatter:on
})
public class PluginUITestSuiteAll {}
