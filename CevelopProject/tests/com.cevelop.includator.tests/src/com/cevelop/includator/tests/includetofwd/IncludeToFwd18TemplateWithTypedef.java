/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.includetofwd;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.tests.base.IncludatorTest;
import com.cevelop.includator.ui.actions.ReplaceIncludesWithFwdAction;


public class IncludeToFwd18TemplateWithTypedef extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {

        List<DeclarationReference> decls = getActiveFileDeclarationReferences();
        Assert.assertEquals((Object) 4, (Object) decls.size());
        DeclarationReference intContentTypeRef = decls.get(1);
        assertDeclarationReference("IntContent", "main.cpp", intContentTypeRef);
        Assert.assertEquals(false, intContentTypeRef.isForwardDeclarationEnough());

        runAction(new ReplaceIncludesWithFwdAction(), AlgorithmScope.FILE_SCOPE);
        List<Suggestion<?>> suggestions = IncludatorPlugin.getSuggestionStore().getAllSuggestions();

        Assert.assertEquals((Object) 0, (Object) suggestions.size());

    }
}
