package com.cevelop.conanator.tests.parser;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.conanator.tests.parser.namedsectionparser.NamedSectionsTest;
import com.cevelop.conanator.tests.parser.namedsectionparser.NamelessSectionTest;


@RunWith(Suite.class)
@SuiteClasses({ NamedSectionsTest.class, NamelessSectionTest.class, })
public class NamedSectionParserTests {

}
