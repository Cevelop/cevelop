package com.cevelop.templator.tests.asttests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.parser.ParserLanguage;
import org.eclipse.cdt.internal.core.dom.rewrite.astwriter.ASTWriter;
import org.junit.Test;

import com.cevelop.templator.plugin.asttools.ASTAnalyzer;
import com.cevelop.templator.tests.TemplatorSimpleTest;
import com.cevelop.templator.tests.TestHelper;


public class GetFunctionDefinitionFromBindingTest extends TemplatorSimpleTest {

    // template<typename T>
    // T bar(T value) {
    // int blub = 333;
    // return value * 42;
    // }
    public ICPPASTFunctionDefinition getPlainFunctionDeclaration() throws Exception {
        String source = TestHelper.getCommentAbove(getClass());
        IASTTranslationUnit ast = TestHelper.parse(source, ParserLanguage.CPP);

        IASTDeclaration iastDeclaration = ast.getDeclarations()[0];

        ICPPASTTemplateDeclaration tDecl = (ICPPASTTemplateDeclaration) iastDeclaration;

        return (ICPPASTFunctionDefinition) tDecl.getDeclaration();
    }

    @Test
    // template<typename T>
    // T bar(T value) {
    // int blub = 333;
    // return value * 42;
    // }
    // int main() {
    // bar(-30.0);
    // }
    public void testResolveFunctionDefinitionWithoutAngleBrackets() throws Exception {

        String source = TestHelper.getCommentAbove(getClass());
        ASTAnalyzer analyzer = getASTAnalyzerFromSource(source);
        IASTTranslationUnit ast = analyzer.getAst();

        IASTName occurance = TestHelper.findName(ast, 1, "bar");
        IBinding b = occurance.resolveBinding();
        IASTFunctionDefinition functionDefinition = analyzer.getFunctionDefinition(b);

        ICPPASTFunctionDefinition plainFunctionDeclaration = getPlainFunctionDeclaration();

        // Test with Instances

        String plainDefString = new ASTWriter().write(plainFunctionDeclaration);
        String resolvedDefString = new ASTWriter().write(functionDefinition);

        assertEquals(plainDefString, resolvedDefString);
    }

    @Test
    // template<typename T>
    // T bar(T value) {
    // int blub = 333;
    // return value * 42;
    // }
    // int main() {
    // bar<double>(-30.0);
    // }
    public void testResolveFunctionDefinitionWithAngleBrackets() throws Exception {
        String source = TestHelper.getCommentAbove(getClass());
        IASTTranslationUnit ast = TestHelper.parse(source, ParserLanguage.CPP);
        ASTAnalyzer analyzer = new ASTAnalyzer(null, ast);

        IASTName occurance = TestHelper.findName(ast, 1, "bar");
        IBinding b = occurance.resolveBinding();
        IASTFunctionDefinition functionDefinition = analyzer.getFunctionDefinition(b);

        String trimmedSource = source.replaceAll("\\s", "");
        String definitionString = new ASTWriter().write(functionDefinition);
        String trimmedDefinition = definitionString.replaceAll("\\s", "");

        assertTrue(trimmedSource.contains(trimmedDefinition));
    }
}
