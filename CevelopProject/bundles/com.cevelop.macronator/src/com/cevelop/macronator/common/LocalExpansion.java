package com.cevelop.macronator.common;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroExpansion;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.parser.util.CharArrayMap;
import org.eclipse.cdt.internal.core.parser.scanner.ILexerLog;
import org.eclipse.cdt.internal.core.parser.scanner.Lexer.LexerOptions;
import org.eclipse.cdt.internal.core.parser.scanner.MacroExpander;
import org.eclipse.cdt.internal.core.parser.scanner.MacroExpansionTracker;
import org.eclipse.text.edits.ReplaceEdit;


@SuppressWarnings({ "restriction", "rawtypes", "unchecked" })
public class LocalExpansion {

    private final IASTPreprocessorMacroExpansion macroExpansion;

    public LocalExpansion(IASTPreprocessorMacroExpansion macroExpansion) {
        this.macroExpansion = macroExpansion;
    }

    public String getExpansion() {
        ArrayList<IASTName> macroNames = new ArrayList<>();
        macroNames.add(macroExpansion.getMacroReference());
        macroNames.addAll(Arrays.asList(macroExpansion.getNestedMacroReferences()));
        MacroExpander expander = new MacroExpander(ILexerLog.NULL, createDictionary(macroNames.toArray(new IASTName[macroNames.size()])), null,
                new LexerOptions());
        MacroExpansionTracker tracker = new MacroExpansionTracker(Integer.MAX_VALUE);
        expander.expand(macroExpansion.getRawSignature(), tracker, macroExpansion.getFileLocation().getFileName(), 0, false);
        ReplaceEdit r = tracker.getReplacement();
        return r.getText();
    }

    private CharArrayMap createDictionary(IASTName[] refs) {
        CharArrayMap map = new CharArrayMap<>(refs.length);
        for (IASTName name : refs) {
            IBinding binding = name.getBinding();
            map.put(name.getSimpleID(), binding);
        }
        return map;
    }
}
