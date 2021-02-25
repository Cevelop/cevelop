package com.cevelop.constificator.tests.checker.functionparameters.builtin;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.constificator.tests.checker.functionparameters.builtin.array.SuiteArrays;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off

    // Pass-by-value
    C49_PBV_ModifyingBinaryExpression.class,
    C49_PBV_ModifyingUnaryExpression.class,
    C49_PBV_PassedToFunctionViaNonConstReference.class,
    C49_PBV_BindNonConstReference.class,
    C49_PBV_PassedToFunctionViaPointerToNonConst.class,
    C49_PBV_AssignedToPointerToNonConst.class,
    C49_PBV_PassedToFunctionViaReferenceToPointerToNonConst.class,
    C49_PBV_BindReferenceToPointerToNonConst.class,

    // Pass-by-reference
    C49A_PBR_ReturnedAsReferenceToNonConst.class,

    // Clashing declarations
    C50_Clash_PassByPointer.class,

    // Arrays
    SuiteArrays.class,

//@formatter:on
})
public class SuiteBuiltin {

}
