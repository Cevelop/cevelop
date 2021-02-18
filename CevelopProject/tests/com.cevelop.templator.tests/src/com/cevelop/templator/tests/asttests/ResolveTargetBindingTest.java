package com.cevelop.templator.tests.asttests;

import static org.junit.Assert.assertTrue;

import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.core.parser.ParserLanguage;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.templator.plugin.asttools.ASTAnalyzer;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.tests.TemplatorResolutionTest;
import com.cevelop.templator.tests.TestHelper;


public class ResolveTargetBindingTest extends TemplatorResolutionTest {

    @Test
    // void bar() {}
    // int main() {
    // bar();
    // }
    public void testResolveTargetBindingNonIndexBinding() throws Exception {
        String source = TestHelper.getCommentAbove(getClass());

        IASTTranslationUnit ast = TestHelper.parse(source, ParserLanguage.CPP);
        ASTAnalyzer analyzer = new ASTAnalyzer(null, ast);

        IASTName occurance = TestHelper.findName(ast, 1, "bar");

        IBinding actual = analyzer.resolveTargetBinding(occurance);
        IBinding expected = occurance.resolveBinding();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testResolveTargetBindingToIndexBindingAdaptBindingEnabled() throws TemplatorException {
        IASTFunctionDefinition main = getMain();
        IASTName functionCallName = getSubStatements(main).get(0).getResolvingName();

        IBinding resolveTargetBinding = analyzer.resolveTargetBinding(functionCallName);
        assertTrue(resolveTargetBinding instanceof IIndexBinding);
    }
}
