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
import java.util.Iterator;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTImplicitName;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.helpers.DeclarationPicker;
import com.cevelop.includator.helpers.DeclarationReferenceHelper;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.helpers.IncludatorStatus;
import com.cevelop.includator.helpers.NodeHelper;
import com.cevelop.includator.resources.IncludatorFile;


public class DeclarationReference {

    protected IASTNode                             declarationReferenceNode;
    protected IncludatorFile                       file;
    protected List<DeclarationReferenceDependency> allDependencies;
    protected boolean                              problemWhileResolving;
    protected boolean                              isFrowardDeclEnough;
    protected List<DeclarationReferenceDependency> includedDependencies;
    protected List<DeclarationReferenceDependency> requiredDependencies;
    protected List<DeclarationReferenceDependency> definitionDependencies;
    protected boolean                              isOnlyReferenceName;
    protected boolean                              isOnlyDeclarationName;
    protected boolean                              isDefinitionName;
    protected boolean                              isQualifiedName;
    protected boolean                              isUnqualifiedName;
    protected final IBinding                       binding;

    public DeclarationReference(IBinding binding, IASTNode declarationReferenceNode, IncludatorFile file) {
        this.binding = binding;
        this.declarationReferenceNode = declarationReferenceNode;
        this.file = file;
        initFlags(declarationReferenceNode);
    }

    protected void initFlags(IASTNode declarationReferenceNode) {
        problemWhileResolving = false;
        isOnlyReferenceName = false;
        isOnlyDeclarationName = false;
        isDefinitionName = false;
        isFrowardDeclEnough = true;
        isQualifiedName = false;
        isUnqualifiedName = false;
        combineMetaInfoOfNodeWith(declarationReferenceNode);
    }

    public void setIsForwardDeclEnough(boolean isFwdDeclEnough) {
        this.isFrowardDeclEnough = isFwdDeclEnough;
    }

    public IASTFileLocation getFileLocation() {
        return FileHelper.getNodeFileLocation(declarationReferenceNode);
    }

    public IncludatorFile getFile() {
        return file;
    }

    /**
     * @return list of all DeclarationReferenceDependencies. This can include several dependencies to the same type declaration in certain cases. (for
     * example one dependency to the definition and another to a forward declaration).
     */
    public List<DeclarationReferenceDependency> getAllDependencies() {
        if (allDependencies == null) {
            initAllDependencies();
        }
        return allDependencies;
    }

    protected void initAllDependencies() {
        try {
            allDependencies = DeclarationReferenceHelper.getDependencies(this);
            if (!isExceptionReference()) {
                problemWhileResolving = allDependencies.isEmpty();
            }
        } catch (IncludatorException e) {
            throw e; // pass IncludatorExceptions trough.
        } catch (Exception e) {
            problemWhileResolving = true;
            IncludatorPlugin.logStatus(new IncludatorStatus("Error while resolving declaration reference " + this, e), file);
            allDependencies = new ArrayList<>();
        }
    }

    private boolean isExceptionReference() {
        if (declarationReferenceNode.getClass().getCanonicalName().equals("org.eclipse.cdt.internal.core.parser.scanner.ASTMacroReferenceName")) {
            return true; // a used MacroReferenceName can be undefined. #ifdef is there to test this.
        } else if (getName().startsWith("operator new")) {
            return true; // not every new-operator can be resolved since only in special cases the new-operator is overloaded.
        } else if (getName().startsWith("operator delete")) {
            return true; // same as with new-operator
        } else if (getName().startsWith("operator delete")) {
            return true; // same as with new-operator
        }
        return false;
    }

    @Override
    public String toString() {
        String positionString = FileHelper.getExtendedPositionString(declarationReferenceNode);
        return getName() + " in " + file.getSmartPath() + positionString;
    }

    public IASTNode getASTNode() {
        return declarationReferenceNode;
    }

    public boolean hadProblemsWhileResolving() {
        boolean wasResolved = allDependencies != null;
        return wasResolved && problemWhileResolving;
    }

    public void clear() {
        declarationReferenceNode = null;
        file = null;
        if (allDependencies != null) {
            for (DeclarationReferenceDependency curDependency : allDependencies) {
                curDependency.clear();
            }
            allDependencies.clear();
            allDependencies = null;
        }
    }

    public boolean isForwardDeclarationEnough() {
        return isFrowardDeclEnough;
    }

    protected boolean evalIsForwardDeclEnough(IASTName name) {
        return NodeHelper.isForwardDeclarationEnough(name);
    }

    public void combineMetaInfoOfNodeWith(IASTNode otherNode) {
        if (otherNode instanceof IASTName) {
            IASTName otherName = (IASTName) otherNode;
            combineMetaInfoOfNameWith(otherName);
        } else if (otherNode instanceof IASTExpression) {
            IASTExpression otherExpression = (IASTExpression) otherNode;
            combineMetaInfoOfExpressionWith(otherExpression);
        }
    }

