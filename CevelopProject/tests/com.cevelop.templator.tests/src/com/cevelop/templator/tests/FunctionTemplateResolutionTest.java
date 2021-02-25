package com.cevelop.templator.tests;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateArgument;
import org.junit.Assert;

import com.cevelop.templator.plugin.asttools.data.AbstractResolvedNameInfo;
import com.cevelop.templator.plugin.asttools.data.NameTypeKind;
import com.cevelop.templator.plugin.asttools.data.ResolvedName;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.plugin.util.ILoadingProgress;


public abstract class FunctionTemplateResolutionTest extends TemplatorResolutionTest {

    protected static ILoadingProgress loadingProgress = new NullLoadingProgress();

    @Override
    protected void initTopLevelDefinitions() {
        super.initTopLevelDefinitions();

        for (int i = 0; i < definitions.size(); i++) {
            IASTDeclaration topLevelDeclaration = definitions.get(i);
            if (topLevelDeclaration instanceof ICPPASTTemplateDeclaration) {
                ICPPASTTemplateDeclaration templateDeclaration = (ICPPASTTemplateDeclaration) topLevelDeclaration;
                IASTDeclaration declaration = templateDeclaration.getDeclaration();
                if (declaration instanceof IASTFunctionDefinition) {
                    definitions.set(i, declaration);
                }
            }
        }
    }

    protected List<ResolvedName> getOnlyFunctionCallSubstatements(AbstractResolvedNameInfo instance) {
        List<ResolvedName> functionCalls = new ArrayList<>();
        for (ResolvedName sub : instance.getSubNames()) {
            NameTypeKind type = sub.getInfo().getType();
            if (type == NameTypeKind.FUNCTION_TEMPLATE || type == NameTypeKind.FUNCTION) {
                functionCalls.add(sub);
            }
        }
        return functionCalls;
    }

    protected void testOuterArgumentMap(ICPPTemplateArgument... expectedArguments) {
        assertEquals(firstStatementInMain.getTemplateArgumentMap(), expectedArguments);
    }

    protected void testFirstInnerArgumentMap(ICPPTemplateArgument... expected) throws TemplatorException {
        firstStatementInMain.searchSubNames(loadingProgress);
        AbstractResolvedNameInfo innerCall = getOnlyFunctionCallSubstatements(firstStatementInMain).get(0).getInfo();
        assertEquals(innerCall.getTemplateArgumentMap(), expected);
    }

    protected void testFirstInnerCallResolvesToFirstDefinition() throws TemplatorException {
        testFirstInnerCallResolvesTo((IASTFunctionDefinition) definitions.get(0));
    }

    protected void testFirstInnerCallResolvesTo(IASTFunctionDefinition expected) throws TemplatorException {
        firstStatementInMain.searchSubNames(loadingProgress);
        ResolvedName actualCall = getOnlyFunctionCallSubstatements(firstStatementInMain).get(0);
        IASTDeclaration actualDefinition = actualCall.getInfo().getDefinition();
        if (actualDefinition instanceof IASTFunctionDefinition) {
            Assert.assertEquals(expected, actualDefinition);
        } else if (actualDefinition instanceof ICPPASTTemplateDeclaration) {
            actualDefinition = ((ICPPASTTemplateDeclaration) actualDefinition).getDeclaration();
            Assert.assertEquals(expected, actualDefinition);
        } else {
            fail("could not determine IASTFunctionDefinition for " + actualCall.getOriginalNode());
        }
    }
}
