package com.cevelop.constificator.tests.checker.localvariables.classtype;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
	C09_PassedToFunctionViaReferenceToNonConst.class,
	C10_BoundViaReferenceToNonConst.class,
	C11_AddressPassedToFunctionViaPointerToNonConst.class,
	C12_AddressAssignedToPointerToNonConst.class,
	C13_PassedToFunctionViaReferenceToPointerToNonConst.class,
	C14_AddressBoundViaReferenceToPointerToNonConst.class,
	C15_CallToNonConstMemberFunction.class,
	C47_CallToNonConstMemberFunctionViaPointer.class,
//@formatter:on
})
public class SuiteClassType {

}
