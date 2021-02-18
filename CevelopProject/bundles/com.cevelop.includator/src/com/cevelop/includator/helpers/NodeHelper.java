/*******************************************************************************
 * Copyright (c) 2014 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 * Thomas Corbat (IFS)
 ******************************************************************************/
package com.cevelop.includator.helpers;

import java.util.Arrays;
import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IArrayType;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.IParameter;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTBinaryExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPUnknownType;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.comparators.AstNodePositionOnlyComparator;


@SuppressWarnings("restriction")
public class NodeHelper {

    public static boolean isTypeSpecifierName(IASTName name) {
        // TODO is this necessary? method declRef.isTypeReference() would solve
        // the same.
        return name.resolveBinding() instanceof ICPPClassType;
    }

    public static boolean isForwardDeclarationEnough(IASTName name) {
        IASTNode ancestor = name.getParent();
        if (ancestor instanceof ICPPASTQualifiedName) {
            ICPPASTQualifiedName qName = (ICPPASTQualifiedName) ancestor;
            for (ICPPASTNameSpecifier specifier : qName.getQualifier()) {
                if (specifier.resolveBinding() instanceof ICPPClassType) {
                    return false;
                }
            }
            ancestor = ancestor.getParent();
        }
        if (ancestor instanceof ICPPASTDeclarator) {
            return false; // only declSpecifier names will cause isForwardDeclarationEnough to return true;
        }
        if (ancestor instanceof ICPPASTBaseSpecifier) {
            return false; // In base clause a type must always be complete
        }
        if (ancestor instanceof ICPPASTNamedTypeSpecifier) {
            if (((ICPPASTNamedTypeSpecifier) ancestor).getStorageClass() == IASTDeclSpecifier.sc_typedef) {
                return true;
            }
        }
        if (isArgumentOfFunctionDefinition(name)) {
            return false; // conservative approach: arguments to function definitions require a complete type
        }

        IASTDeclarator[] declarators = getDeclarators(name);
        if (declarators == null) {
            return false;
        }
        return !containsValueDeclarators(declarators);
    }

    public static boolean isForwardDeclarationEnough(IASTExpression expression, IBinding binding) {
        if (isFullTypeCopied(expression)) {
            return false;
        }
      // @formatter:off
		// covers the case of [otherExpression]->foo, where foo is member-var or member-function
		return !(expression.getParent() instanceof IASTFieldReference) &&
		// covers assignment (e.g. f = [otherExpression], where f is non-pointer)
			   !(isAssignmentToOtherType(expression) || isOtherTypeArrayAssignment(expression));
		// @formatter:on
    }

    private static boolean isFullTypeCopied(IASTExpression expr) {
        if (expr.getExpressionType() instanceof IPointerType) {
            return false;
        }
      // @formatter:off
		return expr instanceof IASTFunctionCallExpression ||
				// returning a type instance in a function implies that it is copied
				isCopiedOnParentFunctionCall(expr) ||
				isExprAssigned(expr);
		// @formatter:on
    }

    private static boolean isExprAssigned(IASTExpression expr) {
        if (expr.getParent() instanceof IASTBinaryExpression) {
            IASTBinaryExpression binExpr = (IASTBinaryExpression) expr.getParent();
            return isRightHandSideOfAssignment(expr, binExpr);
        }
        return false;
    }

    private static boolean isCopiedOnParentFunctionCall(IASTExpression expr) {
        IASTNode parent = expr.getParent();
        if (!(parent instanceof IASTFunctionCallExpression)) {
            return false;
        }
        IASTFunctionCallExpression funcCallExpr = (IASTFunctionCallExpression) parent;
        int argumentIndex = findArgumentIndex(funcCallExpr, expr);
        if (argumentIndex == -1) {
            return false;
        }
        IASTExpression funcNameExpr = funcCallExpr.getFunctionNameExpression();
        if (!(funcNameExpr instanceof IASTIdExpression)) {
            return false;
        }
        IASTIdExpression idExpression = (IASTIdExpression) funcNameExpr;
        IASTName funcName = idExpression.getName();
        IBinding funcNameBinding = funcName.resolveBinding();
        if (!(funcNameBinding instanceof IFunction)) {
            return false;
        }
        if (funcNameBinding instanceof ICPPUnknownType) {
            return true;
        }
        IFunction calledFuncBinding = (IFunction) funcNameBinding;

        IParameter[] parameters = calledFuncBinding.getParameters();
        if (parameters == null || argumentIndex >= parameters.length) { // var-args must be true here which implies the copying of the value.
            return true;
        }
        IParameter param = parameters[argumentIndex];
        IType paramType = param.getType();
        return !(paramType instanceof IPointerType || paramType instanceof ICPPReferenceType);

    }

