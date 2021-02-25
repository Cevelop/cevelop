/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.optimizer.staticcoverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBase;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassTemplatePartialSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPField;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateInstance;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.cxxelement.FunctionDeclarationReference;
import com.cevelop.includator.cxxelement.MethodDeclarationReference;
import com.cevelop.includator.cxxelement.SpecialMemberFunctionDeclarationReference;
import com.cevelop.includator.cxxelement.SpecialMemberFunctionDeclarationReference.SpecialMemberFunctionKind;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.helpers.BindingHelper;
import com.cevelop.includator.helpers.DeclarationReferenceHelper;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.helpers.IncludatorStatus;
import com.cevelop.includator.helpers.IndexHelper;
import com.cevelop.includator.helpers.MainFinderHelper;
import com.cevelop.includator.helpers.NodeHelper;
import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.resources.IncludatorProject;


public class StaticCoverageProjectAnalysisAlgorithm extends Algorithm {

    private Collection<IASTNode>                            nodesInUse;
    private Collection<IASTNode>                            nodesImplicitlyInUse;
    private List<IASTNode>                                  nodesNotInUse;
    private Map<ICPPClassType, ClassDefinitionCoverageData> classDefCoverageDataList;
    private IncludatorFile                                  startingFile;
    private IncludatorProject                               project;
    private IIndex                                          index;

    @Override
    public void run() {
        project = startingPoint.getProject();
        index = project.getIndex();
        runCoverage();

        cleanupDuplicates();
        monitorWorked(0.4);
        createResult(startingFile);
    }

    private void cleanupDuplicates() {
        nodesNotInUse.removeAll(nodesInUse);
        nodesNotInUse.removeAll(nodesImplicitlyInUse);
        nodesImplicitlyInUse.removeAll(nodesInUse);
    }

    private void runCoverage() {
        classDefCoverageDataList = new LinkedHashMap<>();
        nodesInUse = new LinkedHashSet<>();
        nodesImplicitlyInUse = new LinkedHashSet<>();
        nodesNotInUse = new ArrayList<>();

        findInitiallyUsedNodes();
        initializeNodesNotInUse();

        monitorWorked(0.2);
        setProgressMonitorMessage("Static code coverage: collecting used declaration references");
        for (IASTNode node : new ArrayList<>(nodesInUse)) {
            addAllReferences(false, node);
        }
        monitorWorked(0.4);
    }

    private void recursiveProcessReferencesToResolve(DeclarationReference referenceToResolve, boolean isImplicit, boolean skipSelfReference) {
        setProgressMonitorMessage("Static code coverage: traversing declaration references (currently " + referenceToResolve.getName() + ").");
        if (!isParameterDependency(referenceToResolve)) {
            for (DeclarationReferenceDependency curDependency : referenceToResolve.getRequiredDependencies()) {
                try {
                    processResolvedDepencency(curDependency, isImplicit, skipSelfReference);
                } catch (IncludatorException e) {
                    IncludatorPlugin.log(e);
                }
            }
        }
    }

    private boolean isParameterDependency(DeclarationReference declRef) {
        return declRef.getBinding() instanceof ICPPParameter;
    }

    private void processResolvedDepencency(DeclarationReferenceDependency dependency, boolean addToImplicitlyUsed, boolean skipSelfReference) {
        if (skipSelfReference && dependency.isSelfDependency()) {
            return;
        }

        IASTNode match = findAndAddMatch(dependency.getDeclaration().getFileLocation(), addToImplicitlyUsed, false);
        if (match != null) {
            addAllReferences(addToImplicitlyUsed, match);
            addDefinitionForDeclaration(dependency, addToImplicitlyUsed);
        }
        processSpecialCases(dependency, addToImplicitlyUsed);
    }

