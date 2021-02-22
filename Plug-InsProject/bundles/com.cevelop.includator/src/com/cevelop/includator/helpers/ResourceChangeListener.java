package com.cevelop.includator.helpers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.PlatformUI;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.ui.Markers;


public class ResourceChangeListener implements IResourceDeltaVisitor {

    public ResourceChangeListener() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IResourceChangeListener listener = event -> {
            try {
                IResourceDelta delta = event.getDelta();
                if (delta != null) {
                    delta.accept(ResourceChangeListener.this);
                }
            } catch (CoreException e) {
                String path = event.getResource().getFullPath().toOSString();
                IncludatorPlugin.logStatus(new IncludatorStatus("Exception while observing resource change.", e), path);
            }
        };
        workspace.addResourceChangeListener(listener);
    }

    @Override
    public boolean visit(IResourceDelta delta) throws CoreException {
        processMarkerDeltas(delta.getMarkerDeltas());
        return true;
    }

    private void processMarkerDeltas(IMarkerDelta[] markerDeltas) throws CoreException {
        for (IMarkerDelta curDelta : markerDeltas) {
            if (curDelta.getKind() == IResourceDelta.CHANGED && curDelta.getMarker().isSubtypeOf(Markers.INCLUDATOR_MARKER)) {
                checkMarker(curDelta.getMarker());
            }
        }
    }

    private void checkMarker(final IMarker marker) throws CoreException {
        if (isExistingEmptyRemoveIncludeMarker(marker)) {
            PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
                try {
                    marker.delete();
                } catch (CoreException e) {
                    IncludatorPlugin.logStatus(new IncludatorStatus("Failed to remove Includator marker.", e), marker.getResource().getFullPath()
                            .toOSString());
                }
                IncludatorPlugin.getSuggestionStore().removeSuggestion(marker);
            });
        }
    }

    private boolean isExistingEmptyRemoveIncludeMarker(IMarker marker) throws CoreException {
      //@formatter:off
		return 	marker.exists() &&
				(
					marker.isSubtypeOf(Markers.INCLUDATOR_UNUSED_INCLUDE_MARKER) ||
					marker.isSubtypeOf(Markers.REPLACE_INCLUDE_WITH_FWD_MARKER)
				) &&
				MarkerHelper.getMarkerStartOffset(marker) - MarkerHelper.getMarkerEndOffset(marker) == 0;
		//@formatter:on
    }
}
