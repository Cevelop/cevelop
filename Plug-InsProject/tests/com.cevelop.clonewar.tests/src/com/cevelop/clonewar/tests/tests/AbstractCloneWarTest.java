package com.cevelop.clonewar.tests.tests;

import java.util.Properties;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeId;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringContext;
import org.junit.Test;

import com.cevelop.clonewar.refactorings.CloneWarRefactoring;
import com.cevelop.clonewar.transformation.action.TransformAction;
import com.cevelop.clonewar.transformation.configuration.TransformConfiguration;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingRefactoringTest;


public class AbstractCloneWarTest extends CDTTestingRefactoringTest {

    private final String EXPECT_PROBLEM_KEY = "expectProblem";

    private final String          NESTED_ONLY_KEY       = "nestedOnly";
    private final String          EXPECTED_FINAL_ERRORS = "expectedFinalErrors";
    protected CloneWarRefactoring refactoring;
    private boolean               expectProblem;

    private boolean nestedOnly;

    @Override
    protected Refactoring createRefactoring() {
        refactoring = new CloneWarRefactoring(getPrimaryCElementFromCurrentProject().get(), getSelectionOfPrimaryTestFile());
        return refactoring;
    }

    @Test
    public void runTest() throws Throwable {
        openPrimaryTestFileInEditor();
        if (expectProblem) {
            runRefactoringAndAssertFailure();
        } else {
            runRefactoringAndAssertSuccess();
        }
    }

    @Override
    protected void configureTest(Properties properties) {
        expectProblem = Boolean.parseBoolean(properties.getProperty(EXPECT_PROBLEM_KEY, "false"));
        nestedOnly = Boolean.parseBoolean(properties.getProperty(NESTED_ONLY_KEY, "false"));
        expectedFinalErrors = Integer.parseInt(properties.getProperty(EXPECTED_FINAL_ERRORS, "0"));
        super.configureTest(properties);
    }

    @Override
    protected void simulateUserInput(RefactoringContext context) {
        TransformConfiguration config = refactoring.getTransformation().getConfig();
        if (nestedOnly) {
            for (TransformAction action : config.getAllActions()) {
                if (!(action.getNode().getParent() instanceof ICPPASTTypeId)) action.setPerform(false);
            }
        }
        super.simulateUserInput(context);
    }

}