    private void addDefinitionForDeclaration(DeclarationReferenceDependency dependency, boolean addToImplicitlyUsed) {
        boolean isPartOfProject = dependency.getDeclaration().getFile().isPartOfProject();
        if (isPartOfProject && !dependency.getDeclaration().isDefinition() && isFunctionRefOrDecl(dependency.getDeclarationReference())) {
            for (DeclarationReferenceDependency curDefinitionDependency : dependency.getDeclarationReference().getDefinitionDependencies()) {
                processResolvedDepencency(curDefinitionDependency, addToImplicitlyUsed, true);
            }
        }
    }

    private boolean isFunctionRefOrDecl(DeclarationReference ref) {
        return ref instanceof FunctionDeclarationReference && !ref.isDefinitionName();
    }

    /**
     * if the given <i>dependency</i> refers to a constructor or destructor, follow up default-constructors and destructors of non-pointer member vars
     * get added here to the <i>refList</i>.
     *
     * @param addToImplicitlyUsed
     * @param reference
     */
    private void processSpecialCases(DeclarationReferenceDependency dependency, boolean addToImplicitlyUsed) {
        final DeclarationReference declarationReference = dependency.getDeclarationReference();
        IBinding binding = declarationReference.getBinding();
        processConstrDestr(dependency);
        addOwningClass(binding, addToImplicitlyUsed);
        if (declarationReference instanceof MethodDeclarationReference) {
            processPolymorphicMethodCalls((MethodDeclarationReference) declarationReference, addToImplicitlyUsed);
        }
        addPrimaryClassTemplateDeclaration(binding, addToImplicitlyUsed);
    }

    private void addPrimaryClassTemplateDeclaration(IBinding binding, boolean addToImplicitlyUsed) {
        if (binding instanceof ICPPClassTemplatePartialSpecialization) {
            while (binding instanceof ICPPSpecialization) {
                binding = ((ICPPSpecialization) binding).getSpecializedBinding();
            }
            findAndAddMatch(BindingHelper.findBindingDefinitionLocation(binding, index), addToImplicitlyUsed, false);
        }
    }

    private void processPolymorphicMethodCalls(MethodDeclarationReference methodRef, boolean addToImplicitlyUsed) {
        if (methodRef.couldBePolymorphicMethodCall()) {
            try {
                for (ICPPMethod curOverrider : IndexHelper.findOverriders(methodRef, project)) {
                    processPolymorphicMethodCall(curOverrider, addToImplicitlyUsed);
                }
            } catch (DOMException e) {
                IncludatorPlugin.logStatus(new IncludatorStatus("Failed to add polymorpicly called methods while running static code coverage", e),
                        startingFile);
            } catch (CoreException e) {
                IncludatorPlugin.logStatus(new IncludatorStatus("Failed to add polymorpicly called methods while running static code coverage", e),
                        startingFile);
            }
        }
    }

    private void processPolymorphicMethodCall(ICPPMethod overridingMethod, boolean addToImplicitlyUsed) {
        try {
            IIndexName[] declNames = index.findNames(overridingMethod, IIndex.FIND_DECLARATIONS_DEFINITIONS |
                                                                       IIndex.SEARCH_ACROSS_LANGUAGE_BOUNDARIES);
            for (IIndexName declarationName : declNames) {
                IASTNode match = findAndAddMatch(declarationName.getFileLocation(), addToImplicitlyUsed, false);
                if (match != null) {
                    addAllReferences(addToImplicitlyUsed, match);
                }
            }
        } catch (CoreException e) {
            throw new IncludatorException(e);
        }

    }

    private void addOwningClass(IBinding binding, boolean addToImplicitlyUsed) {
        IBinding owner = binding.getOwner();
        if (owner instanceof ICPPClassType) {
            owner = BindingHelper.getMostSpecificDefiningClassBinding((ICPPClassType) owner, index);
            findAndAddMatch(BindingHelper.findBindingDefinitionLocation(owner, index), addToImplicitlyUsed, false);
        }
    }

