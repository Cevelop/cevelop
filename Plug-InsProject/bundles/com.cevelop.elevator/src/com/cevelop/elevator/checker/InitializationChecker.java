package com.cevelop.elevator.checker;

import org.eclipse.cdt.codan.core.CodanRuntime;
import org.eclipse.cdt.codan.core.model.IChecker;
import org.eclipse.cdt.codan.core.model.IProblem;
import org.eclipse.cdt.codan.core.model.IProblemWorkingCopy;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNewExpression;
import org.eclipse.core.resources.IFile;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;
import ch.hsr.ifs.iltis.cpp.core.wrappers.AbstractIndexAstChecker;

import com.cevelop.elevator.ast.analysis.DeclaratorCollector;
import com.cevelop.elevator.ast.analysis.InitializerCollector;
import com.cevelop.elevator.ast.analysis.NodeProperties;
import com.cevelop.elevator.ast.analysis.NullPointerCollector;
import com.cevelop.elevator.ast.analysis.conditions.Condition;
import com.cevelop.elevator.ast.analysis.conditions.HasDefaultConstructor;
import com.cevelop.elevator.ids.IdHelper.ProblemId;


public class InitializationChecker extends AbstractIndexAstChecker implements IChecker {

    private final Condition hasDefaultConstructor = new HasDefaultConstructor();

    @Override
    public void processAst(final IASTTranslationUnit ast) {
        Configuration configuration = Configuration.Companion.of(getProblemById(ProblemId.UNINITIALIZED_VAR.id, getFile()));
        collectAndReportDeclarators(ast, configuration);
        collectAndReportInitializers(ast, configuration);
        collectAndReportAllNullPointerInitializations(ast, configuration);
    }

    public static Configuration getConfiguration(IASTTranslationUnit ast) {
        IFile sourceFile = ast.getOriginatingTranslationUnit().getFile();
        IProblem problem = CodanRuntime.getInstance().getCheckersRegistry().getResourceProfile(sourceFile).findProblem(
                ProblemId.UNINITIALIZED_VAR.id);
        return Configuration.Companion.of(problem);
    }

    @Override
    public void initPreferences(IProblemWorkingCopy problem) {
        super.initPreferences(problem);
        Preferences.Companion.getAll().forEach((k, pref) -> addPreference(problem, pref.getKey(), pref.getDescription(), pref.getDefault()));
    }

    private void collectAndReportInitializers(final IASTTranslationUnit ast, Configuration configuration) {
        final InitializerCollector initializerCollector = new InitializerCollector();
        ast.accept(initializerCollector);
        for (IASTInitializer initializer : initializerCollector.getInitializers()) {
            reportProblem(initializer);
        }
    }

    private void collectAndReportDeclarators(final IASTTranslationUnit ast, Configuration configuration) {
        final DeclaratorCollector declaratorCollector = new DeclaratorCollector(configuration.getMarkEqualsInitializers());
        ast.accept(declaratorCollector);
        for (IASTDeclarator declarator : declaratorCollector.getDeclarators()) {
            reportProblem(declarator);
        }
    }

    private void collectAndReportAllNullPointerInitializations(final IASTTranslationUnit ast, Configuration configuration) {
        final NullPointerCollector nullPointerCollector = new NullPointerCollector();
        ast.accept(nullPointerCollector);
        for (IASTExpression expression : nullPointerCollector.getNullMacroCalls()) {
            reportProblem(expression, ProblemId.NULL_MACRO);
        }
    }

    public void reportProblem(final IASTNode astNode) {
        final ProblemId id = hasDefaultConstructor.satifies(astNode) ? ProblemId.DEFAULT_CTOR : ProblemId.UNINITIALIZED_VAR;
        reportProblem(astNode, id);
    }

    private void reportProblem(final IASTNode astNode, final IProblemId<?> id) {
        final NodeProperties nodeProperties = new NodeProperties(astNode);
        if (nodeProperties.hasAncestor(ICPPASTNewExpression.class)) {
            super.reportProblem(id, nodeProperties.getAncestor(ICPPASTNewExpression.class));
        } else {
            super.reportProblem(id, astNode);
        }
    }
}
