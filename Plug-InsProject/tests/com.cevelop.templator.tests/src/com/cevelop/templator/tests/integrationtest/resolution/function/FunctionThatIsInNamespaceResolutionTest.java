package com.cevelop.templator.tests.integrationtest.resolution.function;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.junit.Test;

import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.FunctionTemplateResolutionTest;
import com.cevelop.templator.tests.TemplateArgument;


public class FunctionThatIsInNamespaceResolutionTest extends FunctionTemplateResolutionTest {

    @Test
    public void testOuterArgumentMapIsInt() {
        testOuterArgumentMap(TemplateArgument.INT);
    }

    @Test
    public void testSubcallArgumentIsInt() throws TemplatorException {
        testFirstInnerArgumentMap(TemplateArgument.INT);
    }

    @Test
    public void testSubcallResolvedToFunctionTemplateAndNotNormalFunction() throws TemplatorException {
        ICPPASTNamespaceDefinition outerNamespace = (ICPPASTNamespaceDefinition) analyzer.getAst().getDeclarations()[0];
        ICPPASTNamespaceDefinition innerNamespace = (ICPPASTNamespaceDefinition) outerNamespace.getDeclarations()[0];
        IASTDeclaration expected = ((ICPPASTTemplateDeclaration) innerNamespace.getDeclarations()[0]).getDeclaration();
        testFirstInnerCallResolvesTo((IASTFunctionDefinition) expected);
    }
}
