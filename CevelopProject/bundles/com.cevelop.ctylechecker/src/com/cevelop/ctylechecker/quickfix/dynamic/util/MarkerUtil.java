package com.cevelop.ctylechecker.quickfix.dynamic.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import com.cevelop.ctylechecker.quickfix.dynamic.model.MarkerModel;
import com.cevelop.ctylechecker.service.types.CtylecheckerRuntime;


public class MarkerUtil {

    public static MarkerModel createModel(IMarker pMarker, IDocument pDocument) {
        int charStart = pMarker.getAttribute("charStart", 0);
        int charEnd = pMarker.getAttribute("charEnd", 0);
        String name = "";
        try {
            name = pDocument.get(charStart, charEnd - charStart);
        } catch (BadLocationException e) {
            CtylecheckerRuntime.log(e);
        }
        return new MarkerModel(charStart, charEnd, name, (IFile) pMarker.getResource());
    }
}
