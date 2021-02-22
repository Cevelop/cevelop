/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.helpers;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeSelector;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorElifStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorFunctionStyleMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfdefStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfndefStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroExpansion;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorUndefStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.resources.IncludatorFile;


public class PreprocessorHelper {

    public static void addAllPreprocessorNames(RefMap list, IASTTranslationUnit translationUnit, IncludatorFile file, IASTFileLocation location) {
        for (IASTPreprocessorMacroExpansion actExpansion : translationUnit.getMacroExpansions()) {
            if (NodeHelper.isNodeContainingOther(location, actExpansion.getFileLocation())) {
                addMacroExpansionNames(list, file, actExpansion);
            }
        }
        for (IASTPreprocessorStatement actStatement : translationUnit.getAllPreprocessorStatements()) {
            if (!(actStatement instanceof IASTPreprocessorIncludeStatement)) {
                addAllPreprocessorNames(actStatement, list, file);
            }
        }
    }

    private static void addMacroExpansionNames(RefMap list, IncludatorFile file, IASTPreprocessorMacroExpansion expansion) {
        for (IASTName curNestedName : expansion.getNestedMacroReferences()) {
            if (shouldConsiderMacroName(curNestedName)) {
                IBinding binding = curNestedName.resolveBinding();
                list.put(binding, new DeclarationReference(binding, curNestedName, file));
            }
        }
        IASTName macroRef = expansion.getMacroReference();
        IBinding binding = macroRef.resolveBinding();
        list.put(binding, new DeclarationReference(binding, macroRef, file));
    }

    private static boolean shouldConsiderMacroName(IASTName name) {
        return !name.toString().startsWith("__"); // ignore compiler macros
    }

    public static void addAllPreprocessorNames(IASTNode node, RefMap list, IncludatorFile file) {
        if (node instanceof IASTPreprocessorIfdefStatement) {
            IASTPreprocessorIfdefStatement ifDefStatement = (IASTPreprocessorIfdefStatement) node;
            IASTName name = ifDefStatement.getMacroReference();
            processName(node, list, file, name);
        } else if (node instanceof IASTPreprocessorIfndefStatement) {
            IASTName name = ((IASTPreprocessorIfndefStatement) node).getMacroReference();
            processName(node, list, file, name);
        } else if (node instanceof IASTPreprocessorUndefStatement) {
            IASTName name = ((IASTPreprocessorUndefStatement) node).getMacroName();
            processName(node, list, file, name);
        } else if ((node instanceof IASTPreprocessorIfStatement) || (node instanceof IASTPreprocessorElifStatement)) {
            addAllChildPPNames(node, list, file);
        } else if (node instanceof IASTPreprocessorFunctionStyleMacroDefinition) {
            addAllChildPPNames(node, list, file);
        }
    }

    private static void processName(IASTNode node, RefMap list, IncludatorFile file, IASTName name) {
        if (name != null) {
            IBinding binding = name.resolveBinding();
            list.put(binding, new DeclarationReference(binding, name, file));
        }
    }

    private static void addAllChildPPNames(IASTNode node, RefMap list, IncludatorFile file) {
        if (!node.isPartOfTranslationUnitFile()) {
            return;
        }
        IASTNodeSelector nodeSelector = node.getTranslationUnit().getNodeSelector(null);
        int curOffset = node.getFileLocation().getNodeOffset();
        int endOffset = curOffset + node.getFileLocation().getNodeLength();
        IASTName curName = null;
        do {
            curName = nodeSelector.findFirstContainedName(curOffset, endOffset - curOffset);
            if (curName != null) {
                if (curName.getParent() instanceof IASTPreprocessorMacroExpansion) {
                    addMacroExpansionNames(list, file, (IASTPreprocessorMacroExpansion) curName.getParent());
                } else {
                    final IBinding nameBinding = curName.resolveBinding();
                    if (list.containsKey(nameBinding)) {
                        break;
                    }
                    list.put(nameBinding, new DeclarationReference(nameBinding, curName, file));
                }
                curOffset = curName.getFileLocation().getNodeOffset() + curName.getFileLocation().getNodeLength();
            }
        } while (curName != null);
    }

}
