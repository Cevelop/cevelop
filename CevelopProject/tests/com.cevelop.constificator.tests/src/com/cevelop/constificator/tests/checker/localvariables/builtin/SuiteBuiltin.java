package com.cevelop.constificator.tests.checker.localvariables.builtin;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
	C01_ModifiedByBinaryExpression.class,
	C02_ModifiedByUnaryExpression.class,
	C03_PassedToFunctionViaReferenceToNonConst.class,
	C04_BoundViaReferenceToNonConst.class,
	C05_AddressPassedToFunctionViaPointerToNonConst.class,
	C06_AddressAssignedToPointerToNonConst.class,
	C07_PassedToFunctionViaReferenceToPointerToNonConst.class,
	C08_AddressBoundViaReferenceToPointerToNonConst.class,
	C16_PointerModifiedByBinaryExpression.class,
	C17_PointerModifiedByUnaryExpression.class,
	C18_PointerPassedToFunctionTakingReferenceToNonConstPointer.class,
	C19_PointerBoundViaReferenceToNonConstPointer.class,
	C20_AddressOfPointerPassedToFunctionViaPointerToNonConstPointer.class,
	C21_AddressOfPointerAssignedToPointerToNonConstPointer.class,
	C22_AddressOfPointerPassedToFunctionViaReferenceToPointerToNonConstPointer.class,
	C23_AddressOfPointerBoundViaReferenceToPointerToNonConstPointer.class,
	C24_PointeeModifiedViaBinaryExpression.class,
	C25_PointeeModifiedViaUnaryExpression.class,
	C26_PointeePassedToFunctionViaReferenceToNonConst.class,
	C27_PointeeBoundViaReferenceToNonConst.class,
	C28_PointeePassedToFunctionViaPointerToNonConst.class,
	C29_PointerAssignedToPointerToNonConst.class,
	C30_PointerPassedToFunctionViaReferenceToPointerToNonConst.class,
	C31_PointerBoundViaReferenceToPointerToNonConst.class,
	C32_ConstQualificationConversion.class,
//@formatter:on
})
public class SuiteBuiltin {

}