package com.cevelop.templator.tests;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.parser.ParserLanguage;

import com.cevelop.templator.plugin.asttools.ASTAnalyzer;


public abstract class TemplatorSimpleTest {

    protected ASTAnalyzer getASTAnalyzerFromSource(String source) throws Exception {
        IASTTranslationUnit ast = TestHelper.parse(source, ParserLanguage.CPP);
        if (ast == null) {
            throw new Exception("parse exception in your test code");
        }

        return new ASTAnalyzer(null, ast);
    }
}
