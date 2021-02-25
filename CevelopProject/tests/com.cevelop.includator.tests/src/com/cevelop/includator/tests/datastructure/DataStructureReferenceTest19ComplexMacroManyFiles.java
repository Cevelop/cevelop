/*******************************************************************************
 * Copyright (c) 2010, 2014 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureReferenceTest19ComplexMacroManyFiles extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 8, (Object) references.size());
        assertDeclRefs(references);
        assertDeclarations(references);
    }

    private void assertDeclRefs(List<DeclarationReference> references) throws IOException {
        assertDeclarationReference("AB", "main.cpp", 123, references.get(3));
        assertDeclarationReference("CONCAT_CALL", "main.cpp", 84, references.get(4));
        assertDeclarationReference("DIRECT_CALL", "main.cpp", 123, references.get(5));
        assertDeclarationReference("X", "main.cpp", 114, references.get(6));
        Assert.assertEquals((Object) 0, (Object) references.get(6).getRequiredDependencies().size());
        assertDeclarationReference("X2", "main.cpp", 149, references.get(7));
    }

    private void assertDeclarations(List<DeclarationReference> references) {
        Assert.assertEquals((Object) 1, (Object) references.get(3).getRequiredDependencies().size());
        assertDeclRefDependencyTargetFile("AB.h", references.get(3).getRequiredDependencies());
        assertDeclRefDependencyTargetFile("CONCAT_CALL.h", references.get(4).getRequiredDependencies());
        assertDeclRefDependencyTargetFile("DIRECT_CALL.h", references.get(5).getRequiredDependencies());
        assertDeclRefDependencyTargetFile("main.cpp", references.get(7).getRequiredDependencies());
    }

}
