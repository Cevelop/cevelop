/*******************************************************************************
 * Copyright (c) 2011, 2014 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.declreftests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DeclRefTest15ImplicitFunction extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        // 9 is expected because there should be no additional refs like __builtin_va*
        Assert.assertEquals((Object) 9, (Object) references.size());

        DeclarationReference vaStartDeclRef = references.get(6);
        assertDeclRefName("va_start", vaStartDeclRef);

        Assert.assertEquals((Object) 1, (Object) vaStartDeclRef.getRequiredDependencies().size());

        assertDeclRefDependencyTargetFile("fake_stdarg.h", vaStartDeclRef.getRequiredDependencies());
        DeclarationReference vaArgDeclRef = references.get(7);
        assertDeclRefName("va_arg", vaArgDeclRef);
        assertDeclRefDependencyTargetFile("fake_stdarg.h", vaArgDeclRef.getRequiredDependencies());
        DeclarationReference vaEndDeclRef = references.get(8);
        assertDeclRefName("va_end", vaEndDeclRef);
        assertDeclRefDependencyTargetFile("fake_stdarg.h", vaEndDeclRef.getRequiredDependencies());
        assertStatus(); // expecting no warning is correct because unresolved refs like __builtin_va* should not generate warnings.
    }
}
