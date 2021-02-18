package com.cevelop.charwars.tests.quickfixes;

import java.util.EnumSet;

import org.junit.Test;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingQuickfixTest;
import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.comparison.ASTComparison.ComparisonArg;


public abstract class AbstractQuickFixTest extends CDTTestingQuickfixTest {

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("commonIncludes");
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        runQuickfixAndAssertAllEqual(getFirstMarker());
    }

    @Override
    protected EnumSet<ComparisonArg> makeComparisonArguments() {
        return EnumSet.of(ComparisonArg.COMPARE_INCLUDE_DIRECTIVES, ComparisonArg.IGNORE_INCLUDE_ORDER);
    }

}