    private static int findArgumentIndex(IASTFunctionCallExpression funcCallExpr, IASTExpression argumentExpr) {
        int i = 0;
        for (IASTInitializerClause curArg : funcCallExpr.getArguments()) {
            if (curArg == argumentExpr) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    private static boolean isAssignmentToOtherType(IASTExpression expression) {
        return isOtherTypeDefinitionAssignment(expression) || isOtherTypeBinaryExpressionAssignment(expression);
    }

    private static boolean isOtherTypeArrayAssignment(IASTExpression expression) {
        IASTArrayDeclarator arrayDeclarator = NodeHelper.findParentOfType(IASTArrayDeclarator.class, expression);
        if (arrayDeclarator == null) {
            return false;
        }
        IBinding declNameBinding = arrayDeclarator.getName().resolveBinding();
        if (!(declNameBinding instanceof IVariable)) {
            return false;
        }
        IVariable var = (IVariable) declNameBinding;

        IType arrayType = var.getType();
        if (!(arrayType instanceof IArrayType)) {
            return false;
        }
        IType type = ((IArrayType) arrayType).getType();
        return !expression.getExpressionType().isSameType(type);
    }

    private static boolean isOtherTypeBinaryExpressionAssignment(IASTExpression expression) {
        if (expression.getParent() instanceof IASTBinaryExpression) {
            IASTBinaryExpression binExpression = (IASTBinaryExpression) expression.getParent();
            // The following three lines match better to the description above:
            if (!isRightHandSideOfAssignment(expression, binExpression)) {
                return false;
            }
            if (binExpression instanceof CPPASTBinaryExpression) {
                CPPASTBinaryExpression cppExpression = (CPPASTBinaryExpression) binExpression;
                if (cppExpression.getOverload() == null) {
                    final IType lhsType = cppExpression.getOperand1().getExpressionType();
                    final IType rhsType = cppExpression.getOperand2().getExpressionType();
                    return !(lhsType != null && rhsType != null && lhsType.isSameType(rhsType));
                }
            }
        }
        return false;
    }

    private static boolean isRightHandSideOfAssignment(IASTExpression expression, IASTBinaryExpression binExpression) {
        if (binExpression.getOperand1() == expression) {
            return false;
        }
        return binExpression.getOperator() == IASTBinaryExpression.op_assign;
    }

    private static boolean isOtherTypeDefinitionAssignment(IASTExpression expression) {
        if (expression.getParent() instanceof IASTEqualsInitializer) {
            IType firstType = findInitializerType(expression);
            if (firstType == null) {
                String failureMsg = "Failed to find left-hand type of assignment \"" + expression.getRawSignature() + FileHelper
                        .getExtendedPositionString(expression) + "\".";
                IncludatorPlugin.logStatus(new IncludatorStatus(new IncludatorException(failureMsg)), expression.getFileLocation().getFileName());
            }
            if (firstType instanceof ICPPReferenceType) {
                IType unwrapedRefType = BindingHelper.unwrapType(firstType);
                if (expression.getExpressionType().isSameType(unwrapedRefType)) {
                    return false; // reference initializer to same type ref
                }
            }
            return !expression.getExpressionType().isSameType(firstType);
        }
        return false;
    }

    private static IType findInitializerType(IASTNode expression) {
        IASTDeclarator declarator = NodeHelper.findParentOfType(IASTDeclarator.class, expression);
        if (declarator == null) {
            return null;
        }
        IBinding declaratorBinding = declarator.getName().resolveBinding();
        if (!(declaratorBinding instanceof IVariable)) {
            return null;
        }
        return ((IVariable) declaratorBinding).getType();
    }

    private static boolean isArgumentOfFunctionDefinition(IASTName name) {
        return isParamDeclaration(name) && isContainedInFunctionDeclaration(name);
    }

    private static boolean isContainedInFunctionDeclaration(IASTName name) {
        IASTNode functionDeclarator = findParentOfType(IASTFunctionDeclarator.class, name);
        if (functionDeclarator != null) {
            IASTNode parent = functionDeclarator.getParent();
            return parent instanceof IASTFunctionDefinition;
        }
        return false;
    }

    private static boolean isParamDeclaration(IASTName name) {
        return findParentOfType(IASTParameterDeclaration.class, name) != null;
    }

    public static IASTDeclarator[] getDeclarators(IASTName name) {
        IASTParameterDeclaration parameterDecl = findParentOfType(IASTParameterDeclaration.class, name);
        if (parameterDecl != null) {
            return new IASTDeclarator[] { parameterDecl.getDeclarator() };
        }
        IASTSimpleDeclaration declaration = findParentOfType(IASTSimpleDeclaration.class, name);
        if (declaration != null) {
            return declaration.getDeclarators();
        }
        IASTFunctionDefinition definition = findParentOfType(IASTFunctionDefinition.class, name);
        if (definition != null) {
            return new IASTDeclarator[] { definition.getDeclarator() };
        }
        return null;
    }

    public static boolean containsValueDeclarators(IASTDeclarator... declarators) {
        for (IASTDeclarator curDeclarator : declarators) {
            if (curDeclarator.getPointerOperators().length == 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNodeContainingOther(IASTFileLocation containingNodeLocation, IASTFileLocation containedNodeLocation) {
        if (containingNodeLocation.getNodeOffset() > containedNodeLocation.getNodeOffset()) {
            return false;
        }
        if (containingNodeLocation.getNodeOffset() + containingNodeLocation.getNodeLength() < containedNodeLocation.getNodeOffset() +
                                                                                              containedNodeLocation.getNodeLength()) {
            return false;
        }
        if (!containingNodeLocation.getFileName().equals(containedNodeLocation.getFileName())) {
            return false;
        }
        return true;
    }

    public static IASTFileLocation createCroppedFileLocation(final IASTFileLocation fileLocation, final int newLength) {
        return new IASTFileLocation() {

            @Override
            public IASTFileLocation asFileLocation() {
                return this;
            }

            @Override
            public int getStartingLineNumber() {
                return fileLocation.getStartingLineNumber();
            }

            @Override
            public int getNodeOffset() {
                return fileLocation.getNodeOffset();
            }

            @Override
            public int getNodeLength() {
                return newLength;
            }

            @Override
            public String getFileName() {
                return fileLocation.getFileName();
            }

            @Override
            public int getEndingLineNumber() {
                return fileLocation.getEndingLineNumber();
            }

            @Override
            public String toString() {
                return getFileName() + "[" + getNodeOffset() + "," + (getNodeOffset() + getNodeLength()) + ")";
            }

            @Override
            public IASTPreprocessorIncludeStatement getContextInclusionStatement() {
                return null;
            }
        };
    }

    public static boolean hasNoPointerOperators(IASTDeclarator declarator) {
        return declarator.getPointerOperators().length == 0;
    }

    /**
     * Returns the first of all given declarators which is not a pointer and ref
     * type.
     *
     * @param declarators A variable number of {@link IASTDeclarator}
     * @return The first {@link IASTDeclarator} which has no pointer operators
     */
    public static Optional<IASTDeclarator> getFirstValueDeclarator(IASTDeclarator... declarators) {
        return Arrays.stream(declarators).filter(NodeHelper::hasNoPointerOperators).findFirst();
    }

    public static boolean hasDeclarators(IASTName name) {
        IASTDeclarator[] declarators = getDeclarators(name);
        return declarators != null && declarators.length != 0;
    }

    @SuppressWarnings("unchecked")
    public static <T extends IASTNode> T findParentOfType(Class<T> klass, IASTNode node) {
        while (node != null) {
            if (klass.isInstance(node)) {
                return (T) node; // here an unchecked warning is generated
                                 // because the code 'node instanceof T' is
                                 // not valid as condition in the
                                 // previous line.
            }
            node = node.getParent();
        }
        return null;
    }

    /**
     * Returns whether this name potentially denotes a polymorphic method call.
     * This is the case when the name is not qualified and denotes a method call
     * and the method is accessed via a pointer or a reference to an object. <br>
     * No checks are performed whether the method is actually virtual or not. <br>
     * <br>
     * This code is an adaption of the method
     * PDOMCPPMethod.getAdditionalNameFlags().
     * 
     * @param methodName The method name to check
     * @return {@code true} iff the method could be a potential polymorphic call
     */
    public static boolean couldBePolymorphicMethodCall(IASTName methodName) {
        if (!methodName.isReference()) {
            return false;
        }
        IASTNode parent = methodName.getParent();
        if (parent instanceof ICPPASTFieldReference) {
            // the name is not qualified
            ICPPASTFieldReference fr = (ICPPASTFieldReference) parent;
            parent = parent.getParent();
            if (parent instanceof IASTFunctionCallExpression) {
                if (fr.isPointerDereference()) {
                    return true;
                }
                IType type = fr.getFieldOwner().getExpressionType();
                if (type instanceof ICPPReferenceType) {
                    return true;
                }
            }
        }
        // calling a member from within a member
        else if (parent instanceof IASTIdExpression) {
            if (parent.getParent() instanceof IASTFunctionCallExpression) {
                return true;
            }
        }
        return false;
    }

    public static boolean isForwardDeclaration(IASTNode node) {
        IASTDeclaration declaration = null;
        if (node instanceof IASTDeclaration) {
            declaration = (IASTDeclaration) node;
        } else if (node instanceof IASTName) {
            node = node.getParent();
            if (node instanceof ICPPASTDeclSpecifier) {
                node = node.getParent();
                if (node instanceof IASTSimpleDeclaration) {
                    declaration = (IASTDeclaration) node;
                }
            }
            if (node instanceof IASTFunctionDeclarator) {
                declaration = (IASTDeclaration) node.getParent();
            }
        }
        if (declaration != null) {
            if (declaration.getParent() instanceof ICPPASTTemplateDeclaration) {
                declaration = (IASTDeclaration) declaration.getParent();
            }
            return isForwardDeclaration(declaration);
        }
        return false;
    }

    public static boolean isForwardDeclaration(IASTDeclaration declaration) {
        if (declaration instanceof ICPPASTTemplateDeclaration) {
            declaration = ((ICPPASTTemplateDeclaration) declaration).getDeclaration();
        }
        if (declaration instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration simpleDecl = (IASTSimpleDeclaration) declaration;
            return isClassFwd(simpleDecl) || isFunctionFwd(simpleDecl);
        }
        return false;
    }

    private static boolean isFunctionFwd(IASTSimpleDeclaration simpleDecl) {
        IASTDeclarator[] declarators = simpleDecl.getDeclarators();
        if (declarators.length != 1) {
            return false;
        }
        IASTDeclarator declarator = declarators[0];
        return declarator instanceof IASTFunctionDeclarator;
    }

    private static boolean isClassFwd(IASTSimpleDeclaration simpleDecl) {
        IASTDeclSpecifier specifier = simpleDecl.getDeclSpecifier();
        if (specifier instanceof IASTElaboratedTypeSpecifier) {
            return true;
        }
        return false;
    }

    public static IASTNode findFollowingNonPreprocessorNodeByPosition(IASTNode node) {
        IASTNode parent = node.getParent();
        if (node == null || parent == null) {
            return null;
        }
        boolean match = false;
        for (IASTNode actNode : getAllNodesIn(parent)) {
            if (match && !(actNode instanceof IASTPreprocessorStatement)) {
                return actNode;
            }
            if (actNode.equals(node)) {
                match = true;
            }
        }
        return null;
    }

    /**
     * returns all contained node in parent, including all include directives
     * contained in the translation unit. the nodes will be ordered according to
     * node offsets.
     *
     * @param parent
     * @return
     */
    private static IASTNode[] getAllNodesIn(IASTNode parent) {
        IASTDeclaration[] declarations = getAllDeclarationsIn(parent);

        IASTTranslationUnit tu = parent.getTranslationUnit();
        IASTNode[] includeDirectives = tu.getIncludeDirectives();
        if (includeDirectives.length == 0) {
            return declarations;
        }
        IASTNode[] all = new IASTNode[includeDirectives.length + declarations.length];
        System.arraycopy(includeDirectives, 0, all, 0, includeDirectives.length);
        System.arraycopy(declarations, 0, all, includeDirectives.length, declarations.length);
        Arrays.sort(all, new AstNodePositionOnlyComparator());
        return all;
    }

    private static IASTDeclaration[] getAllDeclarationsIn(IASTNode parent) {
        if (parent instanceof ICPPASTCompositeTypeSpecifier) {
            return ((ICPPASTCompositeTypeSpecifier) parent).getMembers();
        } else if (parent instanceof IASTTranslationUnit) {
            return ((IASTTranslationUnit) parent).getDeclarations();
        } else if (parent instanceof ICPPASTNamespaceDefinition) {
            return ((ICPPASTNamespaceDefinition) parent).getDeclarations();
        }
        return new IASTDeclaration[0];
    }

    public static boolean isOnlyDeclarationName(IASTName name) {
        // an elaboratedTypeSpecifier can always be considered as a declaration of the type
        return (name.isDeclaration() && !name.isDefinition()) || name.getParent() instanceof IASTElaboratedTypeSpecifier;
    }
}
