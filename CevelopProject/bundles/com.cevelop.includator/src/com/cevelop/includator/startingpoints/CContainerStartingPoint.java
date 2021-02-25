package com.cevelop.includator.startingpoints;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.model.ICContainer;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICElementVisitor;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchWindow;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.resources.IncludatorProject;


public class CContainerStartingPoint extends AlgorithmStartingPoint {

    private ICContainer container;

    public CContainerStartingPoint(IWorkbenchWindow window, ICContainer container) {
        super(window);
        this.container = container;
    }

    @Override
    public IncludatorProject getProject() {
        return getProject(container);
    }

    private IncludatorProject getProject(ICElement container) {
        ICElement parent = container.getParent();
        if (!(parent instanceof ICProject)) {
            return getProject(parent);
        }
        return IncludatorPlugin.getWorkspace().getProject((ICProject) parent);
    }

    @Override
    public List<IncludatorFile> getAffectedFiles() {
        final IncludatorProject project = getProject();
        final List<IncludatorFile> result = new ArrayList<>();
        ICElementVisitor cElementVisitor = element -> {
            if (element.getResource().getName().startsWith(".")) {
                return false;
            }
            switch (element.getElementType()) {
            case ICElement.C_CCONTAINER:
                return true;
            case ICElement.C_UNIT:
                result.add(project.getFile(element.getLocationURI()));
            }
            return false;
        };
        try {
            container.accept(cElementVisitor);
        } catch (CoreException e) {
            throw new IncludatorException(e);
        }
        return result;
    }

    @Override
    public IncludatorFile getFile() {
        return null;
    }

    @Override
    public void clean() {
        container = null;
    }

    @Override
    public AlgorithmScope getScope() {
        return AlgorithmScope.CONTAINER_SCOPE;
    }

    @Override
    public IResource getAffectedResource() {
        return container.getResource();
    }

    @Override
    public String getAffectedResourceName() {
        return getAffectedResource().getName();
    }
}
