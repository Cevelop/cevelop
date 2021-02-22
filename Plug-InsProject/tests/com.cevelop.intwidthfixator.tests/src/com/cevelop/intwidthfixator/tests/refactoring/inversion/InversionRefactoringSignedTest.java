package com.cevelop.intwidthfixator.tests.refactoring.inversion;

import org.eclipse.ltk.core.refactoring.Refactoring;

import com.cevelop.intwidthfixator.refactorings.inversion.InversionRefactoring;


public class InversionRefactoringSignedTest extends AbstractInversionRefactoringTest {

    @Override
    protected Refactoring createRefactoring() {

        return new InversionRefactoring(getPrimaryCElementFromCurrentProject().get(), info.also(c -> {
            c.refactor_int8 = true;
            c.refactor_int16 = true;
            c.refactor_int32 = true;
            c.refactor_int64 = true;
            c.refactor_unsigned = false;
            c.refactor_signed = true;
        }));
    }
}
