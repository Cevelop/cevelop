/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.helpertests;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.stores.IncludeSubstitutionStore;
import com.cevelop.includator.tests.base.IncludatorTest;


public class HelperTest23DontSubstituteTopLevelHeader extends IncludatorTest {

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("HelperTest23DontSubstituteTopLevelHeader");
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        IncludeSubstitutionStore substitutionStore = IncludatorPlugin.getIncludeSubstitutionStore();
        DeclarationReferenceDependency iosfwdDependency = getRequiredDependency(getActiveFileDeclarationReferences().get(1));
        IPath isofwdFilePath = iosfwdDependency.getDeclaration().getFile().getIFile().getRawLocation();
        Assert.assertEquals(externalTestResourcesHolder.makeProjectAbsolutePath("HelperTest23DontSubstituteTopLevelHeader/iosfwd"), isofwdFilePath);
        IPath substitutedPath = new Path(substitutionStore.getSubstitutionFilePath(isofwdFilePath.toOSString(), getActiveProject()));
        Assert.assertEquals(externalTestResourcesHolder.makeProjectAbsolutePath("HelperTest23DontSubstituteTopLevelHeader/iosfwd"), substitutedPath);
    }
}
