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

import org.eclipse.core.runtime.MultiStatus;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.optimizer.organizeincludes.OrganizeIncludesAlgorithm;
import com.cevelop.includator.tests.base.IncludatorTest;


public class OrganizeIncludes16IncludeSubstitution2 extends IncludatorTest {

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("fakeStdLib", "fakeStdLib2");
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        MultiStatus status = runAlgorithmsAsAction(new OrganizeIncludesAlgorithm(), AlgorithmScope.FILE_SCOPE);
        assertStatusOk(status);

        List<Suggestion<?>> suggestions = IncludatorPlugin.getSuggestionStore().getAllSuggestions();

        Assert.assertEquals((Object) 2, (Object) suggestions.size());
        Suggestion<?> includeMathHSuggestion = suggestions.get(0);
        Suggestion<?> includeVectorSuggestion = suggestions.get(1);
        assertSuggestion(includeMathHSuggestion, "main.cpp", "Missing '#include <math.h>'.", 0, 0);
        assertSuggestion(includeVectorSuggestion, "main.cpp", "Missing '#include <vector>'.", 0, 0);

        List<DeclarationReference> refs = getActiveFileDeclarationReferences();
        Assert.assertEquals((Object) 6, (Object) refs.size());
        DeclarationReference sqrtRef = refs.get(1);
        DeclarationReference vectorRef = refs.get(3);
        assertDeclRefDependencyExternalTargetFile("fakeStdLib2/bits/mathcalls.h", sqrtRef.getRequiredDependencies());
        assertDeclRefDependencyExternalTargetFile("fakeStdLib/bits/fakeVector.h", vectorRef.getRequiredDependencies());
    }
}
