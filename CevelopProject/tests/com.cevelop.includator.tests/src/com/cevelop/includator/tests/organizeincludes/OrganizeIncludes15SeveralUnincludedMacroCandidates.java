/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.organizeincludes;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.organizeincludes.OrganizeIncludesAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.tests.helpers.StatusHelper;


public class OrganizeIncludes15SeveralUnincludedMacroCandidates extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<Suggestion<?>> suggestions = runAlgorithm(new OrganizeIncludesAlgorithm());

        Assert.assertEquals((Object) 1, (Object) suggestions.size());
        assertSuggestion(suggestions.get(0), "main.cpp", "Missing '#include \"foo2.h\"'.", 0, 0);
        MultiStatus status = StatusHelper.collectStatus();
        Assert.assertEquals((Object) 3, (Object) status.getChildren().length);
        String msg =
                   "There are several potential macro definitions for FOO in main.cpp[2:11,2:14]. Assuming foo2.h[1:9,1:12] as correct. Candidates were:";
        assertStatus(IStatus.WARNING, msg, status.getChildren()[0]);
        assertStatus(IStatus.WARNING, "foo2.h[1:9,1:12]", status.getChildren()[1]);
        assertStatus(IStatus.WARNING, "foo1.h[1:9,1:12]", status.getChildren()[2]);
    }
}
