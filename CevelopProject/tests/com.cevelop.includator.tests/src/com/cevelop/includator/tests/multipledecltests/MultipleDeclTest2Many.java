/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.multipledecltests;

import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.dependency.FullIncludePath;
import com.cevelop.includator.tests.base.IncludatorTest;


public class MultipleDeclTest2Many extends IncludatorTest {

    private String[] includedFiles;
    private String   requiredFile;
    private String[] requiredIncludePaths;
    private int      allDependenciesCount;
    private int      expectedRefDepCount;

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) expectedRefDepCount, (Object) references.size());

        int classBRefIndex = (expectedRefDepCount == 5) ? 2 : 1;
        DeclarationReference classBRef = references.get(classBRefIndex);
        assertDeclarationReference("B", "A.cpp", classBRef);

        List<DeclarationReferenceDependency> allDependencies = classBRef.getAllDependencies();
        Assert.assertEquals((Object) allDependenciesCount, (Object) allDependencies.size());
        Assert.assertEquals((Object) includedFiles.length, (Object) classBRef.getIncludedDependencies().size());
        for (int i = 0; i < includedFiles.length; i++) {
            assertDeclRefDependencyTargetFile(includedFiles[i], classBRef.getIncludedDependencies(), includedFiles.length, i);
        }
        List<DeclarationReferenceDependency> requiredDependencies = classBRef.getRequiredDependencies();
        Assert.assertEquals((Object) 1, (Object) requiredDependencies.size());
        assertDeclRefDependencyTargetFile(requiredFile, requiredDependencies);
        List<FullIncludePath> includePaths = requiredDependencies.get(0).getIncludePaths();
        Assert.assertEquals((Object) requiredIncludePaths.length, (Object) includePaths.size());
        for (int i = 0; i < requiredIncludePaths.length; i++) {
            assertIncludePath(requiredIncludePaths[i], includePaths.get(i));
        }
    }

    @Override
    protected void configureTest(Properties properties) {
        String includedFilesStr = properties.getProperty("includedFiles");
        includedFiles = (includedFilesStr.equals("")) ? new String[0] : includedFilesStr.split(",");
        requiredFile = properties.getProperty("requiredFile");
        String requiredIncludePathsStr = properties.getProperty("requiredIncludePaths");
        requiredIncludePaths = (requiredIncludePathsStr.equals("")) ? new String[0] : requiredIncludePathsStr.split(",");
        allDependenciesCount = Integer.parseInt(properties.getProperty("dependenciesCount"));
        expectedRefDepCount = Integer.parseInt(properties.getProperty("expectedRefDepCount"));
        super.configureTest(properties);
    }
}