    private void processConstrDestr(DeclarationReferenceDependency dependency) {
        final DeclarationReference declarationReference = dependency.getDeclarationReference();
        if (declarationReference instanceof SpecialMemberFunctionDeclarationReference) {

            IBinding binding = declarationReference.getBinding();
            ICPPClassType classBinding = (ICPPClassType) binding.getOwner();
            processClassFields(classBinding, declarationReference.getASTNode());

            SpecialMemberFunctionDeclarationReference specialMember = (SpecialMemberFunctionDeclarationReference) declarationReference;
            SpecialMemberFunctionKind kind = specialMember.getKind();
            if (dependency.resolvesToImplicitDeclaration()) {
                processFollowUpFields(kind, classBinding, declarationReference.getASTNode());
                processFollowUpBases(kind, classBinding, declarationReference.getASTNode());
            }
        }
    }

    private void processClassFields(ICPPClassType classBinding, IASTNode point) {
        for (ICPPField field : classBinding.getDeclaredFields()) {
            if (field.getType() instanceof ICPPClassType) {
                IASTNode fieldNode = findAndAddMatch(BindingHelper.findBindingDeclarationLocation(field, index), true, true);
                addAllReferences(true, fieldNode);
            }
        }
    }

    private void processFollowUpBases(SpecialMemberFunctionKind kind, ICPPClassType classBinding, IASTNode point) {
        for (ICPPBase curBase : classBinding.getBases()) {
            IBinding baseClass = curBase.getBaseClass();
            if (baseClass instanceof ICPPTemplateInstance) {
                baseClass = ((ICPPTemplateInstance) baseClass).getSpecializedBinding();
            }
            if (baseClass instanceof ICPPClassType) {
                processFollowUpClass((ICPPClassType) baseClass, kind);
            }
        }
    }

    private void processFollowUpClass(ICPPClassType classBinding, SpecialMemberFunctionKind kind) {
        classBinding = BindingHelper.getMostSpecificDefiningClassBinding(classBinding, index);
        IASTNode baseClassNode = findAndAddMatch(BindingHelper.findBindingDefinitionLocation(classBinding, index), true, true);
        if (baseClassNode == null) { // TODO: Fix emergency exit when classBinding could not be resolved.
            return;
        }
        ClassDefinitionCoverageData classData = getClassDefinitionCoverageData(classBinding, baseClassNode);

        for (DeclarationReference curRef : classData.getDeclRefs()) {
            if (isOfKindKind(curRef, kind)) {
                if (classData.shouldCoverConstrOrDestr(curRef)) {
                    recursiveProcessReferencesToResolve(curRef, true, false);
                    classData.setCovered(curRef);
                }
            }
        }
    }

    private boolean isOfKindKind(DeclarationReference reference, SpecialMemberFunctionKind kind) {
        if (reference instanceof SpecialMemberFunctionDeclarationReference) {
            return ((SpecialMemberFunctionDeclarationReference) reference).getKind() == kind;
        }
        return false;
    }

    private ClassDefinitionCoverageData getClassDefinitionCoverageData(ICPPClassType classBinding, IASTNode classDefinitionNode) {
        if (!classDefCoverageDataList.containsKey(classBinding)) {
            IncludatorFile file = project.getFile(classDefinitionNode.getFileLocation().getFileName());
            classDefCoverageDataList.put(classBinding, new ClassDefinitionCoverageData(classDefinitionNode, file));
        }
        return classDefCoverageDataList.get(classBinding);
    }

    private void processFollowUpFields(SpecialMemberFunctionKind kind, ICPPClassType classBinding, IASTNode point) {
        for (ICPPField curField : classBinding.getDeclaredFields()) {
            if (!curField.isStatic()) {
                processFollowUpField(curField, kind);
            }
        }
    }

    private void processFollowUpField(ICPPField field, SpecialMemberFunctionKind kind) {
        IType fieldType = field.getType();
        if (fieldType instanceof ICPPClassType) {
            ICPPClassType fieldClassType = (ICPPClassType) fieldType;
            processFollowUpClass(fieldClassType, kind);
        }
    }

