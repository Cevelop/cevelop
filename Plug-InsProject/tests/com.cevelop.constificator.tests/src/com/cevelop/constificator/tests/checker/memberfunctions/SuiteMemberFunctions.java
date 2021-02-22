package com.cevelop.constificator.tests.checker.memberfunctions;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
	C54_ModifiesNonStaticDataMember.class,
	C55_CallsNonStaticNonConstMemberFunction.class,
	C56_ConstOverloadExists.class,
	C57_ReturnsNonConstReferenceToInstance.class,
//@formatter:on
})
public class SuiteMemberFunctions {

}
