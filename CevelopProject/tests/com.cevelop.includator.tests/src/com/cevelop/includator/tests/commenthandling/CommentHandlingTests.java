/*******************************************************************************
 * Copyright (c) 2011 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.commenthandling;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
      //@formatter:off
			CommentHandlingTest1LineCommentSameLine.class,
			CommentHandlingTest2LeadingCommentTest.class,
			CommentHandlingTest3SameLineAndTrailing.class,
			CommentHandlingTest4CopyrightCommentRemoveTest.class,
			CommentHandlingTest5CopyrightCommentRemoveTest2.class,
			CommentHandlingTest6CopyrightCommentRemoveTest3.class,
			//@formatter:on
})
public class CommentHandlingTests {}
