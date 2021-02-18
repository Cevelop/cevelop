/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.directincludedeclarationstests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
      //@formatter:off
			DirectIncludeDecl1OneIncludeToAdd.class,
			DirectIncludeDecl2OneIncludeToAddServeralRefs.class,
			DirectIncludeDecl4NoIncludeToAdd.class,
			DirectIncludeDecl5ManyIncludesToAdd.class,
			DirectIncludeDecl6MatchingSourceHeaderFileName.class,
			DirectIncludeDecl7QuickFix.class,
			DirectIncludeDecl8Action.class,
			DirectIncludeDecl9RetainSystemInclude.class,
			DirectIncludeDecl10ForwardDeclarationForIncludedClass.class,
			DirectIncludeDecl11SourceInSubDir.class,
			DirectIncludeDecl12AlsoSuggestMissingRefs.class,
			DirectIncludeDecl13SeveralSuggestionsInProject.class,
			DirectIncludeDecl14RefToNotIncludedHeader.class,
			//@formatter:on
})
public class DirectIncludeDeclarationTests {}
