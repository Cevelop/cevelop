/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure;

import java.util.List;

import org.eclipse.core.runtime.MultiStatus;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.findunusedincludes.FindUnusedIncludesAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureFileTest5AsmFileStartingPointProjAlg extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        Assert.assertNull(getActiveIncludatorFile()); // active file is .asm file
        MultiStatus status = runAlgorithmsAsAction(wrapAsProjectAlgorithm(new FindUnusedIncludesAlgorithm()), AlgorithmScope.FILE_SCOPE);
        assertStatusOk(status);

        List<Suggestion<?>> suggestions = IncludatorPlugin.getSuggestionStore().getAllSuggestions();
        Assert.assertEquals((Object) 1, (Object) suggestions.size());
        assertSuggestion(suggestions.get(0), "main.cpp", "The include statement '#include \"A.h\"' is unneeded. No reference requires include.", 0,
                14);
    }
}