    public void combineMetaInfoOfExpressionWith(IASTExpression otherExpression) {
        isFrowardDeclEnough = isForwardDeclarationEnough() && evalIsForwardDeclEnough(otherExpression);
    }

    private boolean evalIsForwardDeclEnough(IASTExpression otherExpression) {
        return NodeHelper.isForwardDeclarationEnough(otherExpression, binding);
    }

    public void combineMetaInfoOfNameWith(IASTName otherName) {
        isOnlyReferenceName = isOnlyReferenceName || otherName.isReference();
        boolean isOtherDefinition = otherName.isDefinition();
        isDefinitionName = isDefinitionName || isOtherDefinition;
        isOnlyDeclarationName = isOnlyDeclarationName || NodeHelper.isOnlyDeclarationName(otherName);
        isFrowardDeclEnough = isForwardDeclarationEnough() && evalIsForwardDeclEnough(otherName);
        boolean isOtherQualifiedName = evalIsQualifiedName(otherName);
        isQualifiedName = isQualifiedName || isOtherQualifiedName;
        isUnqualifiedName = isUnqualifiedName || !isOtherQualifiedName;
        if (isOtherDefinition || declarationReferenceNode instanceof IASTExpression) {
            // prefer definition names before declaration- and reference-names and those before expressions
            declarationReferenceNode = otherName;
        }
    }

    public List<DeclarationReferenceDependency> getIncludedDependencies() {
        if (includedDependencies == null) {
            includedDependencies = new ArrayList<>();
            for (DeclarationReferenceDependency curDependency : getAllDependencies()) {
                if (curDependency.isLocalDependency() || (curDependency.getFirstLastElementIncludePaths().size() != 0)) {
                    includedDependencies.add(curDependency);
                }
            }
        }
        return includedDependencies;
    }

    public List<DeclarationReferenceDependency> getRequiredDependencies() {
        if (requiredDependencies == null) {
            requiredDependencies = new ArrayList<>();
            pickRequiredDependencies(filterNonLocalDependenciesToSourceFiles(getIncludedDependencies()));
            if (requiredDependencies.isEmpty()) {
                pickRequiredDependencies(filterNonLocalDependenciesToSourceFiles(getAllDependencies()));
            }
            if (requiredDependencies.isEmpty()) {
                pickRequiredDependencies(getAllDependencies());
            }
        }
        return requiredDependencies;
    }

    private List<DeclarationReferenceDependency> filterNonLocalDependenciesToSourceFiles(List<DeclarationReferenceDependency> candidates) {
        ArrayList<DeclarationReferenceDependency> result = new ArrayList<>(candidates);
        Iterator<DeclarationReferenceDependency> it = result.iterator();
        while (it.hasNext()) {
            DeclarationReferenceDependency curDependency = it.next();
            if (!curDependency.isLocalDependency() && !curDependency.getDeclaration().getFile().isHeaderFile()) {
                it.remove();
            }
        }
        return result;
    }

    private void pickRequiredDependencies(List<DeclarationReferenceDependency> candidates) {
        if (candidates.isEmpty()) {
            return;
        }
        List<DeclarationReferenceDependency> definitions = new ArrayList<>();
        DeclarationReferenceDependency declaration = initDefinitionsAndDeclarations(definitions, candidates);

        pickRequiredDependencies(definitions, declaration);
    }

    protected void initDefinitions(List<DeclarationReferenceDependency> definitions) {
        definitionDependencies = definitions;
    }

    protected void pickRequiredDependencies(List<DeclarationReferenceDependency> definitions, DeclarationReferenceDependency declaration) {
        if (!definitions.isEmpty() && (areAllLocalDefinitionDependencies(definitions) || areAllDefInHeaderUnit(definitions))) {
            handlePickRequiredFromDefs(definitions);
        } else if (declaration != null) {
            requiredDependencies.add(declaration);
        }
    }

    protected void handlePickRequiredFromDefs(List<DeclarationReferenceDependency> definitions) {
        if (definitions.size() >= 2) {
            handlePickRequiredFromMultipleDefs(definitions);
        } else {
            requiredDependencies.addAll(definitions);
        }
    }

    private void handlePickRequiredFromMultipleDefs(List<DeclarationReferenceDependency> definitions) {
        DeclarationReferenceDependency preferedDef = DeclarationPicker.pickBestFromDeclarations(definitions);
        requiredDependencies.add(preferedDef);
        if (preferedDef.getDeclarationReference().getBinding() instanceof IParameter) {
            return; // Don't log occurence of several parameter definitions. It is normal behaviour that there are several
        }
        String msg = "Found multiple definitions of " + preferedDef.getDeclarationReference() + ". Prefering " + preferedDef.getDeclaration() +
                     ". Candidates are:";
        MultiStatus multipleDefWarning = new MultiStatus(IncludatorPlugin.PLUGIN_ID, IncludatorStatus.STATUS_CODE_CUSTOM, msg, null);
        for (DeclarationReferenceDependency curDefDependency : definitions) {
            multipleDefWarning.add(new IncludatorStatus(IStatus.WARNING, curDefDependency.getDeclaration().getLocationStr()));
        }
        IncludatorPlugin.logStatus(multipleDefWarning, preferedDef.getDeclarationReference().getFile());
    }