    private IASTNode findAndAddMatch(IASTFileLocation fileLocation, boolean addToImplicitlyUsed, boolean alsoFindInUsed) {
        IASTNode match = findMatchIn(fileLocation, nodesNotInUse);
        Collection<IASTNode> removeCandidates = (match != null) ? nodesNotInUse : null;
        if (match == null) {
            match = findMatchIn(fileLocation, nodesImplicitlyInUse);
            removeCandidates = (match != null && !addToImplicitlyUsed) ? nodesImplicitlyInUse : null;
        }
        if (match != null) {
            if (removeCandidates != null) {
                removeCandidates.remove(match);
                if (addToImplicitlyUsed) {
                    nodesImplicitlyInUse.add(match);
                } else {
                    nodesInUse.add(match);
                }
            }
        }
        if (alsoFindInUsed && match == null) {
            match = findMatchIn(fileLocation, nodesInUse);
        }
        return match;
    }

    private IASTNode findMatchIn(IASTFileLocation fileLocation, Collection<IASTNode> candidates) {
        IASTNode match = null;
        for (IASTNode curNode : candidates) {
            if (NodeHelper.isNodeContainingOther(curNode.getFileLocation(), fileLocation)) {
                match = curNode;
                break;
            }
        }
        return match;
    }

    private void addAllReferences(boolean addToImplicitlyUsed, IASTNode nodeToProcess) {
        if (nodeToProcess != null && !isClassNode(nodeToProcess)) {
            IncludatorFile file = project.getFile(nodeToProcess.getFileLocation().getFileName());
            for (DeclarationReference curRef : DeclarationReferenceHelper.findDeclReferences(nodeToProcess, file)) {
                recursiveProcessReferencesToResolve(curRef, addToImplicitlyUsed || curRef.isImplicitRef(), true);
            }
        }
    }

    private boolean isClassNode(IASTNode node) {
        if (node instanceof ICPPASTTemplateDeclaration) {
            node = ((ICPPASTTemplateDeclaration) node).getDeclaration();
        }
        return (node instanceof IASTSimpleDeclaration && ((IASTSimpleDeclaration) node).getDeclSpecifier() instanceof ICPPASTCompositeTypeSpecifier);
    }

    private void findInitiallyUsedNodes() {
        IASTFunctionDefinition main = MainFinderHelper.findMain(project);
        if (main != null) {
            startingFile = project.getFile(main.getFileLocation().getFileName());
            setProgressMonitorMessage("Static code coverage: \"main\" found in " + startingFile.getProjectRelativePath() + ".");
            nodesInUse.add(main);
            addGlobalsToNodesInUse();
        } else {
            setProgressMonitorMessage("Static code coverage: no \"main\" function found. Running in library mode.");
            for (IncludatorFile curFile : project.getAffectedFiles()) {
                if (!curFile.isHeaderFile()) {
                    findNodesInUseInSourceFile(curFile);
                }
            }
        }
    }

    private void addGlobalsToNodesInUse() {
        nodesInUse.addAll(new GlobalDeclaratorLocator(project).getGlobalDeclarations());
    }

    private void findNodesInUseInSourceFile(IncludatorFile file) {
        addAllDeclarations(file, nodesInUse);
    }

    private void addAllDeclarations(IncludatorFile file, Collection<IASTNode> list) {
        IASTTranslationUnit tu = file.getTranslationUnit();
        if (tu != null) {
            tu.accept(new CoverageDeclarationLocator(list));
        }
    }

    private void initializeNodesNotInUse() {
        for (IncludatorFile curFile : project.getAffectedFiles()) {
            setProgressMonitorMessage("Static code coverage: collecting declarations in " + curFile.getProjectRelativePath());
            addAllDeclarations(curFile, nodesNotInUse);
        }
        Collections.reverse(nodesNotInUse);
        nodesNotInUse.removeAll(nodesInUse);
    }

    private void createResult(IncludatorFile activeFile) {
        createCodeInUseAnnotations(activeFile);
        createImplicitlyCodeInUseAnnotations(activeFile);
        createCodeNotInUseAnnotations(activeFile);
    }

