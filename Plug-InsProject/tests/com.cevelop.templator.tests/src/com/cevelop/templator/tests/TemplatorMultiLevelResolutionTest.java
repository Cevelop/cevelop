package com.cevelop.templator.tests;

import java.util.Properties;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateArgument;

import com.cevelop.templator.plugin.asttools.data.AbstractResolvedNameInfo;
import com.cevelop.templator.plugin.asttools.data.ResolvedName;
import com.cevelop.templator.plugin.logger.TemplatorException;


public class TemplatorMultiLevelResolutionTest extends TemplatorProjectTest {

    protected int rectangleIndex;

    @Override
    protected void configureTest(Properties properties) {
        super.configureTest(properties);
        rectangleIndex = Integer.parseInt(properties.getProperty("rectangleIndex", "0"));
    }

    protected AbstractResolvedNameInfo getSubnameInMain(int index) throws TemplatorException {
        return getSubname(getMainAsResolvedName().getInfo(), index);
    }

    protected ResolvedName getMainAsResolvedName() throws TemplatorException {
        IASTFunctionDefinition mainDefinition = getMain();
        IASTName functionName = mainDefinition.getDeclarator().getName();
        AbstractResolvedNameInfo nameInfo = AbstractResolvedNameInfo.create(functionName, false, true, analyzer);
        return new ResolvedName(functionName, nameInfo);
    }

    protected AbstractResolvedNameInfo getSubname(AbstractResolvedNameInfo nameInfo, int index) throws TemplatorException {
        nameInfo.searchSubNames(new NullLoadingProgress());
        return nameInfo.getSubNames().get(index).getInfo();
    }

    // statement y -> definition x (x, y from config in rts file)
    protected void statementMatchesExpectedDefinition() throws TemplatorException {
        statementMatchesExpectedDefinition(definitions.get(expectedDefinitionIndex), getSubnameInMain(rectangleIndex).getDefinition());
    }

    // statement y -> definition x
    protected void statementMatchesExpectedDefinition(IASTDeclaration definition, int rectangleIndex) {
        statementMatchesExpectedDefinition(definitions.get(rectangleIndex), definition);
    }

    protected void testArgumentMap(ICPPTemplateArgument... expected) throws TemplatorException {
        assertEquals(getSubnameInMain(rectangleIndex).getTemplateArgumentMap(), expected);
    }
}
