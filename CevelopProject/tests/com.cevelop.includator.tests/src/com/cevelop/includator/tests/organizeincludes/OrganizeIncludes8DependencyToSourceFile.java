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

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.organizeincludes.OrganizeIncludesAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class OrganizeIncludes8DependencyToSourceFile extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        MultiStatus status = runAlgorithmsAsAction(new OrganizeIncludesAlgorithm(), AlgorithmScope.FILE_SCOPE);
        String expected =
                        "Status WARNING: com.cevelop.includator code=0 Includator Static Include Analysis Status null children=[Status WARNING: com.cevelop.includator code=0 Prevented adding include directive to source file foo.cpp which is referenced by foo in main.cpp[2:3,2:6]. null]";
        assertStatus(1, IStatus.WARNING, expected, status);

        List<Suggestion<?>> suggestions = IncludatorPlugin.getSuggestionStore().getAllSuggestions();
        Assert.assertEquals((Object) 0, (Object) suggestions.size());
    }
}
