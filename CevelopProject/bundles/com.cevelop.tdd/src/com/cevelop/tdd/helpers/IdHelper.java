package com.cevelop.tdd.helpers;

import org.osgi.framework.FrameworkUtil;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;
import ch.hsr.ifs.iltis.cpp.core.ids.IRefactoringId;


public class IdHelper {

    public static final String PLUGIN_ID = FrameworkUtil.getBundle(IdHelper.class).getSymbolicName();

    public enum RefactoringId implements IRefactoringId<RefactoringId> {

      //@formatter:off
      ARGUMENT(PLUGIN_ID + ".refactorings.argument.ArgumentRefactoring"),
      CREATE_CONSTRUCTOR(PLUGIN_ID + ".refactorings.create.function.constructor.ConstructorRefactoring"),
      CREATE_FREE_FUNCTION(PLUGIN_ID + ".refactorings.create.function.free.CreateFreeFunctionRefactoring"),
      CREATE_FREE_OPERATOR(PLUGIN_ID + ".refactorings.create.function.operator.free.CreateFreeOperatorRefactoring"),
      CREATE_LOCAL_VARIABLE(PLUGIN_ID + ".refactorings.create.variable.local.CreateLocalVariableRefactoring"),
      CREATE_MEMBER_FUNCTION(PLUGIN_ID + ".refactorings.create.function.member.CreateMemberFunctionRefactoring"),
      CREATE_MEMBER_OPERATOR(PLUGIN_ID + ".refactorings.create.function.operator.member.CreateMemberOperatorRefactoring"),
      CREATE_MEMBER_VARIABLE(PLUGIN_ID + ".refactorings.create.variable.member.CreateMemberVariableRefactoring"),
      CREATE_NAMESPACE(PLUGIN_ID + ".refactorings.create.namespace.CreateNamespaceRefactoring"),
      CREATE_TYPE(PLUGIN_ID + ".refactorings.create.type.CreateTypeRefactoring"),
      EXTRACT_TO_HEADER(PLUGIN_ID + ".refactorings.extract.ExtractToHeaderRefactoring"),
      VISIBILITY(PLUGIN_ID + ".refactorings.visibility.VisibilityRefactoring");
      //@formatter:on

        private final String id;

        RefactoringId(final String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        public static RefactoringId of(String string) {
            for (final RefactoringId refactoringId : values()) {
                if (refactoringId.getId().equals(string)) {
                    return refactoringId;
                }
            }
            throw new IllegalArgumentException("Illegal RefactoringId: " + string);
        }

        @Override
        public RefactoringId unstringify(final String string) {
            return of(string);
        }

        @Override
        public String stringify() {
            return id;
        }
    }

    public enum ProblemId implements IProblemId<ProblemId> {

      //@formatter:off
      ARGUMENT_MISMATCH(PLUGIN_ID + ".ArgumentMismatchProblem"),
      MISSING_CONSTRUCTOR(PLUGIN_ID + ".MissingConstructorProblem"),
      MISSING_FREE_FUNCTION(PLUGIN_ID + ".MissingFreeFunctionProblem"),
      MISSING_FREE_OPERATOR(PLUGIN_ID + ".MissingFreeOperatorProblem"),
      MISSING_LOCAL_VARIABLE(PLUGIN_ID + ".MissingLocalVariableProblem"),
      MISSING_MEMBER_FUNCTION(PLUGIN_ID + ".MissingMemberFunctionProblem"),
      MISSING_MEMBER_OPERATOR(PLUGIN_ID + ".MissingMemberOperatorProblem"),
      MISSING_MEMBER_VARIABLE(PLUGIN_ID + ".MissingMemberVariableProblem"),
      MISSING_NAMESPACE(PLUGIN_ID + ".MissingNamespaceProblem"),
      MISSING_TYPE(PLUGIN_ID + ".MissingTypeProblem"),
      VISIBILITY(PLUGIN_ID + ".VisibilityProblem");
      //@formatter:on

        private final String id;

        @Override
        public String getId() {
            return id;
        }

        ProblemId(final String id) {
            this.id = id;
        }

        public static ProblemId of(final String string) {
            for (final ProblemId problemId : values()) {
                if (problemId.getId().equals(string)) {
                    return problemId;
                }
            }
            throw new IllegalArgumentException("Illegal ProblemId: " + string);
        }

        @Override
        public ProblemId unstringify(final String string) {
            return of(string);
        }

        @Override
        public String stringify() {
            return id;
        }
    }
}
