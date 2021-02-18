package com.cevelop.gslator.quickfixes.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTBreakStatement;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeleteExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression.CaptureDefault;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTReferenceOperator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplatedTypeTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;
import org.eclipse.cdt.core.parser.Keywords;
import org.eclipse.cdt.internal.core.dom.parser.ASTQueries;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPNodeFactory;

import com.cevelop.gslator.utils.CCGlatorCPPNodeFactory;


@SuppressWarnings("restriction")
public class ASTFactory {

    private static final CPPNodeFactory factory = CCGlatorCPPNodeFactory.getCPPNodeFactory();

    private ASTFactory() {}

    public static ICPPASTFunctionDefinition newDefaultConstructor(final IASTName name) {
        return newConstructor(name, true, false);
    }

    public static ICPPASTFunctionDefinition newInlineDefaultConstructor(final IASTName name, final boolean inline) {
        return newConstructor(name, true, inline);
    }

    public static ICPPASTFunctionDefinition newConstructor(final IASTName name, final boolean shouldBeDefault, final boolean inline) {
        final ICPPASTSimpleDeclSpecifier newSimpleDec = factory.newSimpleDeclSpecifier();
        newSimpleDec.setInline(inline);
        final ICPPASTFunctionDefinition function = factory.newFunctionDefinition(newSimpleDec, factory.newFunctionDeclarator(name.copy()), null);
        ((ICPPASTFunctionDeclarator) function.getDeclarator()).setNoexceptExpression(ICPPASTFunctionDeclarator.NOEXCEPT_DEFAULT);
        fillFunction(function, shouldBeDefault);
        return function;
    }

    public static ICPPASTFunctionDefinition newDefaultCopyAssignment(final IASTName name) {
        return newCopyAssignment(name, true);
    }

    public static ICPPASTFunctionDefinition newCopyAssignment(final IASTName name, final boolean shouldBeDefault) {
        final ICPPASTFunctionDeclarator decl = factory.newFunctionDeclarator(factory.newName("operator=".toCharArray()));
        decl.addPointerOperator(factory.newReferenceOperator());
        decl.addParameterDeclaration(newParameter(true, name.copy(), true, false, factory.newName("rhs".toCharArray())));
        final ICPPASTFunctionDefinition function = factory.newFunctionDefinition(factory.newTypedefNameSpecifier(name.copy()), decl, null);
        fillFunction(function, shouldBeDefault);
        return function;
    }

    private static void fillFunction(final ICPPASTFunctionDefinition function, final boolean shouldBeDefault) {
        if (shouldBeDefault) {
            function.setIsDefaulted(shouldBeDefault);
        } else {
            function.setBody(factory.newCompoundStatement());
        }
    }

    public static ICPPASTFunctionDefinition newDefaultCopyConstructor(final IASTName name) {
        return newCopyConstructor(name, true);
    }

    public static ICPPASTFunctionDefinition newCopyConstructor(final IASTName name, final boolean shouldBeDefault) {
        final ICPPASTFunctionDeclarator decl = factory.newFunctionDeclarator(name.copy());
        decl.addParameterDeclaration(newParameter(true, name.copy(), true, false, factory.newName("rhs".toCharArray())));
        final ICPPASTFunctionDefinition function = factory.newFunctionDefinition(factory.newSimpleDeclSpecifier(), decl, null);
        fillFunction(function, shouldBeDefault);
        return function;
    }

    public static ICPPASTFunctionDefinition newDefaultMoveAssignment(final IASTName name) {
        return newMoveAssignment(name, true);
    }

    public static ICPPASTFunctionDefinition newMoveAssignment(final IASTName name, final boolean shouldBeDefault) {
        final ICPPASTFunctionDeclarator decl = factory.newFunctionDeclarator(factory.newName("operator=".toCharArray()));
        decl.addPointerOperator(factory.newReferenceOperator());
        decl.addParameterDeclaration(newParameter(false, name.copy(), false, true, factory.newName("rhs".toCharArray())));
        decl.setNoexceptExpression(ICPPASTFunctionDeclarator.NOEXCEPT_DEFAULT);
        final ICPPASTFunctionDefinition function = factory.newFunctionDefinition(factory.newTypedefNameSpecifier(name.copy()), decl, null);
        fillFunction(function, shouldBeDefault);
        return function;
    }

