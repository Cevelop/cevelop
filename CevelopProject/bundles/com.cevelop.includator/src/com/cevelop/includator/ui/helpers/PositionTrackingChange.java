package com.cevelop.includator.ui.helpers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;

import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.helpers.filter.FilterHelper;
import com.cevelop.includator.helpers.filter.Predicate;


public class PositionTrackingChange extends Change {

    private final Change                           originalChange;
    private final Map<TextEdit, PositionTrackInfo> positionMap;
    private final IFile                            file;

    public PositionTrackingChange(Change originalChange, IFile file) {
        this.originalChange = ChangeHelper.cloneChange(originalChange);
        this.file = file;
        positionMap = new LinkedHashMap<>();
        createPositionMap(originalChange);
        DocumentPositionManager.handleChangeCreated(this);
    }

    private PositionTrackingChange(Change originalUndoChange, PositionTrackingChange previouslyAppliedChange) {
        originalChange = ChangeHelper.cloneChange(originalUndoChange);
        this.file = previouslyAppliedChange.file;
        positionMap = new LinkedHashMap<>();
        adaptUndoChangePositions(previouslyAppliedChange);
        DocumentPositionManager.handleChangeCreated(this);
    }

    private void createPositionMap(Change change) {
        new TextEditVisitor() {

            @Override
            protected void visitEdit(TextEdit edit) {
                Position pos = new Position(edit.getOffset(), edit.getLength());
                positionMap.put(edit, new PositionTrackInfo(pos));
            }
        }.accept(originalChange);
    }

    private void adaptUndoChangePositions(PositionTrackingChange previouslyAppliedChange) {
        new TextEditVisitor() {

            @Override
            protected void visitEdit(TextEdit edit) {
                positionMap.put(edit, new PositionTrackInfo());
            }
        }.accept(originalChange);
        initializePositions(previouslyAppliedChange);
    }

    private void initializePositions(PositionTrackingChange previouslyAppliedChange) {
        RelevantEditPredicate predicate = new RelevantEditPredicate();
        Collection<Entry<TextEdit, PositionTrackInfo>> relevantPreviouslyAppliedChanges = FilterHelper.filter(previouslyAppliedChange.positionMap
                .entrySet(), predicate);
        ArrayList<Entry<TextEdit, PositionTrackInfo>> relevantPreviouslyAppliedChangesList = new ArrayList<>(relevantPreviouslyAppliedChanges);
        ListIterator<Entry<TextEdit, PositionTrackInfo>> relevantPreviouslyAppliedChangesReverseIter = relevantPreviouslyAppliedChangesList
                .listIterator(relevantPreviouslyAppliedChangesList.size());
        Iterator<Entry<TextEdit, PositionTrackInfo>> iter = FilterHelper.filter(positionMap.entrySet(), predicate).iterator();
        while (iter.hasNext()) {
            Entry<TextEdit, PositionTrackInfo> curEditEntry = iter.next();
            Entry<TextEdit, PositionTrackInfo> previousChangeMatchingEdit = relevantPreviouslyAppliedChangesReverseIter.previous();
            PositionTrackInfo previousPosInfo = previousChangeMatchingEdit.getValue();
            Position previousPos = previousPosInfo.getCurrentPosition();
            Position previousOriginalPos = previousPosInfo.getOriginalPosition();
            curEditEntry.getValue().setCurrentPosition(new Position(previousPos.offset, previousPos.length));
            curEditEntry.getValue().setOriginalPosition(new Position(previousOriginalPos.getOffset(), previousOriginalPos.length));
        }
    }

    @Override
    public String getName() {
        return originalChange.getName();
    }

    @Override
    public void initializeValidationData(IProgressMonitor pm) {
        originalChange.initializeValidationData(pm);
    }

