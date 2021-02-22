package com.cevelop.includator.optimizer.includestofwd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.c.ICExternalBinding;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.core.index.IIndexFile;
import org.eclipse.cdt.core.index.IIndexInclude;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.cxxelement.Declaration;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.dependency.FirstLastElementIncludePath;
import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.helpers.IncludatorStatus;
import com.cevelop.includator.helpers.IncludeHelper;
import com.cevelop.includator.optimizer.Algorithm;
import com.cevelop.includator.optimizer.AlgorithmScope;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.resources.IncludatorProject;


public class ReplaceIncludesWithFwdAlgorithm extends Algorithm {

    private IncludatorProject project;
    private IncludatorFile    file;

    @Override
    protected void run() {
        file = startingPoint.getFile();
        project = startingPoint.getProject();
        Collection<DeclarationReference> declRefs = file.getDeclarationReferences();
        List<IASTPreprocessorIncludeStatement> includes = file.getIncludes();
        DeclRefDepsGroup declRefsGrouped = groupDeclRefsByIncludes(declRefs, includes);
        createSuggestionsForReplacableDeclRefs(declRefsGrouped);
    }

    private void createSuggestionsForReplacableDeclRefs(DeclRefDepsGroup declRefsGrouped) {
        for (Entry<IncludatorFile, List<DeclRefDepBoolPair>> curEntry : declRefsGrouped.entrySet()) {
            List<DeclarationReferenceDependency> insertFwdCandidates = new ArrayList<>();
            boolean replacePossible = findInsertFwdCandidates(curEntry.getValue(), insertFwdCandidates);
            if (replacePossible && !insertFwdCandidates.isEmpty()) {
                if (checkShouldReplace(curEntry)) {
                    addReplaceSuggestionFor(curEntry.getKey(), insertFwdCandidates);
                }
            }
        }
    }

    private boolean checkShouldReplace(Entry<IncludatorFile, List<DeclRefDepBoolPair>> candidate) {
        return checkNoExternalBinding(candidate.getValue()) && checkDeclarationASTNodesExist(candidate);
    }

    private boolean checkNoExternalBinding(List<DeclRefDepBoolPair> dependencies) {
        for (DeclRefDepBoolPair candidate : dependencies) {
            if (isExternalSDKFunctionReference(candidate.dependency)) {
                return false;
            }
        }
        return true;
    }

    /**
     * duplicated (and adapted) from ExternalSDKHighlighting.isExternalSDKReference() (which is private and non-static)
     */
    private boolean isExternalSDKFunctionReference(DeclarationReferenceDependency dependency) {
        IBinding binding = dependency.getDeclarationReference().getBinding();
        if (binding instanceof IFunction) {
            try {
                if (binding instanceof IIndexBinding) {
                    if (((IIndexBinding) binding).isFileLocal()) {
                        return false;
                    }
                } else if (!(binding instanceof ICExternalBinding)) {
                    return false;
                }
                IIndexFile indexFile = FileHelper.getIndexFile(dependency.getDeclaration().getFile());
                return indexFile == null || indexFile.getLocation().getFullPath() == null;
            } catch (CoreException e) {
                IncludatorPlugin.logStatus(new IncludatorStatus("Failed to call isFileLocal() on binding of " + dependency.getDeclarationReference() +
                                                                ".", e), file);
            }
        }
        return false;
    }

    private boolean checkDeclarationASTNodesExist(Entry<IncludatorFile, List<DeclRefDepBoolPair>> candidate) {
        for (DeclRefDepBoolPair curDependency : candidate.getValue()) {
            Declaration decl = curDependency.dependency.getDeclaration();
            if (decl.getASTName() == null) {
                // This might happen if (1) the name dependency points to an inactive declaration or (2) the binding of curDependency is a
                // ICPPFunction whereas the its of name in the AST is a IFunction.
                // examples for this can be found in tone.c of the doom3 source code. Found no way to reproduce to make this testable.
                IncludatorFile fileToReplace = candidate.getKey();
                IASTPreprocessorIncludeStatement astInclude = IncludeHelper.findAstIncludeStatement(file, fileToReplace);
                String msg = "Failed to replace include to " + astInclude.getName() +
                             " with forward declaration(s) because AST node for declaration " + decl + " cannot be found.";
                IncludatorPlugin.logStatus(new IncludatorStatus(msg), file);
                return false;
            }
        }
        return true;
    }

