package com.cevelop.constificator.tests.util.semantic.type;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.constificator.tests.util.semantic.type.inheritancegraph.InheritanceGraphTests;


@RunWith(Suite.class)
@SuiteClasses({
//@formatter:off
    InheritanceGraphTests.class,
    AreSameTypeIgnoringConstTest.class,
//@formatter:on
})
public class TypeTests {

}
