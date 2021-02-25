package com.cevelop.includator.ui.helpers;

import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.model.ICContainer;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;


public class PropertyTesterHelper {

    private static final List<String> fileExtensions = Arrays.asList("cpp", "CPP", "c", "C", "h", "H", "hpp", "HPP", "cc", "CC", "cxx", "CXX", "c++",
            "C++", "hh", "HH", "hxx", "HXX", "h++", "H++");

    public static boolean isCProjectRelatedItemSelected() {
        final ISelection selection = getSelection();
        if (selection != null) {
            if (selection instanceof IStructuredSelection) {
                final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
                final Object[] selectedElements = structuredSelection.toArray();
                if (selectedElements.length != 1) {
                    return false;
                } else {
                    Object selectedElement = selectedElements[0];
                    if (selectedElement instanceof ITranslationUnit || selectedElement instanceof ITextSelection) {
                        return true;
                    }
                    if (selectedElement instanceof IProject) {
                        IProject selectedProject = (IProject) selectedElement;
                        if (selectedProject.isAccessible()) {
                            try {
                                return selectedProject.hasNature("org.eclipse.cdt.core.cnature");
                            } catch (CoreException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (selectedElement instanceof ICContainer || selectedElement instanceof ICProject) {
                        return true;
                    }
                    if (selectedElement instanceof IResource) {
                        IResource selectedResource = (IResource) selectedElement;
                        String fileExtension = selectedResource.getFileExtension();
                        return fileExtensions.contains(fileExtension);
                    }
                    return false;
                }
            }
            if (selection instanceof ITextSelection) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCProjectSelected() {
        final ISelection selection = getSelection();
        if (selection != null) {
            if (selection instanceof IStructuredSelection) {
                final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
                final Object[] selectedElements = structuredSelection.toArray();
                if (selectedElements.length != 1) {
                    return false;
                } else {
                    Object selectedElement = selectedElements[0];
                    if (selectedElement instanceof ITranslationUnit || selectedElement instanceof ITextSelection) {
                        return true;
                    }
                    if (selectedElement instanceof IProject) {
                        IProject selectedProject = (IProject) selectedElement;
                        if (selectedProject.isAccessible()) {
                            try {
                                return selectedProject.hasNature("org.eclipse.cdt.core.cnature");
                            } catch (CoreException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (selectedElement instanceof ICContainer || selectedElement instanceof ICProject) {
                        return true;
                    }
                    return false;
                }
            }
            if (selection instanceof ITextSelection) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCProjectRelatedItemInProjectExplorerSelected() {
        final ISelection selection = getSelection();
        if (selection != null) {
            if (selection instanceof IStructuredSelection) {
                final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
                final Object[] selectedElements = structuredSelection.toArray();
                if (selectedElements.length != 1) {
                    return false;
                } else {
                    Object selectedElement = selectedElements[0];
                    if (selectedElement instanceof ITranslationUnit) {
                        return true;
                    }
                    if (selectedElement instanceof IProject) {
                        IProject selectedProject = (IProject) selectedElement;
                        if (selectedProject.isAccessible()) {
                            try {
                                return selectedProject.hasNature("org.eclipse.cdt.core.cnature");
                            } catch (CoreException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (selectedElement instanceof ICContainer || selectedElement instanceof ICProject) {
                        return true;
                    }
                    if (selectedElement instanceof IResource) {
                        IResource selectedResource = (IResource) selectedElement;
                        String fileExtension = selectedResource.getFileExtension();
                        return fileExtensions.contains(fileExtension);
                    }
                    return false;
                }
            }
        }
        return false;
    }

    private static ISelection getSelection() {
        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            return window.getSelectionService().getSelection();
        } else {
            return null;
        }
    }

}
