/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.helpers;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;
import org.eclipse.ui.texteditor.ITextEditor;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.optimizer.Suggestion;
import com.cevelop.includator.resources.IncludatorProject;
import com.cevelop.includator.resources.IncludatorWorkspace;
import com.cevelop.includator.ui.Markers;
import com.cevelop.includator.ui.helpers.IncludatorEclipseUIHelper;


public class MarkerHelper {

    public static void removeAllIncludatorMarkers(IncludatorWorkspace workspace) {
        for (IncludatorProject curProject : workspace.getAllProjects()) {
            try {
                curProject.getCProject().getProject().deleteMarkers(Markers.INCLUDATOR_MARKER, true, IResource.DEPTH_INFINITE);
            } catch (CoreException e) {
                throw new IncludatorException(e);
            }
        }
    }

    public static void addMarkers(Collection<Suggestion<?>> suggestions) throws CoreException {
        for (Suggestion<?> suggestion : suggestions) {
            IFile file = suggestion.getIFile();
            if (file == null) {
                String msg = "Failed to add marker to project-external file '" + suggestion.getAbsoluteFilePath() + "'.";
                String smartFilePath = FileHelper.getSmartFilePath(suggestion.getAbsoluteFilePath(), suggestion.getProjectRelativePath());
                IncludatorPlugin.logStatus(new IncludatorStatus(msg), smartFilePath);
                continue;
            }
            IMarker marker = file.createMarker(suggestion.getMarkerType());
            suggestion.setMarkerProperties(marker);
        }
    }

    public static int getMarkerStartOffset(IMarker marker, IWorkbenchWindow activeWorkbenchWindow) throws CoreException {
        if (activeWorkbenchWindow == null) {
            return -2;
        }
        Position markerPosition = getMarkerPosition(marker, activeWorkbenchWindow);
        if (markerPosition != null) {
            return markerPosition.getOffset();
        }
        Object attr = marker.getAttribute(IMarker.CHAR_START);
        if (attr == null) {
            return -2; // -2 so that getMarkerStartOffset() - getMarkerEndOffset() != 0
        }
        return (Integer) attr;
    }

    public static int getMarkerEndOffset(IMarker marker, IWorkbenchWindow activeWorkbenchWindow) throws CoreException {
        if (activeWorkbenchWindow == null) {
            return -1;
        }
        Position markerPosition = getMarkerPosition(marker, activeWorkbenchWindow);
        if (markerPosition != null) {
            return markerPosition.getOffset() + markerPosition.getLength();
        }
        Object attr = marker.getAttribute(IMarker.CHAR_END);
        if (attr == null) {
            return -1;
        }
        return (Integer) attr;
    }

    public static int getMarkerStartOffset(IMarker marker) throws CoreException {
        return getMarkerStartOffset(marker, IncludatorPlugin.getActiveWorkbenchWindow());
    }

    public static int getMarkerEndOffset(IMarker marker) throws CoreException {
        return getMarkerEndOffset(marker, IncludatorPlugin.getActiveWorkbenchWindow());
    }

    private static Position getMarkerPosition(IMarker marker, IWorkbenchWindow activeWorkbenchWindow) {
        ITextEditor editor = IncludatorEclipseUIHelper.getEditor(marker.getResource().getLocationURI(), activeWorkbenchWindow);
        if (editor != null) {
            IAnnotationModel model = editor.getDocumentProvider().getAnnotationModel(editor.getEditorInput());
            if (model instanceof AbstractMarkerAnnotationModel) {
                AbstractMarkerAnnotationModel markerModel = (AbstractMarkerAnnotationModel) model;
                Position position = markerModel.getMarkerPosition(marker);
                if (position != null && !position.isDeleted()) {
                    return position;
                }
            }
        }
        return null;
    }
}
