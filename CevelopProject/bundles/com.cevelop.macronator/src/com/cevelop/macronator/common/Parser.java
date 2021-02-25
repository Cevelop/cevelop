package com.cevelop.macronator.common;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.parser.cpp.ANSICPPParserExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.cpp.GPPScannerExtensionConfiguration;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IScanner;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.NullLogService;
import org.eclipse.cdt.core.parser.ParserLanguage;
import org.eclipse.cdt.core.parser.ParserMode;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.cdt.internal.core.dom.parser.cpp.GNUCPPSourceParser;
import org.eclipse.cdt.internal.core.parser.scanner.CPreprocessor;


@SuppressWarnings("restriction")
public class Parser {

    private final static String      TEST_CODE = "<testcode>";
    private final GNUCPPSourceParser parser;

    public Parser(String code) {
        parser = getParser(code);
    }

    public IASTTranslationUnit parse() {
        return parser.parse();
    }

    public boolean encounteredErrors() {
        return parser.encounteredError();
    }

    private GNUCPPSourceParser getParser(String code) {
        IScanner scanner = createScanner(FileContent.create(TEST_CODE, code.toCharArray()), ParserLanguage.CPP);
        ANSICPPParserExtensionConfiguration config = new ANSICPPParserExtensionConfiguration();
        return new GNUCPPSourceParser(scanner, ParserMode.COMPLETE_PARSE, new NullLogService(), config, null);
    }

    private IScanner createScanner(FileContent fileContent, ParserLanguage lang) {
        ScannerInfo scannerInfo = new ScannerInfo(getStdMap());
        GPPScannerExtensionConfiguration configuration = GPPScannerExtensionConfiguration.getInstance(scannerInfo);
        return new CPreprocessor(fileContent, scannerInfo, lang, new NullLogService(), configuration, IncludeFileContentProvider
                .getSavedFilesProvider());
    }

    private static Map<String, String> getStdMap() {
        Map<String, String> map = new HashMap<>();
        map.put("__SIZEOF_SHORT__", "2");
        map.put("__SIZEOF_INT__", "4");
        map.put("__SIZEOF_LONG__", "8");
        map.put("__SIZEOF_POINTER__", "8");
        return map;
    }
}
