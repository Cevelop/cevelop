package com.cevelop.macronator.common;

import org.eclipse.cdt.core.dom.parser.cpp.GPPScannerExtensionConfiguration;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IScanner;
import org.eclipse.cdt.core.parser.IToken;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.NullLogService;
import org.eclipse.cdt.core.parser.ParserLanguage;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.cdt.internal.core.parser.scanner.CPreprocessor;


@SuppressWarnings("restriction")
public class LexerAdapter {

    private final IScanner lexer;
    private IToken         currentToken;
    private boolean        endOfInputReached;

    public LexerAdapter(String input) {
        lexer = createScanner(input);
        endOfInputReached = false;
        nextToken();
    }

    public IToken nextToken() {
        IToken returnToken = currentToken;
        try {
            currentToken = lexer.nextToken();
        } catch (Exception e) {
            endOfInputReached = true;

        }
        return returnToken;
    }

    public boolean atEndOfInput() {
        return endOfInputReached || currentToken.getType() == IToken.tEND_OF_INPUT;
    }

    private IScanner createScanner(String input) {
        FileContent content = FileContent.create("<testcode>", input.toCharArray());
        ScannerInfo scannerInfo = new ScannerInfo();
        GPPScannerExtensionConfiguration configuration = GPPScannerExtensionConfiguration.getInstance(scannerInfo);
        return new CPreprocessor(content, new ScannerInfo(), ParserLanguage.CPP, new NullLogService(), configuration, IncludeFileContentProvider
                .getSavedFilesProvider());
    }
}
