package com.cevelop.constificator.tests.checker.functionparameters.builtin.array;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
    //@formatter:off

    // Pass-by-value
    C49_PBV_ModifyingUnaryExpression.class,
    C49_PBV_PassToFunctionTakingArrayOfNonConst.class,
    C49_PBV_PassToFunctionTakingPointerToNonConst.class,

    // Pass-by-reference
    C49A_PBR_ModifyingUnaryExpression.class,
    C49A_PBR_PassedToFunctionTakingArrayOfNonConst.class,
    C49A_PBR_PassedToFunctionTakingPointerToNonConst.class,
    C49A_PBR_PassedToFunctionTakingReferenceToArrayOfNonConst.class,

    // Pass-by-pointer
    C49B_PBP_ModifyingUnaryExpression.class,
    C49B_PBP_NonConstMemberAccess.class,
    C49B_PBP_PassedToFunctionTakingPointerToArrayOfNonConst.class,
    C49B_PBP_PassedToFunctionTakingReferenceToPointerToArrayOfNonConst.class,

    //@formatter:on
})
public class SuiteArrays {

}
