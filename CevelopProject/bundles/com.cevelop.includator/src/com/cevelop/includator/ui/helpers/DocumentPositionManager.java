package com.cevelop.includator.ui.helpers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.IFileBuffer;
import org.eclipse.core.filebuffers.IFileBufferListener;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.stores.SuggestionStore;
import com.cevelop.includator.ui.actions.IncludatorJob;


public class DocumentPositionManager {

    private final FileBufferListener fileBufferListener;

    public DocumentPositionManager() {
        ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
        fileBufferListener = new FileBufferListener();
        manager.addFileBufferListener(fileBufferListener);
        initAlreadyOpenedBuffers(manager);
    }

    private void initAlreadyOpenedBuffers(ITextFileBufferManager manager) {
        for (IFileBuffer curConnectedBuffer : manager.getFileBuffers()) {
            fileBufferListener.bufferCreated(curConnectedBuffer);
        }
    }

    private void addPositionsToTrack(ITextFileBuffer buffer) {
        for (Suggestion<?> curSuggestion : findAffectedSuggestions(buffer)) {
            if (curSuggestion.hasQuickFix()) {
                PositionTrackingChange change = curSuggestion.getContainedPositionTrackingChange();
                if (change != null) {
                    change.attachPositions(buffer.getDocument());
                }
            }
        }
    }

    public void removeTrackedPositions(ITextFileBuffer buffer) {
        for (Suggestion<?> curSuggestion : findAffectedSuggestions(buffer)) {
            PositionTrackingChange change = curSuggestion.getContainedPositionTrackingChange();
            if (change != null) {
                change.detachPositions(buffer.getDocument());
            }
        }
    }

    public List<Suggestion<?>> findAffectedSuggestions(ITextFileBuffer buffer) {
        IPath location = buffer.getLocation();
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        IFile file = workspaceRoot.getFile(location);
        IPath projectRelativeLocation = file.getLocation();
        if (projectRelativeLocation != null) {
            return IncludatorPlugin.getSuggestionStore().findSuggestionsInFile(projectRelativeLocation.toOSString());
        } else {
            return new ArrayList<>(0);
        }
    }

    public void removeSuggestions(ITextFileBuffer buffer) {
        SuggestionStore suggestionStore = IncludatorPlugin.getSuggestionStore();
        for (Suggestion<?> curSuggestion : findAffectedSuggestions(buffer)) {
            suggestionStore.removeSuggestion(curSuggestion);
        }
    }

    public void persistatePositions(ITextFileBuffer buffer) {
        for (Suggestion<?> curSuggestion : findAffectedSuggestions(buffer)) {
            PositionTrackingChange change = curSuggestion.getContainedPositionTrackingChange();
            if (change != null) {
                change.persistatePositions();
            }
        }
    }

    class FileBufferListener implements IFileBufferListener {

        @Override
        public void bufferCreated(IFileBuffer buffer) {
            if (buffer instanceof ITextFileBuffer) {
                addPositionsToTrack((ITextFileBuffer) buffer);
            }
        }

        @Override
        public void bufferDisposed(IFileBuffer buffer) {
            if (buffer instanceof ITextFileBuffer) {
                removeTrackedPositions((ITextFileBuffer) buffer);
            }
        }

        @Override
        public void bufferContentAboutToBeReplaced(IFileBuffer buffer) {}

        @Override
        public void bufferContentReplaced(final IFileBuffer buffer) {
            IncludatorJob job = new IncludatorJob("Remove Markers", IncludatorPlugin.getActiveWorkbenchWindow()) {

                @Override
                public IStatus runWithWorkbenchWindow(IProgressMonitor monitor) {
                    if (buffer instanceof ITextFileBuffer) {
                        removeSuggestions((ITextFileBuffer) buffer);
                    }
                    return Status.OK_STATUS;
                }
            };
            job.schedule();
        }

        @Override
        public void stateChanging(IFileBuffer buffer) {}

        @Override
        public void dirtyStateChanged(IFileBuffer buffer, boolean isDirty) {
            if (!isDirty) {
                if (buffer instanceof ITextFileBuffer) {
                    persistatePositions((ITextFileBuffer) buffer);
                }
            }
        }

        @Override
        public void stateValidationChanged(IFileBuffer buffer, boolean isStateValidated) {}

        @Override
        public void underlyingFileMoved(IFileBuffer buffer, IPath path) {
            if (buffer instanceof ITextFileBuffer) {
                removeSuggestions((ITextFileBuffer) buffer);
            }
        }

        @Override
        public void underlyingFileDeleted(IFileBuffer buffer) {
            if (buffer instanceof ITextFileBuffer) {
                removeSuggestions((ITextFileBuffer) buffer);
            }
        }

        @Override
        public void stateChangeFailed(IFileBuffer buffer) {
            if (buffer instanceof ITextFileBuffer) {
                removeSuggestions((ITextFileBuffer) buffer);
            }
        }
    }

    public static void handleChangeCreated(PositionTrackingChange change) {
        ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
        IFile file = change.getIFile();
        if (file == null) {
            return;
        }
        IFileBuffer buffer = manager.getFileBuffer(file.getFullPath(), LocationKind.IFILE);
        if (buffer != null && buffer instanceof ITextFileBuffer) {
            change.attachPositions(((ITextFileBuffer) buffer).getDocument());
        }
    }

    public static void handleChangeDisposed(PositionTrackingChange change) {
        ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
        IFile file = change.getIFile();
        if (file == null) {
            return;
        }
        IFileBuffer buffer = manager.getFileBuffer(file.getFullPath(), LocationKind.IFILE);
        if (buffer != null && buffer instanceof ITextFileBuffer) {
            change.detachPositions(((ITextFileBuffer) buffer).getDocument());
        }
    }
}
