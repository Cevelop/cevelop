/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.startingpoints;

import org.eclipse.cdt.core.model.ICContainer;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;

import com.cevelop.includator.helpers.IncludatorException;


public class AlgorithmStartingPointFactory {

    public static AlgorithmStartingPoint getStartingPoint(ISelection selection, IWorkbenchWindow window) {
        AlgorithmStartingPoint result = null;
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;
            Object firstElement = structuredSelection.getFirstElement();
            if (firstElement instanceof ITranslationUnit) {
                result = new TranslationUnitStartingPoint(window, (ITranslationUnit) firstElement);
            } else if (firstElement instanceof IFile) {
                result = new IFileStartingPoint(window, (IFile) firstElement);
            } else if (firstElement instanceof IProject) {
                result = new IProjectStartingPoint(window, (IProject) firstElement);
            } else if (firstElement instanceof ICProject) {
                CProjectStartingPoint cProjectStartingPoint = new CProjectStartingPoint(window);
                cProjectStartingPoint.setCProject((ICProject) firstElement);
                result = cProjectStartingPoint;
            } else if (firstElement instanceof ICContainer) {
                result = new CContainerStartingPoint(window, (ICContainer) firstElement);
            }
        } else if (selection instanceof ITextSelection) {
            result = new ActiveEditorStartingPoint(window);
        }
        if (result == null) {
            throw new IncludatorException("Failed to create starting point for selection " + selection.toString());
        }
        return result;
    }
}
