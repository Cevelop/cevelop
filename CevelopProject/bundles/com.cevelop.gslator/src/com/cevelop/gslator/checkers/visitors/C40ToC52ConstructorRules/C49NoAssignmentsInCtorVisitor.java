package com.cevelop.gslator.checkers.visitors.C40ToC52ConstructorRules;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPField;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.C40ToC52ConstructorRules.C49NoAssignmentsInCtorChecker;
import com.cevelop.gslator.checkers.visitors.BaseVisitor;
import com.cevelop.gslator.checkers.visitors.util.AttributeMatcher;
import com.cevelop.gslator.ids.IdHelper.ProblemId;


public class C49NoAssignmentsInCtorVisitor extends BaseVisitor {

    public C49NoAssignmentsInCtorVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarators = true;
    }

    class AssignmentVisitor extends ASTVisitor {

        public AssignmentVisitor(List<IBinding> initializerNames, IBinding declBinding) {
            this.initializerNames = initializerNames;
            this.declBinding = declBinding;
            super.shouldVisitExpressions = true;
        }

        private final List<IBinding> initializerNames;
        private final IBinding       declBinding;

        @Override
        public int visit(final IASTExpression expression) {

            if (expression instanceof ICPPASTBinaryExpression) {

                ICPPASTBinaryExpression binaryExpression = (ICPPASTBinaryExpression) expression;

                if (binaryExpression.getOperator() == IASTBinaryExpression.op_assign && AttributeMatcher.check(new C49NoAssignmentsInCtorChecker(),
                        binaryExpression)) {

                    final IASTExpression leftOperand = binaryExpression.getOperand1();

                    if (leftOperand instanceof ICPPASTFieldReference) {

                        final IBinding fieldBinding = ((ICPPASTFieldReference) leftOperand).getFieldName().resolveBinding();

                        if (fieldBinding instanceof ICPPField && ((ICPPField) fieldBinding).getInitialValue() == null && !initializerNames.contains(
                                fieldBinding)) {
                            checker.reportProblem(ProblemId.P_C49, binaryExpression);
                        }
                    }

                    if (leftOperand instanceof IASTIdExpression) {
                        final IBinding expressionBinding = ((IASTIdExpression) leftOperand).getName().resolveBinding();

                        if (expressionBinding instanceof ICPPField) {

                            final ICPPField field = (ICPPField) expressionBinding;
                            final ICPPClassType classType = ((ICPPConstructor) declBinding).getClassOwner();

                            if (field.getInitialValue() == null && !initializerNames.contains(expressionBinding) && field.getClassOwner().equals(
                                    classType)) {

                                checker.reportProblem(ProblemId.P_C49, binaryExpression);
                            }
                        }
                    }
                }
            }
            return super.visit(expression);
        }
    }

    @Override
    public int visit(final IASTDeclarator declarator) {
        IASTName declName = declarator.getName();

        if (declName == null) {
            return PROCESS_CONTINUE;
        }

        IBinding declBinding = declName.resolveBinding();
        IASTNode parentNode = declarator.getParent();

        if (declBinding instanceof ICPPConstructor && parentNode instanceof ICPPASTFunctionDefinition) {

            final ICPPASTFunctionDefinition constructor = (ICPPASTFunctionDefinition) parentNode;
            final IASTStatement ctorBody = constructor.getBody();
            List<IBinding> initializerNames = Arrays.stream(constructor.getMemberInitializers()).map(i -> i.getMemberInitializerId().resolveBinding())
                    .collect(Collectors.toList());

            if (ctorBody instanceof ICPPASTCompoundStatement) {

                ctorBody.accept(new AssignmentVisitor(initializerNames, declBinding));
            }
        }

        return super.visit(declarator);
    }
}