    public static ICPPASTFunctionDefinition newDefaultMoveConstructor(final IASTName name) {
        return newMoveConstructor(name, true);
    }

    public static ICPPASTFunctionDefinition newMoveConstructor(final IASTName name, final boolean shouldBeDefault) {
        final ICPPASTFunctionDeclarator decl = factory.newFunctionDeclarator(name.copy());
        decl.addParameterDeclaration(newParameter(false, name.copy(), false, true, factory.newName("rhs".toCharArray())));
        decl.setNoexceptExpression(ICPPASTFunctionDeclarator.NOEXCEPT_DEFAULT);
        final ICPPASTFunctionDefinition function = factory.newFunctionDefinition(factory.newSimpleDeclSpecifier(), decl, null);
        fillFunction(function, shouldBeDefault);
        return function;
    }

    public static ICPPASTFunctionDefinition newDefaultNoexceptDestructor(final IASTName name) {
        return newDestructor(name, true, true);
    }

    public static ICPPASTFunctionDefinition newDefaultDestructor(final IASTName name) {
        return newDestructor(name, true, false);
    }

    public static ICPPASTFunctionDefinition newNoexceptDestructor(final IASTName name) {
        return newDestructor(name, false, true);
    }

    public static ICPPASTFunctionDefinition newDestructor(final IASTName name, final boolean shouldBeDefault, final boolean noexcept) {
        final ICPPASTFunctionDefinition function = factory.newFunctionDefinition(factory.newSimpleDeclSpecifier(), factory.newFunctionDeclarator(
                factory.newName(getName(name))), null);
        if (noexcept) {
            ((ICPPASTFunctionDeclarator) function.getDeclarator()).setNoexceptExpression(ICPPASTFunctionDeclarator.NOEXCEPT_DEFAULT);
        }
        fillFunction(function, shouldBeDefault);
        return function;
    }

    private static char[] getName(final IASTName name) {
        final String oldName = name.toString();
        return oldName.contains("~") ? oldName.toCharArray() : ("~" + oldName).toCharArray();
    }

    private static ICPPASTParameterDeclaration newParameter(final boolean shouldBeConst, final IASTName type, final boolean shouldBeReference,
            final boolean shouldBeRValueReference, final IASTName name) {
        // TODO whatif pointer?
        final ICPPASTDeclarator decl = factory.newDeclarator(name.copy());
        if (shouldBeReference) {
            decl.addPointerOperator(factory.newReferenceOperator());
        }
        if (shouldBeRValueReference) {
            decl.addPointerOperator(factory.newReferenceOperator(shouldBeRValueReference));
        }
        final ICPPASTNamedTypeSpecifier declSpec = factory.newTypedefNameSpecifier(type.copy());
        declSpec.setConst(shouldBeConst);
        final ICPPASTParameterDeclaration parameter = factory.newParameterDeclaration(declSpec, decl);
        return parameter;
    }

    public static IASTStatement newDeleteStatement(final IASTName name, final boolean isArray, final boolean isReference) {
        IASTExpression expr = factory.newIdExpression(name.copy());
        if (isReference) {
            expr = factory.newUnaryExpression(IASTUnaryExpression.op_amper, expr);
        }
        expr = factory.newDeleteExpression(expr);
        ((ICPPASTDeleteExpression) expr).setIsVectored(isArray);

        return factory.newExpressionStatement(expr);
    }

    public static ICPPASTVisibilityLabel newVisibilityLabel(final int visibility) {
        return factory.newVisibilityLabel(visibility);
    }

