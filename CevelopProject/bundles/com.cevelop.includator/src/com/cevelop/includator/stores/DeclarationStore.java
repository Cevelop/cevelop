/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.stores;

import java.util.HashMap;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;

import com.cevelop.includator.cxxelement.Declaration;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.resources.IncludatorFile;


public class DeclarationStore {

    private final HashMap<StringIntegerPair, Declaration> declarations;

    public DeclarationStore() {
        declarations = new HashMap<>();
    }

    public Declaration getDeclaration(IName declarationNode, IncludatorFile file) {
        IASTFileLocation fileLocation = FileHelper.getFileLocation(declarationNode);
        StringIntegerPair key = new StringIntegerPair(fileLocation.getFileName(), fileLocation.getNodeOffset());
        if (!declarations.containsKey(key)) {
            declarations.put(key, new Declaration(declarationNode, file));
        }
        return declarations.get(key);
    }

    public void clear() {
        for (Declaration curDecl : declarations.values()) {
            curDecl.clear();
        }
        declarations.clear();
    }
}
