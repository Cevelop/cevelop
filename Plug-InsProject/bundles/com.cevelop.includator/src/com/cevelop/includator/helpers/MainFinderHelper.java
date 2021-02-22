/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.helpers;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.IParameter;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexManager;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.index.IndexFilter;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.resources.IncludatorProject;


public class MainFinderHelper {

    public static IASTFunctionDefinition findMain(IncludatorProject project) {
        ICProject cProject = project.getCProject();
        IIndexManager manager = CCorePlugin.getIndexManager();
        IIndex index = null;
        try {
            index = manager.getIndex(cProject);
            index.acquireReadLock();
            IBinding[] bindings = index.findBindings("main".toCharArray(), IndexFilter.ALL, null);
            return processBindings(project, index, bindings);
        } catch (CoreException e) {
            throw new IncludatorException(e);
        } catch (InterruptedException e) {
            throw new IncludatorException(e);
        } finally {
            if (index != null) {
                index.releaseReadLock();
            }
        }
    }

    private static IASTFunctionDefinition processBindings(IncludatorProject project, IIndex index, IBinding[] bindings) throws CoreException {
        for (IBinding curBinding : bindings) {
            if (curBinding instanceof IFunction) {
                IFunction ifunction = (IFunction) curBinding;
                if ((ifunction.getOwner() != null) || hasWrongParameter(ifunction) || hawWrongType(ifunction) || ifunction.isStatic()) {
                    continue;
                }
                IIndexName[] names = index.findNames(ifunction, IIndex.FIND_DEFINITIONS | IIndex.SEARCH_ACROSS_LANGUAGE_BOUNDARIES);
                if (names.length == 0) {
                    continue;
                }
                return prcessNames(names, project);
            }
        }
        return null;
    }

    private static IASTFunctionDefinition prcessNames(IIndexName[] names, IncludatorProject project) {
        for (IIndexName curName : names) {
            IncludatorFile targetFile = project.getFile(curName.getFileLocation().getFileName());
            IASTTranslationUnit translationUnit = targetFile.getTranslationUnit();
            if (translationUnit == null) {
                continue;
            }
            IASTFunctionDefinition def = processNamesInTu(curName, translationUnit);
            if (def != null) {
                return def;
            }
        }
        return null;
    }

    private static IASTFunctionDefinition processNamesInTu(IIndexName curName, IASTTranslationUnit translationUnit) {
        for (IASTDeclaration curDecl : translationUnit.getDeclarations()) {
            if (curDecl instanceof IASTFunctionDefinition) {
                IASTFunctionDefinition definition = (IASTFunctionDefinition) curDecl;
                if (nameEquals(curName, definition.getDeclarator().getName())) {
                    return definition;
                }
            }
        }
        return null;
    }

    private static boolean nameEquals(IName first, IName second) {
        return (first.getFileLocation().getNodeOffset() == second.getFileLocation().getNodeOffset()) && first.toString().equals(second.toString());
    }

    private static boolean hawWrongType(IFunction function) {
        String typeStr = function.getType().toString();
        return !typeStr.startsWith("int") && !typeStr.startsWith("void");
    }

    private static boolean hasWrongParameter(IFunction function) {
        if (function.takesVarArgs()) {
            return true;
        }
        IParameter[] params = function.getParameters();
        if (params.length == 0) {
            return false;
        } else if (params.length == 1) {
            IType paramType = params[0].getType();
            if (paramType instanceof IBasicType) {
                return !((IBasicType) paramType).getKind().equals(IBasicType.Kind.eVoid);
            }
        } else if ((params.length == 2) && params[0].getType().toString().equals("int")) {
            String secondParamString = params[1].getType().toString();
            secondParamString = secondParamString.replaceAll("\\s", "");
            if (secondParamString.equals("char**")) {
                return false;
            }
        }
        return true;
    }
}
