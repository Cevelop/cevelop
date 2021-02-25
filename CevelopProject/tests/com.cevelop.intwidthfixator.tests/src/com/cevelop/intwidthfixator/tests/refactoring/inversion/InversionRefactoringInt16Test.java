package com.cevelop.intwidthfixator.tests.refactoring.inversion;

import org.eclipse.ltk.core.refactoring.Refactoring;

import com.cevelop.intwidthfixator.refactorings.inversion.InversionRefactoring;


public class InversionRefactoringInt16Test extends AbstractInversionRefactoringTest {

    @Override
    protected Refactoring createRefactoring() {

        return new InversionRefactoring(getPrimaryCElementFromCurrentProject().get(), info.also(c -> {
            c.refactor_int8 = false;
            c.refactor_int16 = true;
            c.refactor_int32 = false;
            c.refactor_int64 = false;
            c.refactor_unsigned = true;
            c.refactor_signed = true;
        }));
    }
}
