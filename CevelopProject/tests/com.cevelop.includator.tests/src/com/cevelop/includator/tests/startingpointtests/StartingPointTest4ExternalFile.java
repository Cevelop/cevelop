/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.startingpointtests;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.organizeincludes.OrganizeIncludesAlgorithm;
import com.cevelop.includator.tests.IncludatorTestsPlugin;
import com.cevelop.includator.tests.base.IncludatorTest;


public class StartingPointTest4ExternalFile extends IncludatorTest {

    @Override
    protected void initAdditionalIncludes() throws Exception {
        URL fileURL = IncludatorTestsPlugin.getDefault().getBundle().getEntry("/externalTestResource/StartingPointTest4ExternalFile");
        URL absoluteFileURL = FileLocator.resolve(fileURL);
        currentProjectHolder.stageAbsoluteExternalIncludePaths(new Path(absoluteFileURL.getPath()));
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        externalTestResourcesHolder.getProject().close(new NullProgressMonitor());

        URL fileURL = IncludatorTestsPlugin.getDefault().getBundle().getEntry("/externalTestResource/StartingPointTest4ExternalFile/active.h");
        URL absoluteFileURL = FileLocator.resolve(fileURL);
        Path externalPath = new Path(absoluteFileURL.getPath());
        openExternalFileInEditor(externalPath);

        MultiStatus status = runAlgorithmsAsAction(new OrganizeIncludesAlgorithm(), AlgorithmScope.EDITOR_SCOPE);
        String markerMsg = "Failed to add marker to project-external file '" + externalPath + "'.";
        String externalFileMsg =
                               "Suggestion 'Missing '#include \"A.h\"'.' isn't part of an Eclipse project. Adding markers and applying suggestion-solutions is therefore not supported.";
        assertStatus(status, markerMsg, externalFileMsg);
    }
}
