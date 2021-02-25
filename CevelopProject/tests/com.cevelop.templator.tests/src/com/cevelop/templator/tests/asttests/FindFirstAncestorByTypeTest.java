package com.cevelop.templator.tests.asttests;

import static org.junit.Assert.assertEquals;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.junit.Test;

import com.cevelop.templator.plugin.asttools.ASTAnalyzer;
import com.cevelop.templator.plugin.asttools.ASTTools;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.TemplatorSimpleTest;
import com.cevelop.templator.tests.TestHelper;


public class FindFirstAncestorByTypeTest extends TemplatorSimpleTest {

    @Test
    // void foo(int i){}
    // int main() {
    // foo(5);
    // }
    public void testFindAncestorWithTypeICPPASTFunctionCallExpressionWithoutAngleBrackets() throws Exception {
        String source = TestHelper.getCommentAbove(getClass());
        ASTAnalyzer analyzer = getASTAnalyzerFromSource(source);
        IASTTranslationUnit ast = analyzer.getAst();
        IASTName functionCallName = TestHelper.findName(ast, 1, "foo");

        ICPPASTFunctionCallExpression foundCallExpression = ASTTools.findFirstAncestorByType(functionCallName, ICPPASTFunctionCallExpression.class);
        assertEquals(functionCallName.getParent().getParent(), foundCallExpression);
    }

    @Test
    // template<typename T> void foo(T value){}
    // int main() {
    // foo<int>(5);
    // }
    public void testFindAncestorWithTypeICPPASTFunctionCallExpressionWithAngleBrackets() throws Exception {
        String source = TestHelper.getCommentAbove(getClass());
        ASTAnalyzer analyzer = getASTAnalyzerFromSource(source);
        IASTTranslationUnit ast = analyzer.getAst();
        IASTName functionCallName = TestHelper.findName(ast, 1, "foo");

        ICPPASTFunctionCallExpression foundCallExpression = ASTTools.findFirstAncestorByType(functionCallName, ICPPASTFunctionCallExpression.class);

        assertEquals(functionCallName.getParent().getParent().getParent(), foundCallExpression);
    }

    @Test
    // namespace bar{
    // template<typename T> void foo(T value){}
    // }
    // int main() {
    // bar::foo<int>(5);
    // }
    public void testFindAncestorWithTypeICPPASTFunctionCallExpressionWithNamespace() throws Exception {
        String source = TestHelper.getCommentAbove(getClass());
        ASTAnalyzer analyzer = getASTAnalyzerFromSource(source);
        IASTTranslationUnit ast = analyzer.getAst();
        IASTName functionCallName = TestHelper.findName(ast, 1, "foo");

        ICPPASTFunctionCallExpression foundCallExpression = ASTTools.findFirstAncestorByType(functionCallName, ICPPASTFunctionCallExpression.class);
        IASTNode expectedCallExpression = functionCallName.getParent().getParent().getParent().getParent();
        assertEquals(expectedCallExpression, foundCallExpression);
    }

    // namespace bar{
    // namespace inner {
    // template<typename T> void foo(T value){}
    // }
    // }
    // int main() {
    // bar::inner::foo<int>(5);
    // }
    public void testFindAncestorWithTypeICPPASTFunctionCallExpressionWithDoubleNamespace() throws Exception {
        String source = TestHelper.getCommentAbove(getClass());
        ASTAnalyzer analyzer = getASTAnalyzerFromSource(source);
        IASTTranslationUnit ast = analyzer.getAst();
        IASTName functionCallName = TestHelper.findName(ast, 1, "foo");

        ICPPASTFunctionCallExpression foundCallExpression = ASTTools.findFirstAncestorByType(functionCallName, ICPPASTFunctionCallExpression.class);
        IASTNode expectedCallExpression = functionCallName.getParent().getParent().getParent().getParent();
        assertEquals(expectedCallExpression, foundCallExpression);
    }

    @Test
    // template<typename T>
    // void foo(T value){}
    public void testFindAncestorWithTypeICPPASTFunctionDefinitionGoodCase() throws Exception {
        String source = TestHelper.getCommentAbove(getClass());
        ASTAnalyzer analyzer = getASTAnalyzerFromSource(source);
        IASTTranslationUnit ast = analyzer.getAst();
        IASTName functionCallName = TestHelper.findName(ast, 0, "foo");

        ICPPASTFunctionDefinition foundCallExpression = ASTTools.findFirstAncestorByType(functionCallName, ICPPASTFunctionDefinition.class);
        assertEquals(functionCallName.getParent().getParent(), foundCallExpression);
    }

    @Test
    // template<typename T>
    // struct Foo {
    //   Foo() {}
    //   void bar() {}
    // };
    public void testFindAncestorWithTypeICPPASTTemplateDeclarationWithMaxLevel4() throws Exception {
        String source = TestHelper.getCommentAbove(getClass());
        ASTAnalyzer analyzer = getASTAnalyzerFromSource(source);
        IASTTranslationUnit ast = analyzer.getAst();
        IASTName barMethodName = TestHelper.findName(ast, 0, "bar");

        ICPPASTTemplateDeclaration templateDeclaration = ASTTools.findFirstAncestorByType(barMethodName, ICPPASTTemplateDeclaration.class, 4);
        assertEquals(null, templateDeclaration);
    }

    @Test(expected = TemplatorException.class)
    public void testFindAncestorWithTypeNullNode() throws TemplatorException {
        ASTTools.findFirstAncestorByType(null, ICPPASTFunctionCallExpression.class);
    }
}
