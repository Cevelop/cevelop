/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.cxxelement;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;

import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.resources.IncludatorFile;


public class NamespaceDeclarationReference extends DeclarationReference {

    private final ArrayList<String> definitionLocationHintLocations;

    public NamespaceDeclarationReference(IBinding binding, IASTNode node, IncludatorFile file) {
        super(binding, node, file);
        definitionLocationHintLocations = new ArrayList<>();
        isFrowardDeclEnough = false;
    }

    public void addDefinitionLocationHint(IASTFileLocation definitionLocationHint) {
        definitionLocationHintLocations.add(definitionLocationHint.getFileName());
    }

    @Override
    protected void pickRequiredDependencies(List<DeclarationReferenceDependency> definitions, DeclarationReferenceDependency declaration) {
        for (DeclarationReferenceDependency curDefinition : definitions) {
            String definitionFileName = curDefinition.getDeclaration().getFileLocation().getFileName();
            if (definitionLocationHintLocations.contains(definitionFileName)) {
                requiredDependencies.add(curDefinition);
            }
        }
        if (requiredDependencies.isEmpty()) {
            fallbackPickRequiredDependencies(definitions);
        }
    }

    private void fallbackPickRequiredDependencies(List<DeclarationReferenceDependency> definitions) {
        requiredDependencies.add(pickBestWeightedDependency(definitions));
    }

    @Override
    protected void initDefinitions(List<DeclarationReferenceDependency> definitions) {
        if (definitionDependencies == null) {
            definitionDependencies = new ArrayList<>();
        }
        for (DeclarationReferenceDependency curDefinition : definitions) {
            String definitionFileName = curDefinition.getDeclaration().getFileLocation().getFileName();
            if (definitionLocationHintLocations.contains(definitionFileName)) {
                definitionDependencies.add(curDefinition);
            }
        }
    }

    @Override
    protected boolean evalIsForwardDeclEnough(IASTName name) {
        return false;
    }

    @Override
    public void combineMetaInfoOfNameWith(IASTName otherName) {
        isOnlyReferenceName = isOnlyReferenceName || otherName.isReference();
        isDefinitionName = isDefinitionName || otherName.isDefinition();
        // ignore rest. meta-info accessors are all overwritten bellow
    }

    @Override
    public boolean isOnlyDeclarationName() {
        return false; // there is nothing like a namespace-froward-declaration...
    }
}
