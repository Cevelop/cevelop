/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.organizeincludes;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
			OrganizeIncludes1Simple.class,
			OrganizeIncludes2FailingExternalTarget.class,
			OrganizeIncludes3ExternalTarget.class,
			OrganizeIncludes4IncludeSubstitution1.class,
			OrganizeIncludes5SubSourceFolder.class,
			OrganizeIncludes6InProjectIncludeFolderPath.class,
			OrganizeIncludes7NestedClasses.class,
			OrganizeIncludes8DependencyToSourceFile.class,
			OrganizeIncludes9RequireString.class,
			OrganizeIncludes10RequirementThroughMemberAccess.class,
			OrganizeIncludes11MissingSuggestionSymbolExclusion.class,
			OrganizeIncludes12MultipleSuggestionIncludingEachOther.class,
			OrganizeIncludes13MultipleSuggestionIncludingEachOtherRecursively.class,
			OrganizeIncludes14DeclInCppDefInHeader.class,
			OrganizeIncludes15SeveralUnincludedMacroCandidates.class,
			OrganizeIncludes16IncludeSubstitution2.class,
//			OrganizeIncludes17BoostFunctionHpp.class, //Note: CDT since 9.3.0 parses the declaration statement as expression statement (interpreting the <> of the template ID as less and greater than operators)
			OrganizeIncludes18SuggestionRecursivelyIncludingItself.class,
			OrganizeIncludes19CoverageDataTest.class,
			OrganizeIncludes20AddAfterRemovedInclude.class,
			OrganizeIncludes21ReferencedProjectDoubleQuoteInclude.class,
			OrganizeIncludes22AddAfterCopyrightComment.class,
			//@formatter:on
})
public class OrganizeIncludesTests {}