    public static IASTDeclaration newNamespaceSwapFunction(final IASTName typeName) {
        IASTName parameterTypeName = typeName;
        ICPPASTTemplateDeclaration surroundingTemplateDeclaration = ASTQueries.findAncestorWithType(typeName, ICPPASTTemplateDeclaration.class);
        if (surroundingTemplateDeclaration != null) {
            List<String> templateParameterNames = getTemplateParameterNames(surroundingTemplateDeclaration);
            ICPPASTTemplateId swapParameterType = factory.newTemplateId(typeName.copy());
            templateParameterNames.stream().map(factory::newName).map(factory::newIdExpression).forEachOrdered(
                    swapParameterType::addTemplateArgument);
            parameterTypeName = swapParameterType;
        }

        IASTCompoundStatement statement = factory.newCompoundStatement();

        statement.addStatement(factory.newExpressionStatement(factory.newFunctionCallExpression(factory.newFieldReference(factory.newName("swap"),
                factory.newIdExpression(factory.newName("a"))), new IASTInitializerClause[] { factory.newIdExpression(factory.newName("b")) })));

        List<ICPPASTParameterDeclaration> params = new ArrayList<>();
        params.add(newParameter(false, parameterTypeName, true, false, factory.newName("a")));
        params.add(newParameter(false, parameterTypeName, true, false, factory.newName("b")));

        IASTDeclaration swapFunctionDefinition = newFunctionDefinition(IASTSimpleDeclSpecifier.t_void, "swap", params, true, statement);
        if (surroundingTemplateDeclaration != null) {
            ICPPASTTemplateDeclaration swapTemplateDeclaration = factory.newTemplateDeclaration(swapFunctionDefinition);
            List<ICPPASTTemplateParameter> adaptedTemplateParameters = adaptTemplateParameters(surroundingTemplateDeclaration);
            adaptedTemplateParameters.stream().forEachOrdered(swapTemplateDeclaration::addTemplateParameter);
            return swapTemplateDeclaration;
        }
        return swapFunctionDefinition;
    }

    private static ArrayList<ICPPASTTemplateParameter> adaptTemplateParameters(ICPPASTTemplateDeclaration templateDeclaration) {
        ArrayList<ICPPASTTemplateParameter> adaptedParamters = new ArrayList<>();
        int anonymousParameterCount = 0;
        for (ICPPASTTemplateParameter templateParameter : templateDeclaration.getTemplateParameters()) {
            if (templateParameter instanceof ICPPASTParameterDeclaration) {
                ICPPASTParameterDeclaration parameterDeclaration = (ICPPASTParameterDeclaration) templateParameter;
                ICPPASTParameterDeclaration adaptedParameter = parameterDeclaration.copy();
                String adaptedParameterName = adaptedParameter.getDeclarator().getName().toString();
                if (adaptedParameterName.isEmpty()) {
                    adaptedParameter.getDeclarator().setName(factory.newName("__anonymous" + anonymousParameterCount++));
                }
                adaptedParamters.add(adaptedParameter);
            } else if (templateParameter instanceof ICPPASTSimpleTypeTemplateParameter) {
                ICPPASTSimpleTypeTemplateParameter simpleTypeTemplateParameter = (ICPPASTSimpleTypeTemplateParameter) templateParameter;
                ICPPASTSimpleTypeTemplateParameter adaptedParameter = simpleTypeTemplateParameter.copy();
                String adaptedParameterName = adaptedParameter.getName().toString();
                if (adaptedParameterName.isEmpty()) {
                    adaptedParameter.setName(factory.newName("__anonymous" + anonymousParameterCount++));
                }
                adaptedParamters.add(adaptedParameter);
            } else if (templateParameter instanceof ICPPASTTemplatedTypeTemplateParameter) {
                ICPPASTTemplatedTypeTemplateParameter templateTemplateParameter = (ICPPASTTemplatedTypeTemplateParameter) templateParameter;
                ICPPASTTemplatedTypeTemplateParameter adaptedParameter = templateTemplateParameter.copy();
                String adaptedParameterName = adaptedParameter.getName().toString();
                if (adaptedParameterName.isEmpty()) {
                    adaptedParameter.setName(factory.newName("__anonymous" + anonymousParameterCount++));
                }
                adaptedParamters.add(adaptedParameter);
            }
        } ;
        return adaptedParamters;

    }

