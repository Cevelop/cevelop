package com.cevelop.codeanalysator.core.checker;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.cdt.codan.core.CodanRuntime;
import org.eclipse.cdt.codan.core.cxx.model.AbstractIndexAstChecker;
import org.eclipse.cdt.codan.core.model.ICheckersRegistry;
import org.eclipse.cdt.codan.core.model.IProblem;
import org.eclipse.cdt.codan.core.model.IProblemProfile;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import com.cevelop.codeanalysator.core.CodeAnalysatorRuntime;
import com.cevelop.codeanalysator.core.guideline.IGuidelinePreferences;
import com.cevelop.codeanalysator.core.guideline.IGuidelinePriorityResolver;
import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;
import com.cevelop.codeanalysator.core.visitor.VisitorComposite;

import ch.hsr.ifs.iltis.cpp.core.resources.CProjectUtil;


public abstract class CodeAnalysatorChecker extends AbstractIndexAstChecker {

    private final IGuidelinePreferences      guidelinePreferences;
    private final IGuidelinePriorityResolver priorityResolver;

    public CodeAnalysatorChecker() {
        guidelinePreferences = CodeAnalysatorRuntime.getDefault().getGuidelinePreferences();
        priorityResolver = CodeAnalysatorRuntime.getDefault().getPriorityResolver();
    }

    @Override
    public void processAst(final IASTTranslationUnit ast) {
        VisitorComposite compositeVisitor = new VisitorComposite();

        RuleReporter ruleReporter = new RuleReporter(this, priorityResolver);
        Collection<CodeAnalysatorVisitor> visitors = createVisitors(ruleReporter);

        IProject project = CProjectUtil.getProject(ast);
        Set<String> enabledProblemIds = getEnabledProblemIds(ast);
        visitors.stream() //
                .filter(v -> isVisitorEnabledForProject(v, project, enabledProblemIds)) //
                .forEach(v -> compositeVisitor.add(v));

        if (compositeVisitor.containsVisitors()) {
            ast.accept(compositeVisitor);
        }
    }

    protected abstract Collection<CodeAnalysatorVisitor> createVisitors(RuleReporter ruleReporter);

    private Set<String> getEnabledProblemIds(IASTTranslationUnit ast) {
        Collection<IProblem> codanProblems = getCodanProblems(ast);
        return codanProblems.stream() //
                .filter(IProblem::isEnabled) //
                .map(IProblem::getId) //
                .collect(Collectors.toSet());
    }

    private Collection<IProblem> getCodanProblems(IASTTranslationUnit ast) {
        ICheckersRegistry checkersRegistry = CodanRuntime.getInstance().getCheckersRegistry();
        Collection<IProblem> defaultCodanProblems = checkersRegistry.getRefProblems(this);

        Optional<ITranslationUnit> tu = Optional.ofNullable(ast.getOriginatingTranslationUnit());
        Optional<IResource> resource = tu.map(ITranslationUnit::getResource);
        Optional<IProblemProfile> problemProfile = resource.map(checkersRegistry::getResourceProfile);
        Optional<Collection<IProblem>> profileCodanProblems = problemProfile.map(profile -> {
            return defaultCodanProblems.stream() //
                    .map(IProblem::getId) //
                    .map(profile::findProblem) //
                    .collect(Collectors.toList());
        });

        return profileCodanProblems.orElse(defaultCodanProblems);
    }

    private boolean isVisitorEnabledForProject(CodeAnalysatorVisitor visitor, IProject project, Set<String> enabledProblemIds) {
        if (visitor == null || project == null || enabledProblemIds == null) {
            return false;
        }

        Rule rule = visitor.getRule();

        if (rule == null) {
            return false;
        }

        return guidelinePreferences.isGuidelineEnabledForProject(rule.getGuideline(), project) && enabledProblemIds.contains(rule.getProblemId());
    }
}
