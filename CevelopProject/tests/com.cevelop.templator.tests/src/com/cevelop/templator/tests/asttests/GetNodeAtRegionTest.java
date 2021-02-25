package com.cevelop.templator.tests.asttests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.parser.ParserLanguage;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.junit.Test;

import com.cevelop.templator.plugin.asttools.ASTTools;
import com.cevelop.templator.tests.TemplatorSimpleTest;
import com.cevelop.templator.tests.TestHelper;


public class GetNodeAtRegionTest extends TemplatorSimpleTest {

    @Test
    // int main()
    // {
    // return 0;
    // }
    public void testGetNodeAtRegionGoodCase() throws Exception {

        String originalSource = TestHelper.getCommentAbove(getClass());

        String searchString = "main";
        int begin = originalSource.indexOf(searchString);
        IRegion region = new Region(begin, searchString.length());

        IASTTranslationUnit ast = TestHelper.parse(originalSource, ParserLanguage.CPP);
        IASTNode node = ASTTools.getNodeAtRegion(region, ast);

        assertTrue(node instanceof IASTName);
        assertEquals(searchString, node.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNodeAtRegionASTNull() {
        IRegion region = new Region(0, 0);
        ASTTools.getNodeAtRegion(region, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNodeAtRegionRegionNull() {
        IASTTranslationUnit ast = TestHelper.parse("", ParserLanguage.CPP);
        ASTTools.getNodeAtRegion(null, ast);
    }
}
