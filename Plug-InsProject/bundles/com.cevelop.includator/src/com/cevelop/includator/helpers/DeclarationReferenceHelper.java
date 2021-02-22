/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.IASTCompletionContext;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPSpecialization;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexMacro;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.index.IndexFilter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.cxxelement.ClassDeclarationReference;
import com.cevelop.includator.cxxelement.Declaration;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.helpers.comparators.DeclarationComparator;
import com.cevelop.includator.helpers.comparators.StatusComparator;
import com.cevelop.includator.preferences.IncludatorPropertyManager;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.resources.IncludatorProject;


public class DeclarationReferenceHelper {

    public static Collection<DeclarationReference> findDeclReferences(IASTNode node, IncludatorFile file) {
        RefMap refs = new RefMap();
        DeclarationReferenceVisitor visitor = new DeclarationReferenceVisitor(file, refs);
        node.accept(visitor);
        PreprocessorHelper.addAllPreprocessorNames(refs, node.getTranslationUnit(), file, node.getFileLocation());
        purgeExcluded(refs, file);
        return refs.values();
    }

    private static void purgeExcluded(RefMap refs, IncludatorFile file) {
        Set<String> excludedSymbols = new TreeSet<>(IncludatorPropertyManager.getExcludedSymbols(file.getProject()));
        Iterator<DeclarationReference> itr = refs.values().iterator();
        while (itr.hasNext()) {
            DeclarationReference nextDeclRef = itr.next();
            if (excludedSymbols.contains(nextDeclRef.getName())) {
                itr.remove();
            }
        }
    }

    public static List<DeclarationReferenceDependency> getDependencies(DeclarationReference declarationReference) {
        List<DeclarationReferenceDependency> dependencyList = new ArrayList<>();
        Set<Declaration> declarations = new TreeSet<>(new DeclarationComparator());
        IncludatorProject project = declarationReference.getFile().getProject();
        IIndex index = project.getIndex();
        List<IName> targetNames = findTargetNames(declarationReference, index);

        for (IName curName : targetNames) {
            if (shouldSkipName(curName, declarationReference)) {
                continue;
            }
            IncludatorFile file = project.getFile(FileHelper.getFileLocation(curName).getFileName());
            Declaration declaration = IncludatorPlugin.getDeclarationStore().getDeclaration(curName, file);
            declarations.add(declaration);
        }
        for (Declaration curDeclaration : declarations) {
            DeclarationReferenceDependency dependency = new DeclarationReferenceDependency(declarationReference, curDeclaration);
            dependencyList.add(dependency);
        }
        return dependencyList;
    }

    private static boolean shouldSkipName(IName name, DeclarationReference declarationReference) {
        if (name.getFileLocation() != null) {
            return false;
        }
        return (name.getClass().getCanonicalName().equals("org.eclipse.cdt.internal.core.parser.scanner.ASTBuiltinName"));
    }

    private static List<IName> findTargetNames(DeclarationReference declarationReference, IIndex index) {
        ArrayList<IName> targetNames = new ArrayList<>();
        IBinding binding = declarationReference.getBinding();
        findTargetNames(declarationReference, index, targetNames, binding);
        return targetNames;
    }

    private static void findTargetNames(DeclarationReference ref, IIndex index, ArrayList<IName> targetNames, IBinding binding) {
        if (binding == null) {
            return;
        }
        if (binding instanceof IProblemBinding) {
            IProblemBinding problemBinding = (IProblemBinding) binding;
            if (isNotFoundProblem(problemBinding)) {
                addPotentialMacroNames(targetNames, ref, index);
            } else if (problemBinding.getID() == IProblemBinding.SEMANTIC_AMBIGUOUS_LOOKUP) {
                handleAmbiguousLookupBinding(ref, index, targetNames, problemBinding);
            }
            return;
        }
        tryFindWithIndex(targetNames, binding, index);
        tryFindWithinAST(targetNames, binding, ref);
        if (!targetNames.isEmpty()) {
            return;
        }
        tryFindWithSpecializedBinding(targetNames, binding, index);
        if (!targetNames.isEmpty()) {
            return;
        }
        tryFindWithASTCompletionContext(targetNames, ref, index);
    }

    private static void handleAmbiguousLookupBinding(DeclarationReference ref, IIndex index, ArrayList<IName> targetNames,
            IProblemBinding problemBinding) {
        MultiStatus ambiguityWarning = new MultiStatus(IncludatorPlugin.PLUGIN_ID, IncludatorStatus.STATUS_CODE_CUSTOM, "Use of " + ref +
                                                                                                                        " is ambiguous. Candidates are:",
                null);
        ArrayList<IName> ambiguityCandidateNames = new ArrayList<>();
        for (IBinding curCandidate : problemBinding.getCandidateBindings()) {
            findTargetNames(ref, index, ambiguityCandidateNames, curCandidate);
        }
        IncludatorProject proj = ref.getFile().getProject();
        TreeSet<IStatus> trimedCandidates = new TreeSet<>(new StatusComparator());
        for (IName curCandidate : ambiguityCandidateNames) {
            trimedCandidates.add(new IncludatorStatus(IStatus.WARNING, FileHelper.getLocationStrForIName(curCandidate, proj)));
        }
        for (IStatus curStatus : trimedCandidates) {
            ambiguityWarning.add(curStatus);
        }
        IncludatorPlugin.logStatus(ambiguityWarning, ref.getFile());
        targetNames.addAll(ambiguityCandidateNames);
    }