    private DeclarationReferenceDependency initDefinitionsAndDeclarations(List<DeclarationReferenceDependency> definitions,
            List<DeclarationReferenceDependency> candidates) {
        ArrayList<DeclarationReferenceDependency> declarations = new ArrayList<>();
        for (DeclarationReferenceDependency curDependency : candidates) {
            if (curDependency.getDeclaration().isDefinition()) {
                definitions.add(curDependency);
            } else {
                declarations.add(curDependency);
            }
        }
        if (declarations.isEmpty()) {
            return null;
        }
        return pickBestWeightedDependency(declarations);
    }

    protected DeclarationReferenceDependency pickBestWeightedDependency(List<DeclarationReferenceDependency> declarations) {
        if (declarations.size() == 1) {
            return declarations.get(0);
        }
        return DeclarationPicker.pickBestFromDeclarations(declarations);
    }

    private boolean areAllDefInHeaderUnit(List<DeclarationReferenceDependency> definitions) {
        for (DeclarationReferenceDependency curDependency : definitions) {
            if (!curDependency.getDeclaration().getFile().isHeaderFile()) {
                return false;
            }
        }
        return true;
    }

    private boolean areAllLocalDefinitionDependencies(List<DeclarationReferenceDependency> definitions) {
        for (DeclarationReferenceDependency curDependency : definitions) {
            if (!curDependency.isLocalDependency()) {
                return false;
            }
        }
        return true;
    }

    /**
     * TODO: Fix this method!
     * 
     * If a IASTName is part of a qName, then often resolving to the definition is required because other parts of the qName needs to be resolvable as
     * well (which is given with the declaration) <br>
     * <br>
     * e.g. void "N::foo() { }". Here, N needs to be resolved as well, because otherwise the given code would be seen as independently correct, which
     * is not the case since the compiler results in a "unknown N" error.
     *
     * @param otherName Currently <b>UNUSED</b>!
     *
     * @return {@code true} iff {@link #declarationReferenceNode} is part of a qName
     */
    protected boolean evalIsQualifiedName(IASTName otherName) {
        return NodeHelper.findParentOfType(ICPPASTQualifiedName.class, declarationReferenceNode) != null;
    }

    public boolean isClassReference() {
        return false;
    }

    public List<DeclarationReferenceDependency> getDefinitionDependencies() {
        if (definitionDependencies == null) {
            ArrayList<DeclarationReferenceDependency> defs = new ArrayList<>();
            for (DeclarationReferenceDependency curDependency : getAllDependencies()) {
                if (curDependency.getDeclaration().isDefinition()) {
                    defs.add(curDependency);
                }
            }
            initDefinitions(defs);
        }
        return definitionDependencies;
    }

    /**
     * Returns the name of the current reference. This is in most cases the name of the encapsulated IASTName.
     *
     * @return the name of the current reference
     */
    public String getName() {
        if (declarationReferenceNode instanceof IASTExpression) {
            return ((IASTExpression) declarationReferenceNode).getRawSignature();
        }
        return declarationReferenceNode.toString();
    }

    public IBinding getBinding() {
        return binding;
    }

    public boolean isOnlyReferenceName() {
        return isOnlyReferenceName;
    }

    /**
     * Note here that this returns false if the current declRef is a definition. true is only returned on "pure" declarations (e.g function
     * declaration 'void foo();')
     *
     * @return {@link #isOnlyDeclarationName}
     */
    public boolean isOnlyDeclarationName() {
        return isOnlyDeclarationName;
    }

    public boolean isDefinitionName() {
        return isDefinitionName;
    }

    public boolean isNameQualifiedName() {
        return isQualifiedName;
    }

    public boolean isNameUnqualifiedName() {
        return isUnqualifiedName;
    }

    public boolean isImplicitRef() {
        return declarationReferenceNode instanceof IASTImplicitName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DeclarationReference)) {
            return false;
        }
        DeclarationReference other = (DeclarationReference) obj;
        return isDefinitionName == other.isDefinitionName && isFrowardDeclEnough == other.isFrowardDeclEnough &&
               isOnlyDeclarationName == other.isOnlyDeclarationName && isOnlyReferenceName == other.isOnlyReferenceName &&
               isQualifiedName == other.isQualifiedName && isUnqualifiedName == other.isUnqualifiedName &&
               problemWhileResolving == other.problemWhileResolving && getBinding().equals(other.getBinding());
    }

    @Override
    public int hashCode() {
        return getFileLocation().getNodeOffset();
    }

    public boolean isFunctionReference() {
        return false;
    }

}
