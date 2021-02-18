/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure.multipledefinition;

import java.util.List;

import org.eclipse.core.runtime.Path;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class MultipleDefinitionTest12PreferShortestRelativePath extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 2, (Object) references.size());
        DeclarationReference fooRef = references.get(1);
        assertDeclRefDependencyTargetFile(new Path("a/a1/a2/Foo.h").toOSString(), fooRef.getRequiredDependencies());
        assertStatusOk();
    }
}
