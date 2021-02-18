package com.cevelop.constificator.tests.checker.functionparameters.classtype;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
  //@formatter:off

      // Objects passed by value
      C49_PBV_PassedToFunctionViaNonConstReference.class,
      C49_PBV_BindNonConstReference.class,
      C49_PBV_PassedToFunctionViaPointerToNonConst.class,
      C49_PBV_AssignedToPointerToNonConst.class,
      C49_PBV_PassedToFunctionViaReferenceToPointerToNonConst.class,

  //@formatter:on
})
public class SuiteClassType {

}
