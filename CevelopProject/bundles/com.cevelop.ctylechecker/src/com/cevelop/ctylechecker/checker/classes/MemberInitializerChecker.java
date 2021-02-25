package com.cevelop.ctylechecker.checker.classes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IField;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;

import com.cevelop.ctylechecker.Infrastructure;
import com.cevelop.ctylechecker.ids.IdHelper.ProblemId;


public class MemberInitializerChecker extends AbstractClassChecker {

    @Override
    public void processAst(IASTTranslationUnit ast) {
        List<ICPPASTFunctionDefinition> constructorDefinitions = constructorDefinitionsOf(ast);
        constructorDefinitions.forEach(this::reportMembersInitializedInBody);
    }

    private boolean hasDelegatingConstructor(ICPPASTConstructorChainInitializer[] initializers, String name) {
        return Arrays.stream(initializers).map(ICPPASTConstructorChainInitializer::getMemberInitializerId).map(IASTName::resolveBinding).map(
                IBinding::getName).anyMatch(initializerName -> initializerName.equals(name));
    }

    private void reportMembersInitializedInBody(ICPPASTFunctionDefinition function) {
        ICPPASTConstructorChainInitializer[] memberInitializers = function.getMemberInitializers();
        String constructorName = function.getDeclarator().getName().resolveBinding().getName();
        if (hasDelegatingConstructor(memberInitializers, constructorName)) {
            return;
        }

        Optional<ICPPConstructor> constructorBinding = getConstructorBinding(function);
        List<IField> fields = Infrastructure.optToStream(constructorBinding).map(ICPPConstructor::getClassOwner).map(ICPPClassType::getFields)
                .flatMap(Arrays::stream).collect(Collectors.toList());

        List<IField> initializedInMemberInitializer = Arrays.stream(memberInitializers).map(
                ICPPASTConstructorChainInitializer::getMemberInitializerId).map(IASTName::resolveBinding).filter(IField.class::isInstance).map(
                        IField.class::cast).collect(Collectors.toList());
        IASTStatement body = function.getBody();
        if (body == null) {
            return;
        }
        body.accept(new ASTVisitor() {

            {
                shouldVisitStatements = true;
            }

            @Override
            public int visit(IASTStatement statement) {
                Optional<IASTExpressionStatement> expressionStatement = Infrastructure.asOpt(statement, IASTExpressionStatement.class);
                expressionStatement.map(IASTExpressionStatement::getExpression).filter(IASTBinaryExpression.class::isInstance).map(
                        IASTBinaryExpression.class::cast).filter(MemberInitializerChecker::isAssignment).map(IASTBinaryExpression::getOperand1)
                        .filter(IASTIdExpression.class::isInstance).map(IASTIdExpression.class::cast).filter(idExpression -> fields.stream().anyMatch(
                                field -> field == idExpression.getName().resolveBinding())).filter(idExpression -> !initializedInMemberInitializer
                                        .stream().anyMatch(field -> field == idExpression.getName().resolveBinding())).ifPresent(
                                                inBodyInitializedField -> reportProblem(ProblemId.MEMBER_INITIALIZER_UNUSED, inBodyInitializedField));
                return super.visit(statement);
            }
        });
    }

    static boolean isAssignment(IASTExpression expression) {
        Optional<IASTBinaryExpression> binaryExpression = Infrastructure.asOpt(expression, IASTBinaryExpression.class);
        return binaryExpression.map(IASTBinaryExpression::getOperator).map(op -> op == IASTBinaryExpression.op_assign).orElse(false);
    }
}
