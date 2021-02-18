package com.cevelop.includator.ui.helpers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.UndoTextFileChange;
import org.eclipse.text.edits.TextEdit;

import com.cevelop.includator.helpers.IncludatorException;


public class ChangeHelper {

    public static Change cloneChange(Change changeToClone) {
        if (changeToClone instanceof CompositeChange) {
            CompositeChange compChange = (CompositeChange) changeToClone;
            CompositeChange newChange = new CompositeChange(compChange.getName());
            for (Change curChild : compChange.getChildren()) {
                newChange.add(cloneChange(curChild));
            }
            return newChange;
        }
        if (changeToClone instanceof TextFileChange) {
            TextFileChange textFileChange = (TextFileChange) changeToClone;
            TextFileChange newChange = new TextFileChange(changeToClone.getName(), textFileChange.getFile());
            newChange.setEdit(textFileChange.getEdit());
            newChange.setSaveMode(textFileChange.getSaveMode());
            return newChange;
        }
        if (changeToClone instanceof UndoTextFileChange) {
            UndoTextFileChange undoTextFileChange = (UndoTextFileChange) changeToClone;
            TextFileChange newChange = new TextFileChange(changeToClone.getName(), getUndoTextFile(undoTextFileChange));
            newChange.setEdit(getUndoTextEdit(undoTextFileChange));
            newChange.setSaveMode(undoTextFileChange.getSaveMode());
            return newChange;
        }
        throw new IncludatorException("Cloning of change " + changeToClone.getClass().getName() + " not jet supported");
    }

    private static TextEdit getUndoTextEdit(UndoTextFileChange undoTextFileChange) {
        try {
            Field editField = UndoTextFileChange.class.getDeclaredField("fUndo");
            editField.setAccessible(true);
            return (TextEdit) editField.get(undoTextFileChange);
        } catch (SecurityException e) {
            throw new IncludatorException(e);
        } catch (NoSuchFieldException e) {
            throw new IncludatorException(e);
        } catch (IllegalArgumentException e) {
            throw new IncludatorException(e);
        } catch (IllegalAccessException e) {
            throw new IncludatorException(e);
        }
    }

    private static IFile getUndoTextFile(UndoTextFileChange undoTextFileChange) {
        Object modifiedElement = undoTextFileChange.getModifiedElement();
        if (modifiedElement instanceof IFile) {
            return (IFile) modifiedElement;
        }
        throw new IncludatorException("Modified element of undo change is no IFile.");
    }

    public static List<TextEdit> extractTextEdits(Change change) {
        List<TextEdit> result = new ArrayList<>();
        if (change instanceof CompositeChange) {
            for (Change curChild : ((CompositeChange) change).getChildren()) {
                result.addAll(extractTextEdits(curChild));
            }
        } else if (change instanceof TextFileChange) {
            result.add(((TextFileChange) change).getEdit());
        } else if (change instanceof PositionTrackingChange) {
            result.addAll(extractTextEdits(((PositionTrackingChange) change).getWrappedChange()));
        }
        return result;
    }

}
