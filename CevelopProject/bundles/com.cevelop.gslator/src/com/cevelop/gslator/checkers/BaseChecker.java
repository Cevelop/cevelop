package com.cevelop.gslator.checkers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.codan.core.model.IProblem;
import org.eclipse.cdt.codan.core.model.IProblemWorkingCopy;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;
import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;
import ch.hsr.ifs.iltis.cpp.core.wrappers.AbstractIndexAstChecker;

import com.cevelop.gslator.checkers.visitors.util.ICheckIgnoreAttribute;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.infos.GslatorInfo;
import com.cevelop.gslator.nodes.util.AttributeOwnerHelper;
import com.cevelop.gslator.utils.ASTHelper;


public abstract class BaseChecker extends AbstractIndexAstChecker implements ICheckIgnoreAttribute {

    private static Map<ProblemId, BaseChecker> checkers = new HashMap<>();
    protected IProblem                         problem;

    public BaseChecker() {
        for (ProblemId problemId : getProblemIds()) {
            checkers.put(problemId, this);
        }
    }

    public static BaseChecker getCheckerByProblemId(IProblemId<?> id) {
        return checkers.get(id);
    }

    protected final static String PREFIX_PROBLEM_ID = "com.cevelop.gslator.problems.";

    @Override
    public void initPreferences(IProblemWorkingCopy problem) {
        this.problem = problem;
        super.initPreferences(problem);
    }

    @Override
    public void reportProblem(IProblemId<?> id, IASTNode astNode) {
        if (!ASTHelper.isInMacro(astNode)) {
            super.reportProblem(id, astNode, new GslatorInfo());
        }
    }

    // TODO(tstauber - Sep 27, 2018) REMOVE AFTER TESTING
    //   @Override
    //   public void reportProblem(IProblem problem, IASTNode astNode, Object... args) {
    //      if (!ASTHelper.isInMacro(astNode)) super.reportProblem(problem, astNode);
    //   }

    @Override
    public void reportProblem(IProblemId<?> id, IASTNode astNode, MarkerInfo<?> info) {
        if (!ASTHelper.isInMacro(astNode)) {
            super.reportProblem(id, astNode, info);
        }
    }
    // // TODO(tstauber - Sep 27, 2018) REMOVE AFTER TESTING
    //   @Override
    //   public void reportProblem(IProblem problem, IASTNode astNode, MarkerInfo<?> info) {
    //      if (!ASTHelper.isInMacro(astNode)) super.reportProblem(problem, astNode, info);
    //   }

    abstract public Rule getRule();

    abstract public ProblemId getProblemId();

    protected List<ProblemId> getProblemIds() {
        return Lists.mutable.of(getProblemId());
    }

    @Override
    abstract public String getIgnoreString();

    @Override
    public String getProfileGroup() {
        return "";
    }

    @Override
    public String getNrInProfileGroup() {
        return "";
    }

    public Object getPreference(IResource res, String key) {
        return getPreference(getProblemById(getProblemId().getId(), res), key);
    }

    public IASTNode getIgnoreAttributeNode(IASTNode markedNode) {
        return AttributeOwnerHelper.getWantedAttributeOwner(markedNode);
    }

    public boolean isIgnoreApplicable(IMarker marker, IASTNode iastNode) {
        return true;
    }

    static {
        MEASURE_CHECKER = Boolean.parseBoolean(System.getProperty("MeasureCodan"));
    }

    private static boolean MEASURE_CHECKER;

    @Override
    public final void processAst(IASTTranslationUnit ast) {
        if (MEASURE_CHECKER) {
            checkAstAndMeasure(ast);
        } else {
            checkAst(ast);
        }
    }

    // TODO Is this "Testing?" infrastructure needed here?
    protected void checkAstAndMeasure(IASTTranslationUnit ast) {
        CheckerMeasurement measurement = startMeasurement(ast);
        checkAst(ast);
        stopMeasurement(measurement);
    }

    private void stopMeasurement(CheckerMeasurement measurement) {
        measurement.stop();
        long duration = measurement.duration();
        System.out.println("Stopping measurement of '" + measurement.checkerName + "' for TU '" + measurement.filePath + "' took " + duration + "ms");
    }

    protected abstract void checkAst(IASTTranslationUnit ast);

    protected CheckerMeasurement startMeasurement(IASTTranslationUnit ast) {
        CheckerMeasurement measurement = new CheckerMeasurement(getClass().getName(), ast.getFilePath());
        System.out.println("Starting measurement of '" + measurement.checkerName + "' for TU '" + measurement.filePath + "'");
        measurement.start();
        return measurement;
    }

    protected static class CheckerMeasurement {

        private long         startTimestamp = -1;
        private long         stopTimestamp  = -1;
        private final String filePath;
        private final String checkerName;

        public CheckerMeasurement(String checkerName, String filePath) {
            this.checkerName = checkerName;
            this.filePath = filePath;
        }

        public void stop() {
            if (startTimestamp == -1) {
                throw new RuntimeException("Measurement has not been started yet");
            }
            stopTimestamp = System.currentTimeMillis();
        }

        public void start() {
            if (startTimestamp != -1) {
                throw new RuntimeException("Measurement has already been started");
            }
            startTimestamp = System.currentTimeMillis();
        }

        public long duration() {
            if (startTimestamp == -1) {
                throw new RuntimeException("Measurement has not been started yet");
            }
            if (stopTimestamp == -1) {
                throw new RuntimeException("Measurement is still running");
            }
            return stopTimestamp - startTimestamp;
        }
    }
}