    private static List<String> getTemplateParameterNames(ICPPASTTemplateDeclaration surroundingTemplateDeclaration) {
        ArrayList<String> names = new ArrayList<>();
        int anonymousParameterCount = 0;
        for (ICPPASTTemplateParameter templateParameter : surroundingTemplateDeclaration.getTemplateParameters()) {
            IASTName templateArgumentASTName = factory.newName("__unknown__");
            if (templateParameter instanceof ICPPASTParameterDeclaration) {
                ICPPASTParameterDeclaration parameterDeclaration = (ICPPASTParameterDeclaration) templateParameter;
                templateArgumentASTName = parameterDeclaration.getDeclarator().getName();
            } else if (templateParameter instanceof ICPPASTSimpleTypeTemplateParameter) {
                ICPPASTSimpleTypeTemplateParameter simpleTypeTemplateParameter = (ICPPASTSimpleTypeTemplateParameter) templateParameter;
                templateArgumentASTName = simpleTypeTemplateParameter.getName();
            } else if (templateParameter instanceof ICPPASTTemplatedTypeTemplateParameter) {
                ICPPASTTemplatedTypeTemplateParameter templateTemplateParameter = (ICPPASTTemplatedTypeTemplateParameter) templateParameter;
                templateArgumentASTName = templateTemplateParameter.getName();
            }
            String argumentName = templateArgumentASTName.toString();
            if (argumentName.isEmpty()) {
                argumentName = "__anonymous" + anonymousParameterCount++;
            }
            if (templateParameter.isParameterPack()) {
                argumentName += String.valueOf(Keywords.cpELLIPSIS);
            }
            names.add(argumentName);
        } ;
        return names;
    }

    public static ICPPASTFunctionDefinition newSwapFunction(final IASTName typeName) {
        IASTCompoundStatement compoundstmt = factory.newCompoundStatement();
        compoundstmt.addStatement(factory.newDeclarationStatement(factory.newUsingDeclaration(factory.newQualifiedName(new String[] { "std" },
                "swap"))));

        List<ICPPASTParameterDeclaration> params = new ArrayList<>();
        params.add(newParameter(false, typeName, true, false, factory.newName("other")));

        return newFunctionDefinition(IASTSimpleDeclSpecifier.t_void, "swap", params, true, compoundstmt);
    }

    public static ICPPASTFunctionCallExpression newQualifiedNamedFunctionCall(String[] nameQualifiers, String name,
            IASTInitializerClause[] initializerClauses) {
        if (nameQualifiers == null) {
            nameQualifiers = new String[] {};
        }
        ICPPASTQualifiedName qualifiedName = factory.newQualifiedName(nameQualifiers, name);
        return factory.newFunctionCallExpression(factory.newIdExpression(qualifiedName), initializerClauses);
    }

    public static ICPPASTFunctionCallExpression newFunctionCallExpression(IASTExpression idExpr, IASTInitializerClause[] arguments) {
        return factory.newFunctionCallExpression(idExpr, arguments);
    }

    private static ICPPASTFunctionDefinition newFunctionDefinition(int returnType, String name, List<ICPPASTParameterDeclaration> params,
            boolean noexcept, IASTStatement compoundstmt) {
        ICPPASTFunctionDeclarator functionDeclarator = factory.newFunctionDeclarator(factory.newName(name));
        for (ICPPASTParameterDeclaration param : params) {
            functionDeclarator.addParameterDeclaration(param);
        }
        if (noexcept) functionDeclarator.setNoexceptExpression(ICPPASTFunctionDeclarator.NOEXCEPT_DEFAULT);

        ICPPASTSimpleDeclSpecifier declspec = factory.newSimpleDeclSpecifier();
        declspec.setType(returnType);

        return factory.newFunctionDefinition(declspec, functionDeclarator, compoundstmt);
    }

    public static ICPPASTReferenceOperator newReferenceOperator() {
        return factory.newReferenceOperator();
    }

