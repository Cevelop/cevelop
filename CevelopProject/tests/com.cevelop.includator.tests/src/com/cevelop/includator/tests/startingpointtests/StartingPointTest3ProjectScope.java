/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.startingpointtests;

import java.util.List;

import org.eclipse.core.runtime.MultiStatus;
import org.junit.Test;

import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.tests.mocks.StartingPointExtracterFakeAlg;


public class StartingPointTest3ProjectScope extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        AlgorithmScope scope = AlgorithmScope.PROJECT_SCOPE;
        StartingPointExtracterFakeAlg startingPointExtracterFakeAlg = new StartingPointExtracterFakeAlg(scope);
        MultiStatus status = runAlgorithmsAsAction(startingPointExtracterFakeAlg, scope);
        assertStatusOk(status);
        List<IncludatorFile> affectedFiles = startingPointExtracterFakeAlg.getStartingPoint().getAffectedFiles();
        assertFileNames(affectedFiles, makeOSPath("folder1/a.cpp"), makeOSPath("folder1/b.cpp"), makeOSPath("folder1/subfolder/c.cpp"), makeOSPath(
                "folder2/a.cpp"), makeOSPath("folder2/b.cpp"), makeOSPath("main.cpp"));
    }
}
