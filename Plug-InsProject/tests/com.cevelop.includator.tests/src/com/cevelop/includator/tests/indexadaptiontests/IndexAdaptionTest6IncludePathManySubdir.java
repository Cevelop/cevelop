/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.indexadaptiontests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.tests.base.IncludatorTest;


public class IndexAdaptionTest6IncludePathManySubdir extends IncludatorTest {

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("IndexAdaptionTest6IncludePathrManySubdir");
        addIncludePathsSubDir("sub1");
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 11, (Object) references.size());

        List<DeclarationReferenceDependency> qwerDependencies = references.get(2).getRequiredDependencies();
        assertDeclRefDependencyExternalTargetFile("IndexAdaptionTest6IncludePathrManySubdir/QWER.h", qwerDependencies);

        List<DeclarationReferenceDependency> availableSubDependencies = references.get(6).getRequiredDependencies();
        assertDeclRefDependencyExternalTargetFile("IndexAdaptionTest6IncludePathrManySubdir/sub1/Sub.h", availableSubDependencies);

        List<DeclarationReferenceDependency> unavialableSubDependencies = references.get(9).getRequiredDependencies();
        Assert.assertEquals((Object) 0, (Object) unavialableSubDependencies.size());
        Assert.assertTrue(references.get(9).hadProblemsWhileResolving());
    }
}