    @Override
    public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        return originalChange.isValid(pm);
    }

    @Override
    public Change perform(IProgressMonitor pm) throws CoreException {
        adaptPositionsToTracked();
        Change undoChange = originalChange.perform(pm);
        adaptEmptyPositions();
        return new PositionTrackingChange(undoChange, this);
    }

    /**
     * Assume that the position of an InsertEdit is X,0 (length will always be 0). After inserting a text of length Y (while performing the current
     * change) at X, the position will become X+Y,0. But we want it to become X,Y. This is solved here.
     */
    private void adaptEmptyPositions() {
        for (Entry<TextEdit, PositionTrackInfo> curEntry : positionMap.entrySet()) {
            TextEdit edit = curEntry.getKey();
            if (edit instanceof InsertEdit) {
                Position pos = curEntry.getValue().getCurrentPosition();
                pos.offset -= edit.getLength();
                pos.length += edit.getLength();
            }
        }
    }

    private void adaptPositionsToTracked() {
        OffsetDelta delta = new OffsetDelta();
        for (Entry<TextEdit, PositionTrackInfo> curEntry : positionMap.entrySet()) {
            TextEdit edit = curEntry.getKey();
            Position pos = curEntry.getValue().getCurrentPosition();
            if (pos != null && !pos.isDeleted()) {
                int newOffset = pos.offset + ((pos.offset >= delta.offset) ? delta.delta : 0);
                setField(edit, "fOffset", newOffset);
                setField(edit, "fLength", pos.getLength());
                adaptDelta(edit, delta);
            }
        }
        adaptNullPositionEdits();
    }

    /**
     * Edits which have no positions are UndoEdits. They do not have a tracking positions because it is hard to map UndoEdits to their originating
     * edits because they are extra edits which are not directly contained in its originating change. Tracking their position is not really necessary
     * since they always contain childEdits which's positions are correctly tracked.
     */
    private void adaptNullPositionEdits() {
        for (Entry<TextEdit, PositionTrackInfo> curEntry : positionMap.entrySet()) {
            if (curEntry.getValue().getCurrentPosition() == null) {
                updatePositionFromChildren(curEntry.getKey());
            }
        }

    }

    private void updatePositionFromChildren(TextEdit edit) {
        final int[] newOffsetData = new int[] { Integer.MAX_VALUE, -1 };
        edit.accept(new org.eclipse.text.edits.TextEditVisitor() {

            @Override
            public boolean visitNode(TextEdit edit) {
                if (!(edit instanceof InsertEdit || edit instanceof DeleteEdit || edit instanceof ReplaceEdit)) {
                    return true;
                }
                if (edit.getOffset() < newOffsetData[0]) {
                    newOffsetData[0] = edit.getOffset();
                }
                int endOffset = edit.getOffset() + edit.getLength();
                if (endOffset > newOffsetData[1]) {
                    newOffsetData[1] = endOffset;
                }
                return true;
            }
        });
        if (newOffsetData[0] != Integer.MAX_VALUE) {
            setField(edit, "fOffset", newOffsetData[0]);
            setField(edit, "fLength", newOffsetData[1] - newOffsetData[0]);
        }
    }

    private void adaptDelta(TextEdit edit, OffsetDelta delta) {
        if (edit instanceof InsertEdit) {
            adaptDelta(((InsertEdit) edit).getText().length(), edit.getOffset(), delta);
        } else if (edit instanceof DeleteEdit) {
            adaptDelta(-((DeleteEdit) edit).getLength(), edit.getOffset(), delta);
        } else if (edit instanceof ReplaceEdit) {
            ReplaceEdit replaceEdit = (ReplaceEdit) edit;
            adaptDelta(replaceEdit.getText().length() - replaceEdit.getLength(), edit.getOffset(), delta);
        }
    }

    private void adaptDelta(int deltaChange, int newOffset, OffsetDelta delta) {
        delta.offset = newOffset;
        delta.delta += deltaChange;
    }

    private void setField(TextEdit edit, String fieldName, int value) {
        try {
            Field field = TextEdit.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(edit, value);
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

    @Override
    public Object getModifiedElement() {
        return originalChange.getModifiedElement();
    }

    @Override
    public ChangeDescriptor getDescriptor() {
        return originalChange.getDescriptor();
    }

    @Override
    public boolean isEnabled() {
        return originalChange.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        originalChange.setEnabled(enabled);
    }

    @Override
    public Change getParent() {
        return originalChange.getParent();
    }

    @Override
    public void dispose() {
        DocumentPositionManager.handleChangeDisposed(this);
        originalChange.dispose();
    }

    @Override
    public Object[] getAffectedObjects() {
        return originalChange.getAffectedObjects();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Object getAdapter(Class adapter) {
        return originalChange.getAdapter(adapter);
    }

    public Change getWrappedChange() {
        adaptPositionsToTracked();
        return originalChange;
    }

    public void attachPositions(IDocument document) {
        for (PositionTrackInfo curInfo : positionMap.values()) {
            try {
                Position currentPosition = curInfo.getCurrentPosition();
                if (currentPosition != null) {
                    document.addPosition(currentPosition);
                }
            } catch (BadLocationException e) {
                throw new IncludatorException(e);
            }

        }
    }

    public void detachPositions(IDocument document) {
        for (PositionTrackInfo curInfo : positionMap.values()) {
            Position pos = curInfo.getCurrentPosition();
            if (pos != null) {
                document.removePosition(pos);
                pos.delete();
                Position originalPos = curInfo.getOriginalPosition();
                curInfo.setCurrentPosition(new Position(originalPos.offset, originalPos.length));
            }
        }
    }

    public IFile getIFile() {
        return file;
    }

    public void persistatePositions() {
        for (PositionTrackInfo curPosInfo : positionMap.values()) {
            Position currentPosition = curPosInfo.getCurrentPosition();
            if (currentPosition != null) {
                curPosInfo.setOriginalPosition(new Position(currentPosition.offset, currentPosition.length));
            }
        }
    }

    public boolean becameEmpty() {
        boolean result = !positionMap.isEmpty();
        for (PositionTrackInfo curPosInfo : positionMap.values()) {
            Position currentPosition = curPosInfo.getCurrentPosition();
            if (currentPosition != null) {
                result &= curPosInfo.getOriginalPosition().getLength() != 0 && currentPosition.length == 0;
            }
        }
        return result;
    }

    /**
     * This method is intended for testing only! do not modify the position map!!!!
     *
     * @return the position map as {@code Map<TextEdit, PositionTrackInfo>}
     */
    public Map<TextEdit, PositionTrackInfo> getPositionMap() {
        return positionMap;
    }
}



class RelevantEditPredicate implements Predicate<Entry<TextEdit, PositionTrackInfo>> {

    @Override
    public boolean matches(Entry<TextEdit, PositionTrackInfo> entry) {
        TextEdit edit = entry.getKey();
        return !(edit instanceof UndoEdit || edit instanceof MultiTextEdit);
    }

}
