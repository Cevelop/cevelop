package com.cevelop.includator.helpers;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.NullLogService;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.core.runtime.CoreException;


public class SyntheticASTHelper {

    public static IASTTranslationUnit getAstForContent(String source) throws CoreException {
        String fileName = "syntheticFile.c";
        FileContent content = FileContent.create(fileName, source.toCharArray());
        GPPLanguage lang = GPPLanguage.getDefault();
        ScannerInfo scanInfo = new ScannerInfo();
        IncludeFileContentProvider includeFileContentProvider = IncludeFileContentProvider.getEmptyFilesProvider();
        int options = 0;
        IIndex index = null;
        return lang.getASTTranslationUnit(content, scanInfo, includeFileContentProvider, index, options, new NullLogService());
    }
}
