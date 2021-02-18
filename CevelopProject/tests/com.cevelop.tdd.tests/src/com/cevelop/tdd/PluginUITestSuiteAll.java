/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.tdd.ui.tests.checkers.CheckerTestSuiteAll;
import com.cevelop.tdd.ui.tests.extraction.ExtractionTestSuiteAll;
import com.cevelop.tdd.ui.tests.quickfixes.QuickFixTestSuiteAll;


@RunWith(Suite.class)
@SuiteClasses({ QuickFixTestSuiteAll.class, //
                ExtractionTestSuiteAll.class, //
                CheckerTestSuiteAll.class, //
})
public class PluginUITestSuiteAll {}
