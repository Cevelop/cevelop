package com.cevelop.ctylechecker.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cevelop.ctylechecker.tests.unit.common.CtyleProblemTest;
import com.cevelop.ctylechecker.tests.unit.common.FileUtilTest;
import com.cevelop.ctylechecker.tests.unit.common.ProblemFileLoaderTest;
import com.cevelop.ctylechecker.tests.unit.common.ProblemFileParserTest;
import com.cevelop.ctylechecker.tests.unit.domain.AddPrefixResolutionTest;
import com.cevelop.ctylechecker.tests.unit.domain.AddSuffixResolutionTest;
import com.cevelop.ctylechecker.tests.unit.domain.CaseTransformerResolutionTest;
import com.cevelop.ctylechecker.tests.unit.domain.ConceptTest;
import com.cevelop.ctylechecker.tests.unit.domain.ConceptsTest;
import com.cevelop.ctylechecker.tests.unit.domain.ExpressionGroupTest;
import com.cevelop.ctylechecker.tests.unit.domain.ExpressionTest;
import com.cevelop.ctylechecker.tests.unit.domain.GroupingTest;
import com.cevelop.ctylechecker.tests.unit.domain.QualifiersTest;
import com.cevelop.ctylechecker.tests.unit.domain.RenameTransformerTest;
import com.cevelop.ctylechecker.tests.unit.domain.ReservedNamesCheckerTest;
import com.cevelop.ctylechecker.tests.unit.domain.ResolutionFactoryTest;
import com.cevelop.ctylechecker.tests.unit.domain.RuleTest;
import com.cevelop.ctylechecker.tests.unit.domain.StyleguideTest;
import com.cevelop.ctylechecker.tests.unit.domain.StyleguideUtilTest;
import com.cevelop.ctylechecker.tests.unit.service.ConceptServiceTest;
import com.cevelop.ctylechecker.tests.unit.service.ConfigurationServiceTest;
import com.cevelop.ctylechecker.tests.unit.service.ExpressionServiceTest;
import com.cevelop.ctylechecker.tests.unit.service.GroupingServiceTest;
import com.cevelop.ctylechecker.tests.unit.service.RuleServiceTest;
import com.cevelop.ctylechecker.tests.unit.service.StyleguideServiceTest;


@RunWith(Suite.class)
@SuiteClasses({ //
                CtyleProblemTest.class, //
                ProblemFileParserTest.class, //
                ProblemFileLoaderTest.class, //
                ConceptTest.class, //
                ExpressionGroupTest.class, //
                GroupingTest.class, //
                ExpressionTest.class, //
                RuleTest.class, //
                StyleguideTest.class, //
                StyleguideUtilTest.class, //
                RenameTransformerTest.class, //
                FileUtilTest.class, //
                AddPrefixResolutionTest.class, //
                AddSuffixResolutionTest.class, //
                CaseTransformerResolutionTest.class, //
                ReservedNamesCheckerTest.class, //
                ResolutionFactoryTest.class, //
                QualifiersTest.class, //
                ConceptsTest.class, //
                ConfigurationServiceTest.class, //
                RuleServiceTest.class, //
                GroupingServiceTest.class, //
                StyleguideServiceTest.class, //
                ExpressionServiceTest.class, //
                ConceptServiceTest.class//
})
public class PluginTestSuiteAll {

}
