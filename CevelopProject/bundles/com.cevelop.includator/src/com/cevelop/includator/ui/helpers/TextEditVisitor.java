package com.cevelop.includator.ui.helpers;

import java.lang.reflect.Field;

import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.UndoTextFileChange;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;

import com.cevelop.includator.helpers.IncludatorException;


public abstract class TextEditVisitor {

    public void accept(Change... changes) {
        for (Change curChange : changes) {
            try {
                if (curChange instanceof CompositeChange) {
                    CompositeChange compositeChange = (CompositeChange) curChange;
                    accept(compositeChange.getChildren());
                } else if (curChange instanceof TextFileChange) {
                    TextFileChange textChange = (TextFileChange) curChange;
                    internalVisitEdit(textChange.getEdit());
                } else if (curChange instanceof UndoTextFileChange) {
                    UndoTextFileChange undoChange = (UndoTextFileChange) curChange;
                    Field field = UndoTextFileChange.class.getDeclaredField("fUndo");
                    field.setAccessible(true);
                    UndoEdit undoEdit = (UndoEdit) field.get(undoChange);
                    internalVisitEdit(undoEdit);
                }
            } catch (Exception e) {
                throw new IncludatorException(e);
            }
        }
    }

    private void internalVisitEdit(TextEdit edit) {
        visitEdit(edit);
        for (TextEdit curChild : edit.getChildren()) {
            internalVisitEdit(curChild);
        }
    }

    protected abstract void visitEdit(TextEdit edit);
}
