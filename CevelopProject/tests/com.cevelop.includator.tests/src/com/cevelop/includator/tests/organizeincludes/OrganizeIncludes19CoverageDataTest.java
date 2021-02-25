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

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.optimizer.organizeincludes.OtherFileCoverageData;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.tests.mocks.OrganizeIncludesCoverageDataMockAlg;


public class OrganizeIncludes19CoverageDataTest extends IncludatorTest {

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("OrganizeIncludes19CoverageDataTest");
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        OrganizeIncludesCoverageDataMockAlg alg = new OrganizeIncludesCoverageDataMockAlg(getActiveIncludatorFile());
        List<DeclarationReference> activeFileDeclarationReferences = getActiveFileDeclarationReferences();
        Assert.assertEquals((Object) 3, (Object) activeFileDeclarationReferences.size());

        alg.handleUnincludedDeclRefDependency(getRequiredDependency(activeFileDeclarationReferences.get(1)));
        alg.handleUnincludedDeclRefDependency(getRequiredDependency(activeFileDeclarationReferences.get(2)));
        alg.initCoverageInfo();

        List<OtherFileCoverageData> coverageDatas = alg.getIncludeCoverageDatas();
        Assert.assertEquals((Object) 2, (Object) coverageDatas.size());
        OtherFileCoverageData bothHCoverageData = coverageDatas.get(1);
        Assert.assertEquals("Missing '#include <both.h>'. covers 1 other includes.", bothHCoverageData.toString());
        Assert.assertEquals("Missing '#include <foo.h>'. covers 0 other includes.", bothHCoverageData.getCoveredSuggestions().get(0).toString());
    }
}
