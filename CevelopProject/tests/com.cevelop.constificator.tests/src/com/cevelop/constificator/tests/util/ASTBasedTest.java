package com.cevelop.constificator.tests.util;

import java.io.IOException;

import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeSelector;
import org.eclipse.cdt.core.parser.ParserLanguage;
import org.eclipse.cdt.core.parser.tests.ast2.AST2TestBase;
import org.eclipse.cdt.core.testplugin.util.TestSourceReader;
import org.eclipse.cdt.internal.core.parser.ParserException;
import org.osgi.framework.FrameworkUtil;

import com.cevelop.constificator.core.util.type.Cast;


@SuppressWarnings("restriction")
public abstract class ASTBasedTest extends AST2TestBase {

    public class AssertionHelper extends AST2AssertionHelper {

        public AssertionHelper(String contents, ParserLanguage lang) throws ParserException {
            super(contents, lang);
        }

        public IASTLiteralExpression findLiteral(String literal) {
            return findLiteral(null, literal);
        }

        public IASTLiteralExpression findLiteral(String context, String literal) {
            if (context == null) {
                context = contents;
            }
            int offset = contents.indexOf(context);
            assertTrue("Context \"" + context + "\" not found", offset >= 0);
            int literalOffset = context.indexOf(literal);
            assertTrue("Literal \"" + literal + "\" not found", literalOffset >= 0);
            IASTNodeSelector selector = tu.getNodeSelector(null);
            IASTNode node = selector.findNode(offset + literalOffset, literal.length());
            return Cast.as(IASTLiteralExpression.class, node);
        }

    }

    protected AssertionHelper getAssertionHelper() {
        AssertionHelper assertionHelper = null;
        try {
            String comment = getAboveComment();
            if (comment == null) {
                fail("Testcode not found!");
            }
            assertionHelper = new AssertionHelper(comment, ParserLanguage.CPP);
        } catch (Throwable e) {
            fail(e.getMessage());
        }

        assertNotNull("Failed to acquire BindingAssertionHelper instance.", assertionHelper);
        return assertionHelper;
    }

    @Override
    protected CharSequence[] getContents(int sections) throws IOException {
        return TestSourceReader.getContentsForTest(FrameworkUtil.getBundle(getClass()), "src", getClass(), getName(), sections);
    }

}