    private void createCodeInUseAnnotations(IncludatorFile activeFile) {
        String inUseMessage = "This declaration is in use" + ((activeFile != null) ? (" through the file " + activeFile.getProjectRelativePath())
                                                                                   : "") + ".";
        for (IASTNode curNode : nodesInUse) {
            IASTFileLocation location = getLocationToMark(curNode);
            IncludatorFile file = project.getFile(location.getFileName());
            addSuggestion(new CodeInUseSuggestion(location.getNodeOffset(), location.getNodeLength(), inUseMessage, file,
                    StaticCoverageProjectAnalysisAlgorithm.class));
        }
    }

    private void createImplicitlyCodeInUseAnnotations(IncludatorFile activeFile) {
        String inUseMessage = "This declaration is implicitly in use" + ((activeFile != null) ? (" through the file " + activeFile
                .getProjectRelativePath()) : "") + ".";
        for (IASTNode curNode : nodesImplicitlyInUse) {
            IASTFileLocation location = getLocationToMark(curNode);
            IncludatorFile file = project.getFile(location.getFileName());
            addSuggestion(new CodeImplicitlyInUseSuggestion(location.getNodeOffset(), location.getNodeLength(), inUseMessage, file,
                    StaticCoverageProjectAnalysisAlgorithm.class));
        }
    }

    private void createCodeNotInUseAnnotations(IncludatorFile activeFile) {
        String notInUseMessage = "This declaration is not in use" + ((activeFile != null) ? (" through the file " + activeFile
                .getProjectRelativePath()) : "") + ".";
        for (IASTNode curNode : nodesNotInUse) {
            IASTFileLocation location = getLocationToMark(curNode);
            IncludatorFile file = project.getFile(location.getFileName());
            addSuggestion(new CodeNotInUseSuggestion(location.getNodeOffset(), location.getNodeLength(), notInUseMessage, file,
                    StaticCoverageProjectAnalysisAlgorithm.class));
        }
    }

    private IASTFileLocation getLocationToMark(IASTNode node) {
        if (node instanceof ICPPASTTemplateDeclaration) {
            node = ((ICPPASTTemplateDeclaration) node).getDeclaration();
        }
        if (node instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration simpleDecl = (IASTSimpleDeclaration) node;
            if (simpleDecl.getDeclSpecifier() instanceof IASTCompositeTypeSpecifier) {
                IASTCompositeTypeSpecifier typeNode = (IASTCompositeTypeSpecifier) simpleDecl.getDeclSpecifier();
                IASTFileLocation nameLocation = typeNode.getName().getFileLocation();
                IASTFileLocation nodeLocation = typeNode.getFileLocation();

                if (nameLocation == null) {
                    return NodeHelper.createCroppedFileLocation(nodeLocation, getCompositeTypeKeywordLength(typeNode.getKey()));
                } else {
                    return NodeHelper.createCroppedFileLocation(nodeLocation, nameLocation.getNodeOffset() + nameLocation.getNodeLength() -
                                                                              nodeLocation.getNodeOffset());
                }
            }
        }

        return FileHelper.getNodeFileLocation(node);
    }

    private int getCompositeTypeKeywordLength(int key) {
        switch (key) {
        case IASTCompositeTypeSpecifier.k_struct:
            return 6;
        case IASTCompositeTypeSpecifier.k_union:
            return 5;
        case ICPPASTCompositeTypeSpecifier.k_class:
            return 5;
        default:
            throw new IllegalArgumentException("Unknow Specifiertype: " + key);
        }
    }

    @Override
    public String getInitialProgressMonitorMessage(String resourceName) {
        return "Running include optimization: Static code coverage.";
    }

    @Override
    public Set<Class<? extends Algorithm>> getInvolvedAlgorithmTypes() {
        HashSet<Class<? extends Algorithm>> involvedAlgorithms = new HashSet<>();
        involvedAlgorithms.add(StaticCoverageProjectAnalysisAlgorithm.class);
        return involvedAlgorithms;
    }

    @Override
    public AlgorithmScope getScope() {
        return AlgorithmScope.PROJECT_SCOPE;
    }
}
