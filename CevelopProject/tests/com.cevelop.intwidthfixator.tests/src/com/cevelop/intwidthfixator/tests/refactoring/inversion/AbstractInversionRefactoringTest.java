package com.cevelop.intwidthfixator.tests.refactoring.inversion;

import java.util.EnumSet;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Test;

import com.cevelop.intwidthfixator.helpers.IdHelper;
import com.cevelop.intwidthfixator.preferences.PropAndPrefHelper;
import com.cevelop.intwidthfixator.refactorings.inversion.InversionRefactoringInfo;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingRefactoringTestWithPreferences;
import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.comparison.ASTComparison.ComparisonArg;


public abstract class AbstractInversionRefactoringTest extends CDTTestingRefactoringTestWithPreferences {

    protected InversionRefactoringInfo info = new InversionRefactoringInfo();

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("include/refactorings");
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        info.also(c -> {
            c.fileName = getPrimaryCElementFromCurrentProject().get().getLocationURI().toString();
            c.setSelection(getSelectionOfPrimaryTestFile());
        });
        runRefactoringAndAssertSuccess();
    }

    @Override
    protected EnumSet<ComparisonArg> makeComparisonArguments() {
        return EnumSet.of(ComparisonArg.COMPARE_INCLUDE_DIRECTIVES, ComparisonArg.PRINT_WHOLE_ASTS_ON_FAIL);
    }

    @Override
    public IPreferenceStore initPrefs() {
        return new PropAndPrefHelper().getWorkspacePreferences();
    }

    @Override
    public Class<?> getPreferenceConstants() {
        return IdHelper.class;
    }

}