    public static IASTDeclarationStatement newDeclarationStatement(IASTName name, IASTDeclSpecifier declSpecifier, IASTNode rightSide) {
        if (rightSide == null) {
            rightSide = factory.newLiteralExpression(0, "0");
        }
        ICPPASTDeclarator declarator = factory.newDeclarator(name);
        if (rightSide instanceof IASTLiteralExpression) {
            declarator.setInitializer(factory.newEqualsInitializer((IASTInitializerClause) rightSide));
        } else {
            declarator.setInitializer((IASTInitializer) rightSide.copy());
        }
        if (declSpecifier == null) {
            declSpecifier = newSimpleDeclSpec(Kind.eInt);
        } else {
            declSpecifier = declSpecifier.copy();
        }
        IASTSimpleDeclaration declaration = factory.newSimpleDeclaration(declSpecifier);
        declaration.addDeclarator(declarator);

        IASTDeclarationStatement statement = factory.newDeclarationStatement(declaration);

        return statement;
    }

    public static IASTWhileStatement newWhileStatement(IASTExpression condition, IASTStatement body) {
        return factory.newWhileStatement(condition, body);
    }

    public static IASTBinaryExpression newBinaryExpression(int op, IASTExpression expr1, IASTExpression expr2) {
        return factory.newBinaryExpression(op, expr1, expr2);
    }

    public static IASTCompoundStatement newCompoundStatement(final List<IASTStatement> statements) {
        IASTCompoundStatement comp = factory.newCompoundStatement();
        for (IASTStatement iastStatement : statements) {
            comp.addStatement(iastStatement);
        }
        return comp;
    }

    public static IASTName newName(String name) {
        return factory.newName(name);
    }

    public static IASTIdExpression newIdExpression(IASTName name) {
        return factory.newIdExpression(name);
    }

    public static IASTSimpleDeclSpecifier newSimpleDeclSpec(Kind kind) {
        ICPPASTSimpleDeclSpecifier declspec = factory.newSimpleDeclSpecifier();
        declspec.setType(kind);
        return declspec;
    }

    public static IASTEqualsInitializer newEqualsInitializer(IASTLiteralExpression expr) {
        return factory.newEqualsInitializer(expr);
    }

    /**
     * Creates a new literal expression
     * 
     * @param kind
     * The literal expression kind {@link IASTLiteralExpression}
     * 
     * @param val
     * The literal expression value
     * 
     * @return A new {@link IASTLiteralExpression}
     */
    public static IASTLiteralExpression newLiteralExpression(int kind, String val) {
        return factory.newLiteralExpression(kind, val);
    }

    public static IASTExpressionStatement newExpressionStatement(IASTExpression expr) {
        return factory.newExpressionStatement(expr);
    }

    /**
     * Creates a new unary expression
     * 
     * @param operator
     * The unary expression operator
     * 
     * @param operand
     * The unary expression operand
     * 
     * @return a new {@link IASTUnaryExpression}
     */
    public static IASTUnaryExpression newUnaryExpression(int operator, IASTExpression operand) {
        return factory.newUnaryExpression(operator, operand);
    }

    public static IASTIfStatement newIfStatement(IASTExpression condition, IASTStatement thenClause, IASTStatement elseClause) {
        return factory.newIfStatement(condition, thenClause, elseClause);
    }

    public static IASTDoStatement newDoStatement(IASTStatement body, IASTExpression condition) {
        return factory.newDoStatement(body, condition);
    }

    public static IASTBreakStatement newBreakStatement() {
        return factory.newBreakStatement();
    }

    public static IASTExpression newLambdaExpression(IASTCompoundStatement compoundStatement, CaptureDefault capture) {
        ICPPASTLambdaExpression lambda = factory.newLambdaExpression();
        lambda.setCaptureDefault(capture);
        lambda.setBody(compoundStatement);
        return lambda;
    }

    public static IASTReturnStatement newReturnStatement(IASTExpression retValue) {
        return factory.newReturnStatement(retValue);

    }
}
