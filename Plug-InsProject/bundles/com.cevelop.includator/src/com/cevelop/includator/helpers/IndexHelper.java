package com.cevelop.includator.helpers;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ClassTypeHelper;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.includator.cxxelement.MethodDeclarationReference;
import com.cevelop.includator.resources.IncludatorProject;


@SuppressWarnings("restriction")
public class IndexHelper {

    public static ICPPMethod[] findOverriders(MethodDeclarationReference methodRef, IncludatorProject project) throws DOMException, CoreException {
        return ClassTypeHelper.findOverriders(project.getIndex(), methodRef.getMethodBinding());
    }
}
