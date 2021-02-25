package com.cevelop.templator.tests;

import java.io.IOException;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.parser.ParserLanguage;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.cdt.core.parser.tests.ast2.AST2TestBase;
import org.eclipse.cdt.core.testplugin.TestScannerProvider;
import org.eclipse.cdt.core.testplugin.util.TestSourceReader;
import org.eclipse.cdt.internal.core.parser.ParserException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;


public final class TestHelper {

    private TestHelper() {}

    private static class AST2TestMock extends AST2TestBase {

        @Override
        public IASTTranslationUnit parse(String code, ParserLanguage lang) throws ParserException {
            return super.parse(code, lang);
        }

        public static IASTName findName(IASTNode searchRoot, int idx, String searchName) {
            NameCollector collector = new NameCollector();
            searchRoot.accept(collector);

            int foundOccurances = 0;
            for (IASTName name : collector.nameList) {

                if (name.toString().equals(searchName)) {
                    if (foundOccurances++ == idx) {
                        return name;
                    }
                }
            }
            return null;
        }

        @Override
        public ScannerInfo createScannerInfo(boolean useGnu) {
            // this is needed that the includes are found inside the RTS files
            return (ScannerInfo) new TestScannerProvider().getScannerInformation(null);
        }
    }

    public static IASTTranslationUnit parse(String originalSource, ParserLanguage cpp) {

        AST2TestMock mockTest = new AST2TestMock();
        try {
            return mockTest.parse(originalSource, ParserLanguage.CPP);
        } catch (ParserException e) {
            // do nothing, the AST2Test parse method will fail the test if something is not good
        }
        return null;
    }

    public static IASTName findName(IASTNode searchRoot, int occuranceIdx, String searchName) {
        return AST2TestMock.findName(searchRoot, occuranceIdx, searchName);
    }

    public static String getCommentAbove(Class<? extends Object> c) throws IOException {
        return getContents(1, c)[0].toString();
    }

    private static CharSequence[] getContents(int sections, Class<? extends Object> c) throws IOException {
        Bundle bundle = null;
        ClassLoader cl = c.getClassLoader();
        if (cl instanceof BundleReference) {
            bundle = ((BundleReference) cl).getBundle();
        }

        return TestSourceReader.getContentsForTest(bundle, "src", c, getMethodName(4), sections);
    }

    private static String getMethodName(int depth) {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        return ste[depth].getMethodName();
    }

}
