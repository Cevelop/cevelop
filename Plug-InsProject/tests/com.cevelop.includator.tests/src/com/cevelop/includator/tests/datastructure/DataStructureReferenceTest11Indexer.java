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

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureReferenceTest11Indexer extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 18, (Object) references.size());

        DeclarationReference left = references.get(15);
        assertDeclRefName("s", left);
        DeclarationReferenceDependency leftDependency = getRequiredDependency(left);
        assertFileLocation(315, leftDependency.getDeclaration().getFileLocation());

        DeclarationReference opperator = references.get(6);
        assertDeclRefName("operator <<", opperator);
        List<DeclarationReferenceDependency> opperatorDependencies = opperator.getRequiredDependencies();
        // This should actually return one dependency. see bug //
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=297457
        // => Bug fixed
        Assert.assertEquals((Object) 1, (Object) opperatorDependencies.size());

        DeclarationReference right = references.get(11);
        assertDeclRefName("nothingOp", right);
        DeclarationReferenceDependency rightDependency = getRequiredDependency(right);
        assertFileLocation(218, rightDependency.getDeclaration().getFileLocation());
    }
}
