package com.cevelop.codeanalysator.core.guideline;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.core.resources.IProject;

import com.cevelop.codeanalysator.core.guideline.IGuidelinePreferences.IPreferencesChangeListener;

import ch.hsr.ifs.iltis.cpp.core.resources.CProjectUtil;


public class GuidelinePriorityResolverImpl implements IGuidelinePriorityResolver, IPreferencesChangeListener {

    private final Map<IGuideline, Set<Rule>>                sharedRulesByGuideline = new HashMap<>();
    private IGuidelinePreferences                           guidelinePreferences;
    private Map<IProject, GuidelineProjectPriorityResolver> guidelinePriorityResolversByProject;

    public GuidelinePriorityResolverImpl() {
        guidelinePriorityResolversByProject = new HashMap<>();
    }

    public void startListening(IGuidelinePreferences guidelinePreferences) {
        if (guidelinePreferences == null) throw new IllegalArgumentException("guidelinePreferences must not be null.");
        if (this.guidelinePreferences != null) throw new IllegalStateException("cannot listen to more than one guideline preferences instance.");

        this.guidelinePreferences = guidelinePreferences;
        guidelinePreferences.addPreferencesChangeListener(this);
    }

    @Override
    public void registerSharedRule(Rule rule) {
        if (rule == null) throw new IllegalArgumentException("rule must not be null.");
        if (!rule.isSharedProblem()) throw new IllegalArgumentException(String.format("rule is not a shared rule. [%s]", rule.getProblemId()));

        Set<Rule> sharedRules = sharedRulesByGuideline.computeIfAbsent(rule.getGuideline(), k -> new HashSet<Rule>());
        boolean alreadyRegistered = !sharedRules.add(rule);
        if (alreadyRegistered) throw new IllegalArgumentException(String.format("rule already registered. [%s]", rule.getProblemId()));
    }

    @Override
    public void computePriorityOrderings() {
        if (guidelinePreferences == null) throw new IllegalStateException("cannot compute priority orderings while not listening.");

        guidelinePriorityResolversByProject.values().stream() //
                .forEach(GuidelineProjectPriorityResolver::computePriorityOrderings);
    }

    @Override
    public boolean isHighestActiveRuleForNode(Rule rule, IASTNode node) {
        if (rule == null) throw new IllegalArgumentException("rule must not be null.");
        if (node == null) throw new IllegalArgumentException("node must not be null.");

        IProject project = CProjectUtil.getProject(node);
        if (project == null) throw new IllegalArgumentException("node must be associated with a project.");

        if (rule.isSharedProblem()) {
            GuidelineProjectPriorityResolver guidelinePriorityResolverForProject = getSharedRulesBySharedProblemIdForProject(project);
            return guidelinePriorityResolverForProject.isHighestActiveSharedRuleForNode(rule, node);
        } else {
            return guidelinePreferences != null && guidelinePreferences.isGuidelineEnabledForProject(rule.getGuideline(), project) //
                   && !rule.isSuppressedForNode(node);
        }
    }

    private GuidelineProjectPriorityResolver getSharedRulesBySharedProblemIdForProject(IProject project) {
        GuidelineProjectPriorityResolver guidelinePriorityResolverForProject = guidelinePriorityResolversByProject.get(project);
        if (guidelinePriorityResolverForProject == null) {
            // replace guidelinePriorityResolversByProject with new instance to assure thread safety
            // and to prevent concurrent isHighestActiveRuleForNode checks to read a partial map
            Map<IProject, GuidelineProjectPriorityResolver> newGuidelinePriorityResolversByProject = new HashMap<>(
                    guidelinePriorityResolversByProject);
            guidelinePriorityResolverForProject = new GuidelineProjectPriorityResolver(project);
            newGuidelinePriorityResolversByProject.put(project, guidelinePriorityResolverForProject);
            guidelinePriorityResolversByProject = newGuidelinePriorityResolversByProject;
        }
        return guidelinePriorityResolverForProject;
    }

    @Override
    public void guidelinePreferencesChange() {
        computePriorityOrderings();
    }

    private class GuidelineProjectPriorityResolver {

        private final IProject          project;
        private Map<String, List<Rule>> sharedRulesBySharedProblemId;

        private GuidelineProjectPriorityResolver(IProject project) {
            this.project = project;
            computePriorityOrderings();
        }

        private void computePriorityOrderings() {
            // replace sharedRulesBySharedProblemId with new instance to assure thread safety
            // and to prevent concurrent isHighestActiveSharedRuleForNode checks to read a partial map
            Map<String, List<Rule>> newSharedRulesBySharedProblemId = new HashMap<>();
            Comparator<IGuideline> guidelinePriorityComparator = Comparator.comparingInt( //
                    g -> guidelinePreferences.getGuidelinePriorityForProject(g, project));
            sharedRulesByGuideline.entrySet().stream() //
                    .filter(entry -> guidelinePreferences.isGuidelineEnabledForProject(entry.getKey(), project)) //
                    .sorted((a, b) -> guidelinePriorityComparator.compare(a.getKey(), b.getKey())) //
                    .forEachOrdered(entry -> entry.getValue().stream() //
                            .forEach(rule -> newSharedRulesBySharedProblemId.computeIfAbsent(rule.getSharedProblemId(), //
                                    k -> new ArrayList<Rule>()).add(rule)));
            this.sharedRulesBySharedProblemId = newSharedRulesBySharedProblemId;
        }

        private boolean isHighestActiveSharedRuleForNode(Rule rule, IASTNode node) {
            return getHighestActiveSharedRuleForNode(rule.getSharedProblemId(), node) //
                    .map(r -> r.equals(rule)) //
                    .orElse(false);
        }

        private Optional<Rule> getHighestActiveSharedRuleForNode(String sharedProblemId, IASTNode node) {
            List<Rule> sharedRules = sharedRulesBySharedProblemId.get(sharedProblemId);
            if (sharedRules == null) throw new IllegalStateException(String.format("no shared rule with this shared problem id registered. [%s]", //
                    sharedProblemId));

            return sharedRules.stream() //
                    .filter(r -> !r.isSuppressedForNode(node)) //
                    .findFirst();
        }
    }
}