    private void addReplaceSuggestionFor(IncludatorFile includedFileToRemove, List<DeclarationReferenceDependency> insertFwdCandidates) {
        IASTPreprocessorIncludeStatement astInclude = IncludeHelper.findAstIncludeStatement(file, includedFileToRemove);
        addSuggestion(new ReplaceIncludeWithFwdSuggestion(file, astInclude, insertFwdCandidates, ReplaceIncludesWithFwdAlgorithm.class));
    }

    /**
     * @return true if replacing of include is possible at all and false if a referred declaration is either not reachable through any other include
     * or it requires a complete type.
     */
    private boolean findInsertFwdCandidates(List<DeclRefDepBoolPair> fwdCandidates, List<DeclarationReferenceDependency> candidatesList) {
        for (DeclRefDepBoolPair curCandidate : fwdCandidates) {
            boolean doPathsHaveSameFirstElement = curCandidate.bool;
            boolean isForwardDeclarationEnough = curCandidate.dependency.getDeclarationReference().isForwardDeclarationEnough();
            if (doPathsHaveSameFirstElement && !isForwardDeclarationEnough) {
                return false;
            }
            if (isForwardDeclarationEnough) {
                candidatesList.add(curCandidate.dependency);
            }
        }
        return true;
    }

    private DeclRefDepsGroup groupDeclRefsByIncludes(Collection<DeclarationReference> declRefs, List<IASTPreprocessorIncludeStatement> includes) {
        DeclRefDepsGroup result = new DeclRefDepsGroup();
        addEmptyIncludeLists(result, includes);
        for (DeclarationReference curRef : declRefs) {
            for (DeclarationReferenceDependency curDependency : curRef.getRequiredDependencies()) {
                List<FirstLastElementIncludePath> includePaths = curDependency.getFirstLastElementIncludePaths();
                boolean haveSameFirstElement = haveSameFirstElement(includePaths);
                DeclRefDepBoolPair pair = new DeclRefDepBoolPair(curDependency, haveSameFirstElement);
                addPairToDeclRefDepsGroup(pair, result);
            }
        }
        return result;
    }

    private void addPairToDeclRefDepsGroup(DeclRefDepBoolPair pair, DeclRefDepsGroup groups) {
        try {
            for (FirstLastElementIncludePath curPath : pair.dependency.getFirstLastElementIncludePaths()) {
                groups.get(IncludeHelper.findIncludedFile(curPath.getFirstInclude(), project)).add(pair);
            }
        } catch (CoreException e) {
            IncludatorPlugin.log("Error while replacing includes with froward declarations.", e);
        }
    }

    private boolean haveSameFirstElement(List<FirstLastElementIncludePath> includePaths) {
        IIndexInclude firstElement = null;
        for (FirstLastElementIncludePath curPath : includePaths) {
            if (firstElement == null) {
                firstElement = curPath.getFirstInclude();
            } else {
                if (!firstElement.equals(curPath.getFirstInclude())) {
                    return false;
                }
            }
        }
        return true;
    }

    private void addEmptyIncludeLists(DeclRefDepsGroup groups, List<IASTPreprocessorIncludeStatement> includes) {
        for (IASTPreprocessorIncludeStatement curInclude : includes) {
            if (curInclude.isResolved()) {
                groups.put(IncludeHelper.findIncludedFile(curInclude, project), new ArrayList<DeclRefDepBoolPair>());
            }
        }
    }

    @Override
    public String getInitialProgressMonitorMessage(String resourceName) {
        return "Trying to replace includes with forward declarations in " + resourceName;
    }

    @Override
    public Set<Class<? extends Algorithm>> getInvolvedAlgorithmTypes() {
        HashSet<Class<? extends Algorithm>> involvedAlgorithms = new HashSet<>();
        involvedAlgorithms.add(ReplaceIncludesWithFwdAlgorithm.class);
        return involvedAlgorithms;
    }

    @Override
    public AlgorithmScope getScope() {
        return AlgorithmScope.EDITOR_SCOPE;
    }
}



class DeclRefDepsGroup extends LinkedHashMap<IncludatorFile, List<DeclRefDepBoolPair>> {

    private static final long serialVersionUID = 1842558839489832912L;
}



class DeclRefDepBoolPair {

    public final boolean                        bool;
    public final DeclarationReferenceDependency dependency;

    public DeclRefDepBoolPair(DeclarationReferenceDependency dependency, boolean bool) {
        this.dependency = dependency;
        this.bool = bool;
    }

    @Override
    public String toString() {
        return dependency.toString() + " / " + Boolean.toString(bool);
    }
}