    private static boolean isNotFoundProblem(IProblemBinding binding) {
        int id = binding.getID();
        return id == IProblemBinding.SEMANTIC_NAME_NOT_FOUND || id == IProblemBinding.SEMANTIC_DEFINITION_NOT_FOUND;
    }

    private static void addPotentialMacroNames(ArrayList<IName> targetNames, DeclarationReference declarationReference, IIndex index) {
        IASTNode node = declarationReference.getASTNode();
        if (!(node instanceof IASTName)) {
            return;
        }
        IASTName astName = (IASTName) node;
        IndexFilter filter = IndexFilter.getDeclaredBindingFilter(astName.getLinkage().getLinkageID(), false);
        try {
            IIndexMacro[] macros = index.findMacros(astName.toCharArray(), filter, new NullProgressMonitor());
            macros = filterUndefs(macros);
            if (macros.length == 0) {
                return;
            }
            IName firstMacroName = macros[0].getDefinition();
            if (firstMacroName != null) {
                targetNames.add(firstMacroName);
                if (macros.length > 1) {
                    IncludatorProject project = declarationReference.getFile().getProject();
                    String locStr = FileHelper.getLocationStrForIName(firstMacroName, project);
                    String message = "There are several potential macro definitions for " + declarationReference + ". Assuming " + locStr +
                                     " as correct. Candidates were:";
                    MultiStatus status = new MultiStatus(IncludatorPlugin.PLUGIN_ID, IncludatorStatus.STATUS_CODE_CUSTOM, message, null);
                    addMacroCandidateChildStatus(status, macros, project);
                    IncludatorPlugin.logStatus(status, declarationReference.getFile());
                }
            }
        } catch (CoreException e) {
            // ignore
        }
    }

    private static IIndexMacro[] filterUndefs(IIndexMacro[] macros) {
        ArrayList<IIndexMacro> defs = new ArrayList<>(Arrays.asList(macros));
        Iterator<IIndexMacro> it = defs.iterator();
        while (it.hasNext()) {
            try {
                if (it.next().getDefinition() == null) {
                    it.remove();
                }
            } catch (CoreException e) {
                it.remove();
            }
        }
        return defs.toArray(new IIndexMacro[defs.size()]);
    }

    private static void addMacroCandidateChildStatus(MultiStatus status, IIndexMacro[] candidates, IncludatorProject project) throws CoreException {
        for (IIndexMacro curCandidate : candidates) {
            status.add(new IncludatorStatus(IStatus.WARNING, FileHelper.getLocationStrForIName(curCandidate.getDefinition(), project)));
        }
    }

    private static void tryFindWithSpecializedBinding(List<IName> targetNames, IBinding binding, IIndex index) {
        if (binding instanceof ICPPSpecialization) {
            // CDT bug 207320, handle template instances
            while (binding instanceof ICPPSpecialization) {
                binding = ((ICPPSpecialization) binding).getSpecializedBinding();
            }
            resolveBindingAndAddNames(targetNames, index, binding);
        }
    }

    private static void tryFindWithinAST(List<IName> targetNames, IBinding binding, DeclarationReference declarationReference) {
        IASTTranslationUnit tu = declarationReference.getFile().getTranslationUnit();
        if (tu == null) {
            return;
        }
        IName[] declarations = tu.getDeclarations(binding);
        targetNames.addAll(Arrays.asList(declarations));
    }

    private static void tryFindWithASTCompletionContext(List<IName> targetNames, DeclarationReference declarationReference, IIndex index) {
        if (declarationReference instanceof ClassDeclarationReference) {
            return;
        }
        IASTNode node = declarationReference.getASTNode();
        if (node instanceof IASTName) {
            IASTName name = (IASTName) node;
            IASTCompletionContext completionContext = getExpressionCompletionContext(name);
            if (completionContext != null && completionContext instanceof IASTIdExpression) {
                IBinding[] completionContextBindings = completionContext.findBindings(name, false);
                if (completionContextBindings == null) {
                    return;
                }
                for (IBinding curBinding : completionContextBindings) {
                    resolveBindingAndAddNames(targetNames, index, curBinding);
                }
            }
        }
    }

    private static IASTCompletionContext getExpressionCompletionContext(IASTName name) {
        IASTCompletionContext completionContext = name.getCompletionContext();
        while (completionContext instanceof ICPPASTQualifiedName) {
            completionContext = ((IASTName) completionContext).getCompletionContext();
        }
        return completionContext;
    }

    private static void tryFindWithIndex(List<IName> targetNames, IBinding binding, IIndex index) {
        resolveBindingAndAddNames(targetNames, index, binding);
    }

    private static void resolveBindingAndAddNames(List<IName> targetNames, IIndex index, IBinding binding) {
        try {
            IIndexName[] declNames = index.findNames(binding, IIndex.FIND_DECLARATIONS_DEFINITIONS | IIndex.SEARCH_ACROSS_LANGUAGE_BOUNDARIES);
            targetNames.addAll(Arrays.asList(declNames));
        } catch (CoreException e) {
            throw new IncludatorException(e);
        }
    }
}
