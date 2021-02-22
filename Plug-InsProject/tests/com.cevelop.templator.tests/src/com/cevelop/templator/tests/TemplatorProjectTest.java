package com.cevelop.templator.tests;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.cdt.core.dom.ast.ASTNodeFactoryFactory;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateArgument;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.parser.ParserLanguage;
import org.junit.Assert;

import com.cevelop.templator.plugin.asttools.ASTAnalyzer;
import com.cevelop.templator.plugin.asttools.templatearguments.TemplateArgumentMap;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.plugin.util.EclipseUtil;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.base.CDTTestingUITest;


public class TemplatorProjectTest extends CDTTestingUITest {

    protected ASTAnalyzer           analyzer;
    protected ICPPNodeFactory       factory;
    protected List<IASTDeclaration> definitions;
    protected int                   expectedDefinitionIndex;

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("filesOutsideWorkspace");
        super.initAdditionalIncludes();
    }

    /**
     * Using setUp() and not @Before because the existing CDTTestingTest uses this and the correct order would not be
     * guaranteed.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        initAnalyzer();
        initTopLevelDefinitions();
        factory = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();
    }

    @Override
    protected void configureTest(Properties properties) {
        expectedDefinitionIndex = Integer.parseInt(properties.getProperty("definition", "0"));
        super.configureTest(properties);
    }

    private void initAnalyzer() {
        IASTTranslationUnit ast = TestHelper.parse(getCurrentDocument(getPrimaryIFileFromCurrentProject()).get(), ParserLanguage.CPP);
        IIndex index = EclipseUtil.getIndexFromProject(getCurrentCProject());
        analyzer = new ASTAnalyzer(index, ast);
    }

    protected void initTopLevelDefinitions() {
        IASTDeclaration[] allDeclarations = analyzer.getAst().getDeclarations();
        definitions = new ArrayList<>(allDeclarations.length);

        // just writing it out is easier than a visitor
        for (IASTDeclaration topLevelDeclaration : allDeclarations) {
            if (topLevelDeclaration instanceof IASTFunctionDefinition || topLevelDeclaration instanceof IASTSimpleDeclaration) {
                definitions.add(topLevelDeclaration);
            }
            if (topLevelDeclaration instanceof ICPPASTTemplateDeclaration) {
                ICPPASTTemplateDeclaration templateDeclaration = (ICPPASTTemplateDeclaration) topLevelDeclaration;
                definitions.add(templateDeclaration);
            }
        }
    }

    protected IASTFunctionDefinition getMain() throws TemplatorException {
        for (IASTDeclaration declaration : definitions) {
            if (declaration instanceof IASTFunctionDefinition) {
                IASTFunctionDefinition definition = (IASTFunctionDefinition) declaration;
                if (getName(definition).toString().equals("main") && definition.getDeclSpecifier().toString().equals("int")) {
                    return definition;
                }
            }
        }
        throw new TemplatorException("no main function found in " + getNameOfPrimaryTestFile());
    }

    //	protected AbstractResolvedNameInfo resolveCall(IASTDeclaration definition, int index,
    //			AbstractResolvedNameInfo parent) throws TemplatorException {
    //		List<UnresolvedNameInfo> subCalls = getSubStatements(definition);
    //		return AbstractResolvedNameInfo.create(subCalls.get(index), parent, analyzer);
    //	}

    //	protected AbstractResolvedNameInfo resolveCallInMain(int index) throws TemplatorException {
    //		List<UnresolvedNameInfo> subCalls = getSubStatements(getMain());
    //		return AbstractResolvedNameInfo.create(subCalls.get(index), null, analyzer);
    //	}

    private IASTName getName(IASTFunctionDefinition definition) {
        return definition.getDeclarator().getName();
    }

    protected void assertEquals(TemplateArgumentMap expected, TemplateArgumentMap actual) {
        int expectedSize = expected.size();
        int actualSize = actual.size();
        if (expectedSize != actualSize) {
            fail("TemplateArgumentMap size was expected to be " + expectedSize + " but was " + actualSize);
        }

        for (int parameterId = 0; parameterId < expectedSize; parameterId++) {
            ICPPTemplateArgument expectedArgument = expected.getArgument(parameterId);
            ICPPTemplateArgument actualArgument = actual.getArgument(parameterId);

            String expectedString = expectedArgument.toString();
            String actualString = actualArgument.toString();

            IType expectedType = expectedArgument.getTypeValue();
            IType actualType = actualArgument.getTypeValue();
            if (expectedType instanceof IBinding) {
                expectedString = ((IBinding) expectedType).getName();
            }
            if (actualType instanceof IBinding) {
                actualString = ((IBinding) actualType).getName();
            }

            String errorMessage = "Argument #" + parameterId + " was expected to be " + expectedArgument + " but was " + actualArgument + "\n";
            Assert.assertEquals(errorMessage, expectedString, actualString);
        }
    }

    protected <T> void assertEqualsToString(T[] expected, T[] actual) {
        if (expected.length != actual.length) {
            fail("Array length was expected to be " + expected.length + " but was " + actual.length);
        }
        for (int i = 0; i < actual.length; i++) {
            T actualElement = actual[i];
            T expectedElement = expected[i];
            Assert.assertEquals(expectedElement.toString(), actualElement.toString());
        }
    }

    protected void assertEquals(TemplateArgumentMap actual, ICPPTemplateArgument... expectedArguments) {
        TemplateArgumentMap expected = new TemplateArgumentMap();

        for (int i = 0; i < expectedArguments.length; i++) {
            ICPPTemplateArgument argument = expectedArguments[i];
            expected.put(i, argument);
        }

        assertEquals(expected, actual);
    }

    protected void statementMatchesExpectedDefinition(IASTDeclaration expectedDefinition, IASTDeclaration definition) {
        Assert.assertEquals(expectedDefinition, definition);
    }

    public void statementMatchesExpectedDefinition(IASTDeclaration definition) {
        statementMatchesExpectedDefinition(definitions.get(expectedDefinitionIndex), definition);
    }
}
