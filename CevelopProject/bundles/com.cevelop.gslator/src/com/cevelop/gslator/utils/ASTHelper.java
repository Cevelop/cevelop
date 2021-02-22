package com.cevelop.gslator.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.codan.core.cxx.CxxAstUtils;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTMacroExpansionLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IQualifierType;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTRangeBasedForStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTReferenceOperator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMember;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPVariable;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.core.index.IIndexManager;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPFunctionType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;
import org.eclipse.core.runtime.CoreException;

import ch.hsr.ifs.iltis.cpp.core.resources.CProjectUtil;

import com.cevelop.gslator.CCGlator;
import com.cevelop.gslator.quickfixes.utils.ASTRewriteStore;


@SuppressWarnings("restriction")
public class ASTHelper {

   // @formatter:off
	public enum SpecialFunction {
		NoSpecialFunction,
		DefaultConstructor,
		DefaultCopyAssignment,
		DefaultCopyConstructor,
		MoveAssignment,
		MoveConstructor,
		DefaultDestructor,
		SwapFunction
	}
	// @formatter:on

    private ASTHelper() {}

    public static List<IASTNode> findNames(final IBinding binding, final ICProject iProject, int findDefinitions) {
        return findNames(null, binding, iProject, findDefinitions, null);
    }

    public static List<IASTNode> findNames(final ASTRewriteStore rewriteCache, final IBinding binding, final ICProject iProject, final int flags,
            Map<ITranslationUnit, IASTTranslationUnit> astCache) {
        final List<IASTNode> results = new ArrayList<>();
        IIndex index = null;
        try {

            index = getIndex(rewriteCache, iProject);

            index.acquireReadLock();
            final IIndexBinding adaptedBinding = index.adaptBinding(binding);
            final IIndexName[] indexNames = index.findNames(adaptedBinding, flags);

            for (final IIndexName indexName : indexNames) {
                final ITranslationUnit translationUnit = CxxAstUtils.getTranslationUnitFromIndexName(indexName);
                // sometimes wild null object appears
                if (translationUnit == null) {
                    continue;
                }

                IASTTranslationUnit astTranslationUnit = null;
                if (astCache != null) {
                    if (astCache.containsKey(translationUnit)) {
                        astTranslationUnit = astCache.get(translationUnit);
                    }
                }

                if (astTranslationUnit == null) {
                    astTranslationUnit = getASTTranslationUnit(rewriteCache, translationUnit, index);
                    if (astCache != null && astTranslationUnit != null) {
                        astCache.put(translationUnit, astTranslationUnit);
                    }
                }

                // sometimes wild null object appears
                if (astTranslationUnit == null) {
                    continue;
                }
                final IASTNode declarationName = astTranslationUnit.getNodeSelector(null).findNode(indexName.getNodeOffset(), indexName
                        .getNodeLength());

                if (declarationName != null) {
                    results.add(declarationName);
                }
            }
        } catch (final CoreException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (index != null) {
                index.releaseReadLock();
            }
        }
        return results;
    }

    public static <T extends IASTNode> List<T> findNodeTypes(IASTNode node, Class<T> type) {
        return findNodeTypes(node, type, -1);
    }

    public static <T extends IASTNode> List<T> findNodeTypes(IASTNode node, Class<T> type, int depth) {
        List<T> list = null;
        if (node.getChildren().length > 0 && depth != 0) {
            for (IASTNode child : node.getChildren()) {
                if (list == null) {
                    list = findNodeTypes(child, type, depth - 1);
                } else {
                    list.addAll(findNodeTypes(child, type, depth - 1));
                }
            }
        }
        if (list == null) {
            list = new ArrayList<>();
        }
        if (type.isInstance(node)) {
            list.add(type.cast(node));
        }
        return list;
    }

    private static IIndex getIndex(final ASTRewriteStore rewriteCache, ICProject project) throws CoreException {
        if (rewriteCache != null) {
            return rewriteCache.getIndex();
        } else {
            return CCorePlugin.getIndexManager().getIndex(project, IIndexManager.ADD_DEPENDENCIES | IIndexManager.ADD_DEPENDENT);
        }
    }

    private static IASTTranslationUnit getASTTranslationUnit(final ASTRewriteStore rewriteCache, final ITranslationUnit translationUnit,
            final IIndex index) throws CoreException {
        if (rewriteCache != null) {
            // is needed to store rewrite if not already stored
            rewriteCache.getASTRewrite(translationUnit);
            return rewriteCache.getASTTranslationUnit(translationUnit);
        } else {
            IASTTranslationUnit ast = null;
            try {
                index.acquireReadLock();
                ast = translationUnit.getAST(index, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                index.releaseReadLock();
            }
            return ast;
        }
    }

    public static ICPPASTFunctionDefinition getFunctionDefinition(IASTNode node) {
        while (!(node instanceof ICPPASTFunctionDefinition) && node != null) {
            node = node.getParent();
        }
        return node != null ? (ICPPASTFunctionDefinition) node : null;
    }

    public static IASTDeclaration getFunctionDeclaration(IASTNode node) {
        while (!(node instanceof IASTDeclaration) && node != null) {
            node = node.getParent();
        }
        return node != null ? (IASTDeclaration) node : null;
    }

    public static ICPPASTCompositeTypeSpecifier getCompositeTypeSpecifier(IASTNode node) {
        while (!(node instanceof ICPPASTCompositeTypeSpecifier) && node != null) {
            node = node.getParent();
        }
        return node != null ? (ICPPASTCompositeTypeSpecifier) node : null;
    }

    public static boolean isDefaultConstructor(final IASTNode node) {
        if (doesFunctionDefinitionNameMatchWithTypeSpecifierName(node)) {
            final ICPPASTFunctionDeclarator funcDecl = ASTHelper.getFunctionDeclaratorFromDeclarationOrDefinition(node);
            if (funcDecl != null) {
                return !funcDecl.getName().toString().contains("~") && isParameterListEmpty(funcDecl);
            }
        }
        return false;
    }

    public static boolean isDefaultCopyConstructor(final IASTDeclaration declaration) {
        final IASTDeclarator declarator = getFunctionDeclaratorFromDeclarationOrDefinition(declaration);
        if (declarator != null) {
            final String nows = declarator.getName().toString().replaceAll(" ", "");
            if (nows.contains("operator=") && !nows.contains("operator==")) {
                return false;
            }
        }
        if (doesFunctionDefinitionNameMatchWithTypeSpecifierName(declaration)) {
            return doesParameterMatchWithClassName(declaration, "") && hasOnlyReferenceParameter(declaration, false);
        }
        return false;
    }

    public static boolean isDefaultCopyAssignment(final IASTDeclaration declaration) {
        final IASTDeclarator declarator = getFunctionDeclaratorFromDeclarationOrDefinition(declaration);
        if (declarator != null) {
            final String nows = declarator.getName().toString().replaceAll(" ", "");
            if (nows.contains("operator=") && !nows.contains("operator==")) {
                return doesParameterMatchWithClassName(declaration, "") && hasOnlyReferenceParameter(declaration, false);
            }
        }
        return false;
    }

    public static boolean isDefaultDestructor(final IASTNode declaration) {
        final IASTDeclarator declarator = getFunctionDeclaratorFromDeclarationOrDefinition(declaration);
        final IASTName name = getStructNameFromDeclarationOrDefinition(declaration);
        if (declarator == null || name == null) {
            return false;
        }
        final String destructorShouldName = "~" + name.toString();
        return declarator.getName().toString().contains(destructorShouldName) && isParameterListEmpty(declarator);
    }

    public static boolean doesFunctionDefinitionNameMatchWithTypeSpecifierName(final IASTNode node) {
        final ICPPASTFunctionDeclarator funcDeclarator = getFunctionDeclaratorFromDeclarationOrDefinition(node);
        final IASTName name = getStructNameFromDeclarationOrDefinition(node);
        if (name != null && funcDeclarator != null) {
            IASTName funcDeclaratorName = funcDeclarator.getName();

            if (funcDeclaratorName instanceof ICPPASTQualifiedName) {
                funcDeclaratorName = (IASTName) funcDeclaratorName.getChildren()[0];
            }
            return name.toString().contentEquals(funcDeclaratorName.toString());
        }
        return false;
    }

    private static boolean doesParameterMatchWithClassName(final IASTDeclaration decl, final String optionalClassName) {
        final ICPPASTFunctionDeclarator declarator = getFunctionDeclaratorFromDeclarationOrDefinition(decl);
        if (declarator == null) {
            return false;
        }
        final ICPPASTParameterDeclaration[] parameters = declarator.getParameters();
        for (final ICPPASTParameterDeclaration param : parameters) {
            // Check if parameter name is equal to class name
            if (param.getDeclSpecifier() instanceof ICPPASTNamedTypeSpecifier) {
                IASTName structName = getStructNameFromDeclarationOrDefinition(decl);
                if (structName == null) {
                    return ((ICPPASTNamedTypeSpecifier) param.getDeclSpecifier()).getName().toString().contentEquals(optionalClassName) &&
                           parameters.length == 2;
                } else {
                    return ((ICPPASTNamedTypeSpecifier) param.getDeclSpecifier()).getName().toString().contentEquals(structName.toString());
                }

            }
        }
        return false;
    }

    private static boolean hasOnlyReferenceParameter(final IASTDeclaration decl, boolean shouldBeRValue) {
        final ICPPASTFunctionDeclarator declarator = getFunctionDeclaratorFromDeclarationOrDefinition(decl);
        if (declarator == null) {
            return false;
        }
        final ICPPASTParameterDeclaration[] parameters = declarator.getParameters();
        boolean isRValue = false;
        for (final ICPPASTParameterDeclaration param : parameters) {
            if (param.getDeclarator() != null) {
                IASTPointerOperator[] pointerOperators = param.getDeclarator().getPointerOperators();
                if (pointerOperators.length == 0) {
                    return false;
                }
                for (final IASTPointerOperator poop : pointerOperators) {
                    if (poop instanceof ICPPASTReferenceOperator) {
                        isRValue = ((ICPPASTReferenceOperator) poop).isRValueReference();
                    }
                }
            }
        }
        return isRValue == shouldBeRValue;
    }

    private static boolean isParameterListEmpty(final IASTDeclarator declarator) {
        for (final IASTNode child : declarator.getChildren()) {
            if (child instanceof ICPPASTParameterDeclaration) {
                return false;
            }
        }
        return true;
    }

    public static List<IASTSimpleDeclaration> collectMemberVariables(final ICPPASTCompositeTypeSpecifier struct) {
        final List<IASTSimpleDeclaration> memberVars = new ArrayList<>();
        for (final IASTDeclaration decl : struct.getMembers()) {
            if (decl instanceof IASTSimpleDeclaration) {
                IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) decl;
                IASTDeclSpecifier declSpecifier = simpleDeclaration.getDeclSpecifier();
                if (declSpecifier.getStorageClass() == IASTDeclSpecifier.sc_static) {
                    continue;
                }
                for (final IASTDeclarator toCheckIfFuncDef : simpleDeclaration.getDeclarators()) {
                    if (!(toCheckIfFuncDef instanceof ICPPASTFunctionDeclarator) && declIsNotContained(toCheckIfFuncDef, memberVars)) {
                        memberVars.add((IASTSimpleDeclaration) decl);
                    }
                }
            }
        }
        return memberVars;
    }

    public static List<IASTDeclaration> collectMemberFunctions(final ICPPASTCompositeTypeSpecifier struct) {
        List<ICPPASTFunctionDeclarator> fdecl = findNodeTypes(struct, ICPPASTFunctionDeclarator.class);
        List<IASTDeclaration> ret = new ArrayList<>();
        for (ICPPASTFunctionDeclarator func : fdecl) {
            if (func.getParent() instanceof IASTDeclaration) {
                ret.add((IASTDeclaration) func.getParent());
            }
        }
        return ret;
    }

    private static boolean declIsNotContained(final IASTDeclarator toCheckIfFuncDef, final List<IASTSimpleDeclaration> memberVars) {
        for (final IASTSimpleDeclaration simDec : memberVars) {
            for (final IASTDeclarator decl : simDec.getDeclarators()) {
                if (toCheckIfFuncDef.getName().toString().equals(decl.getName().toString())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static List<IASTSimpleDeclaration> collectGslOwners(final List<IASTSimpleDeclaration> memberVars) {
        final List<IASTSimpleDeclaration> gslOwners = new ArrayList<>();
        for (final IASTSimpleDeclaration decl : memberVars) {
            if (isGslOwner(decl)) {
                gslOwners.add(decl);
            }
        }
        return gslOwners;
    }

    public static boolean isGslOwner(final IASTSimpleDeclaration decl) {
        return decl.getDeclSpecifier().getRawSignature().contains("gsl::owner<");
    }

    public static boolean isMoveConstructor(final IASTDeclaration declaration) {
        return doesFunctionDefinitionNameMatchWithTypeSpecifierName(declaration) && doesParameterMatchWithClassName(declaration, "") &&
               hasOnlyReferenceParameter(declaration, true);
    }

    public static boolean isMoveAssignment(final IASTDeclaration declaration) {
        final IASTDeclarator declarator = getFunctionDeclaratorFromDeclarationOrDefinition(declaration);
        if (declarator != null) {
            return declarator.getName().toString().contains("operator") && declarator.getName().toString().contains("=") &&
                   doesParameterMatchWithClassName(declaration, "") && hasOnlyReferenceParameter(declaration, true);
        }
        return false;
    }

    public static IASTDeclaration getFirstSpecialMemberFunction(final ICPPASTCompositeTypeSpecifier struct, final SpecialFunction desiredFunction) {
        List<IASTDeclaration> nodes = getSpecialMemberFunctions(struct, desiredFunction);
        if (!nodes.isEmpty()) {
            return nodes.get(0);
        }
        return null;

    }

    public static List<IASTDeclaration> getSpecialMemberFunctions(final ICPPASTCompositeTypeSpecifier struct,
            final SpecialFunction... desiredFunctions) {
        List<IASTDeclaration> nodes = new ArrayList<>();
        for (final IASTNode node : struct.getChildren()) {
            if (node instanceof ICPPASTFunctionDefinition || node instanceof IASTSimpleDeclaration) {
                for (SpecialFunction desiredFunction : desiredFunctions) {
                    if (isDesiredFunction(desiredFunction, (IASTDeclaration) node)) {
                        nodes.add((IASTDeclaration) node);
                    }
                }
            }
        }
        return nodes;
    }

    public static boolean isDesiredFunction(final SpecialFunction desiredFunction, final IASTDeclaration decl) {
        switch (desiredFunction) {
        case DefaultConstructor:
            return isDefaultConstructor(decl);
        case DefaultCopyAssignment:
            return isDefaultCopyAssignment(decl);
        case DefaultCopyConstructor:
            return isDefaultCopyConstructor(decl);
        case MoveAssignment:
            return isMoveAssignment(decl);
        case MoveConstructor:
            return isMoveConstructor(decl);
        case DefaultDestructor:
            return isDefaultDestructor(decl);
        case SwapFunction:
            return getSwapFunctionType(decl, "") == AnalyseSwapFunction.IsFriendFunction || getSwapFunctionType(decl,
                    "") == AnalyseSwapFunction.IsMemberFunction;
        default:
            return false;
        }
    }

    public enum AnalyseSwapFunction {
        IsMemberFunction, IsFriendFunction, IsNamespaceFunction, HasWrongName, HasWrongParamCount, HasWrongParamType, HasWrongReturnType, ParamIsConst, ParamIsNotReference, FunctionIsConst, Unknown
    }

    public static int getVisibilityForStatement(final IASTNode statement) {
        final ICPPASTCompositeTypeSpecifier struct = getCompositeTypeSpecifier(statement);
        int visibility = getDefaultVisibilityForStruct(struct);

        for (final IASTNode node : struct.getChildren()) {
            if (node instanceof ICPPASTVisibilityLabel) {
                visibility = ((ICPPASTVisibilityLabel) node).getVisibility();
            }
            if (statement == node) {
                return visibility;
            }
        }
        return 0;
    }

    public static int getDefaultVisibilityForStruct(final ICPPASTCompositeTypeSpecifier struct) {
        switch (struct.getKey()) {
        case IASTCompositeTypeSpecifier.k_struct:
            return ICPPASTVisibilityLabel.v_public;
        case ICPPASTCompositeTypeSpecifier.k_class:
            return ICPPASTVisibilityLabel.v_private;
        default:
            return ICPPASTVisibilityLabel.v_protected;
        }
    }

    public static IASTDeclSpecifier getDeclSpecifierFromDeclaration(final IASTDeclaration declaration) {
        IASTDeclSpecifier declSpec = null;
        if (declaration instanceof IASTSimpleDeclaration) {
            declSpec = ((IASTSimpleDeclaration) declaration).getDeclSpecifier();
        } else if (declaration instanceof ICPPASTFunctionDefinition) {
            declSpec = ((ICPPASTFunctionDefinition) declaration).getDeclSpecifier();
        }
        if (declSpec == null) {
            return getDeclSpecifierFromDeclarationUsingReflection(declaration);
        }
        return declSpec;
    }

    private static IASTDeclSpecifier getDeclSpecifierFromDeclarationUsingReflection(final IASTDeclaration declaration) {
        Class<? extends IASTDeclaration> declarationclass = declaration.getClass();
        try {
            Method getdeclspecifier = declarationclass.getMethod("getDeclSpecifier");
            return (IASTDeclSpecifier) getdeclspecifier.invoke(declaration);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // So what?
        }
        return null;
    }

    public static ICPPASTFunctionDeclarator getFunctionDeclaratorFromDeclarationOrDefinition(final IASTNode node) {
        ICPPASTFunctionDeclarator declarator = null;
        if (node instanceof ICPPASTFunctionDeclarator) {
            return (ICPPASTFunctionDeclarator) node;
        } else if (node instanceof IASTSimpleDeclaration) {
            if (((IASTSimpleDeclaration) node).getDeclarators().length == 0) {
                return null;
            }
            final IASTDeclarator decl = ((IASTSimpleDeclaration) node).getDeclarators()[0];
            if (!(decl instanceof ICPPASTFunctionDeclarator)) {
                return null;
            }
            declarator = (ICPPASTFunctionDeclarator) ((IASTSimpleDeclaration) node).getDeclarators()[0];
        } else if (node instanceof ICPPASTFunctionDefinition) {
            declarator = (ICPPASTFunctionDeclarator) ((IASTFunctionDefinition) node).getDeclarator();
        } else if (node instanceof ICPPASTTemplateDeclaration) {
            IASTDeclaration templateDeclaration = ((ICPPASTTemplateDeclaration) node).getDeclaration();
            if (templateDeclaration instanceof ICPPASTFunctionDefinition) {
                declarator = (ICPPASTFunctionDeclarator) ((ICPPASTFunctionDefinition) templateDeclaration).getDeclarator();
            }
        }
        return declarator;
    }

    public static Boolean isExplicit(final IASTNode node) {
        IASTNode elem = node;
        if (node instanceof ICPPASTFunctionDeclarator) {
            elem = node.getParent();
        }

        if (elem instanceof IASTSimpleDeclaration) {
            final ICPPASTDeclSpecifier declSpec = (ICPPASTDeclSpecifier) ((IASTSimpleDeclaration) elem).getDeclSpecifier();
            return declSpec.isExplicit();
        } else if (elem instanceof ICPPASTFunctionDefinition) {
            final ICPPASTDeclSpecifier declSpec = (ICPPASTDeclSpecifier) ((ICPPASTFunctionDefinition) elem).getDeclSpecifier();
            return declSpec.isExplicit();
        }
        return false;
    }

    public static SpecialFunction getSpecialMemberFunctionType(final IASTDeclaration decl) {
        if (isDefaultConstructor(decl)) {
            return SpecialFunction.DefaultConstructor;
        } else if (isDefaultCopyAssignment(decl)) {
            return SpecialFunction.DefaultCopyAssignment;
        } else if (isDefaultCopyConstructor(decl)) {
            return SpecialFunction.DefaultCopyConstructor;
        } else if (isDefaultDestructor(decl)) {
            return SpecialFunction.DefaultDestructor;
        } else if (isMoveAssignment(decl)) {
            return SpecialFunction.MoveAssignment;
        } else if (isMoveConstructor(decl)) {
            return SpecialFunction.MoveConstructor;
        } else if (getSwapFunctionType(decl, "") == AnalyseSwapFunction.IsFriendFunction || getSwapFunctionType(decl,
                "") == AnalyseSwapFunction.IsMemberFunction) {
                    return SpecialFunction.SwapFunction;
                }
        return SpecialFunction.NoSpecialFunction;
    }

    private static IASTName getStructNameFromDeclarationOrDefinition(final IASTNode decl) {
        final ICPPASTCompositeTypeSpecifier struct = ASTHelper.getCompositeTypeSpecifier(decl);
        if (struct != null) {
            return struct.getName();
        }
        final ICPPASTFunctionDeclarator funcDecl = ASTHelper.getFunctionDeclaratorFromDeclarationOrDefinition(decl);

        if (funcDecl == null) {
            return null;
        }
        final IASTName name = funcDecl.getName();
        if (name instanceof ICPPASTQualifiedName) {
            return (IASTName) name.getChildren()[0];
        } else {
            return null;
        }
    }

    public static boolean isSameName(final IASTName name1, final IASTName name2) {
        return name1.resolveBinding().equals(name2.resolveBinding());
    }

    private static boolean isReturnType(IASTDeclaration funcDefinition, int returnType) {
        IASTNode[] children = funcDefinition.getChildren();
        if (children.length == 0) {
            return false;
        }
        IASTNode firstChild = children[0];
        if (firstChild instanceof IASTSimpleDeclSpecifier) {
            return ((IASTSimpleDeclSpecifier) firstChild).getType() == returnType;
        }

        if (funcDefinition instanceof ICPPASTTemplateDeclaration) {
            IASTDeclaration templateDeclaration = ((ICPPASTTemplateDeclaration) funcDefinition).getDeclaration();
            if (templateDeclaration instanceof ICPPASTFunctionDefinition) {
                IASTDeclSpecifier declSpecifier = ((ICPPASTFunctionDefinition) templateDeclaration).getDeclSpecifier();
                if (declSpecifier instanceof ICPPASTSimpleDeclSpecifier) {
                    return ((ICPPASTSimpleDeclSpecifier) declSpecifier).getType() == returnType;
                }
            }
        }
        return false;
    }

    public static ICPPASTNamespaceDefinition getNamespace(final IASTDeclaration declaration) {
        IASTNode parent = declaration;
        while (!(parent instanceof ICPPASTNamespaceDefinition)) {
            if (parent.getParent() == null) {
                return null;
            }
            parent = parent.getParent();
        }
        return (ICPPASTNamespaceDefinition) parent;
    }

    private static boolean isConstFunction(IASTDeclaration declaration) {
        ICPPASTFunctionDeclarator funcDecl = getFunctionDeclaratorFromDeclarationOrDefinition(declaration);
        if (funcDecl == null) {
            return false;
        }
        return funcDecl.isConst();
    }

    private static boolean hasConstParameter(IASTDeclaration declaration) {
        ICPPASTFunctionDeclarator funcDecl = getFunctionDeclaratorFromDeclarationOrDefinition(declaration);
        if (funcDecl == null) {
            return false;
        }
        boolean isConst = false;
        ICPPASTParameterDeclaration[] parameters = funcDecl.getParameters();
        for (ICPPASTParameterDeclaration parameter : parameters) {
            if (parameter.getDeclSpecifier().isConst()) {
                isConst = true;
            }
        }
        return isConst;
    }

    private static boolean equalsFunctionName(IASTDeclaration declaration, String functionName) {
        ICPPASTFunctionDeclarator funcDecl = getFunctionDeclaratorFromDeclarationOrDefinition(declaration);
        if (funcDecl == null) {
            return false;
        }
        return funcDecl.getName().toString().equals(functionName);
    }

    private static boolean isFriendFunction(IASTDeclaration declaration) {
        IASTNode[] children = declaration.getChildren();
        if (children[0] instanceof ICPPASTSimpleDeclSpecifier) {
            ICPPASTSimpleDeclSpecifier declSpecifier = (ICPPASTSimpleDeclSpecifier) declaration.getChildren()[0];
            return declSpecifier.isFriend();
        }
        return false;
    }

    private static boolean paramTypeMatchesClassName(IASTDeclaration declaration, String className) {
        ICPPASTFunctionDeclarator funcDecl = getFunctionDeclaratorFromDeclarationOrDefinition(declaration);
        ICPPASTParameterDeclaration[] parameters = funcDecl.getParameters();
        for (ICPPASTParameterDeclaration parameter : parameters) {
            if (parameter.getDeclSpecifier() instanceof ICPPASTNamedTypeSpecifier) {
                IASTName paramDeclName = ((ICPPASTNamedTypeSpecifier) parameter.getDeclSpecifier()).getName();
                if (paramDeclName instanceof ICPPASTTemplateId) {
                    if (!((ICPPASTTemplateId) paramDeclName).getTemplateName().toString().equals(className)) {
                        return false;
                    }
                } else if (!((ICPPASTNamedTypeSpecifier) parameter.getDeclSpecifier()).getName().toString().equals(className)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public static AnalyseSwapFunction getSwapFunctionType(IASTDeclaration declaration, String optionalClassName) {
        List<AnalyseSwapFunction> analyseResult = analyseSwapFunction(declaration, optionalClassName);
        if (analyseResult.size() != 1) {
            return AnalyseSwapFunction.Unknown;
        } else {
            switch (analyseResult.get(0)) {
            case IsNamespaceFunction:
            case IsFriendFunction:
            case IsMemberFunction:
                return analyseResult.get(0);
            default:
                return AnalyseSwapFunction.Unknown;
            }
        }
    }

    public static List<AnalyseSwapFunction> analyseSwapFunction(IASTDeclaration declaration, String optionalClassName) {
        List<AnalyseSwapFunction> analyseResult = new ArrayList<>();
        if (!equalsFunctionName(declaration, "swap")) {
            analyseResult.add(AnalyseSwapFunction.HasWrongName);
        }
        if (!hasOnlyReferenceParameter(declaration, false)) {
            analyseResult.add(AnalyseSwapFunction.ParamIsNotReference);
        }
        if (hasConstParameter(declaration)) {
            analyseResult.add(AnalyseSwapFunction.ParamIsConst);
        }
        if (isConstFunction(declaration)) {
            analyseResult.add(AnalyseSwapFunction.FunctionIsConst);
        }
        if (!isReturnType(declaration, IASTSimpleDeclSpecifier.t_void)) {
            analyseResult.add(AnalyseSwapFunction.HasWrongReturnType);
        }

        if (!analyseResult.contains(AnalyseSwapFunction.HasWrongName)) {
            IASTName structName = getStructNameFromDeclarationOrDefinition(declaration);
            int paramcount = 0;
            ICPPASTFunctionDeclarator funcDecl = getFunctionDeclaratorFromDeclarationOrDefinition(declaration);

            if (structName == null) {
                if (optionalClassName != null) {
                    if (!(paramTypeMatchesClassName(declaration, optionalClassName))) {
                        analyseResult.add(AnalyseSwapFunction.HasWrongParamType);
                    }
                } //TODO: else: add check if both param types are equal (not needed yet)
                paramcount = 2;
                if (!(funcDecl.getParameters().length == paramcount)) {
                    analyseResult.add(AnalyseSwapFunction.HasWrongParamCount);
                }
                analyseResult.add(AnalyseSwapFunction.IsNamespaceFunction);
            } else {
                if (!(paramTypeMatchesClassName(declaration, structName.toString()))) {
                    analyseResult.add(AnalyseSwapFunction.HasWrongParamType);
                }
                if (isFriendFunction(declaration)) {
                    paramcount = 2;
                    if (!(funcDecl.getParameters().length == paramcount)) {
                        analyseResult.add(AnalyseSwapFunction.HasWrongParamCount);
                    }
                    analyseResult.add(AnalyseSwapFunction.IsFriendFunction);
                } else {
                    paramcount = 1;
                    if (!(funcDecl.getParameters().length == paramcount)) {
                        analyseResult.add(AnalyseSwapFunction.HasWrongParamCount);
                    }
                    analyseResult.add(AnalyseSwapFunction.IsMemberFunction);
                }
            }
        }
        analyseResult.sort(null);
        return analyseResult;
    }

    public static IType getTypeFromBinding(IBinding binding, boolean simplify) {
        IType type = null;
        try {
            ICPPMember member = (ICPPMember) binding;
            if (member != null) {
                type = member.getType();
            }
        } catch (Exception e) {}
        if (type == null) {
            try {
                ICPPVariable variable = (ICPPVariable) binding;
                if (variable != null) {
                    type = variable.getType();
                }
            } catch (Exception e) {}
        }
        IType simpletype = null;
        if (simplify) {
            simpletype = SemanticUtil.getSimplifiedType(type);
        }
        return simpletype == null ? type : simpletype;
    }

    public static IType getTypeFromExpressionElement(IASTNode element) {
        return getTypeFromExpressionElement(element, false, null);
    }

    public static IType getTypeFromExpressionElement(IASTNode element, boolean simplify) {
        return getTypeFromExpressionElement(element, simplify, null);
    }

    public static IType getTypeFromExpressionElement(IASTNode element, List<String> intermediates) {
        return getTypeFromExpressionElement(element, false, intermediates);
    }

    public static IType getTypeFromExpressionElement(IASTNode element, boolean simplify, List<String> intermediates) {
        return getTypeFromExpressionElement(element, simplify, intermediates, String.class);
    }

    @SuppressWarnings("unchecked")
    public static IType getTypeFromExpressionElement(IASTNode element, boolean simplify, List<?> intermediates, Class<?> listType) {
        if (element instanceof IASTIdExpression) {
            return getTypeFromBinding(((IASTIdExpression) element).getName().resolveBinding(), simplify);
        }
        if (element instanceof IASTCastExpression) {
            for (IASTNode iastNode : element.getChildren()) {
                while (iastNode instanceof IASTUnaryExpression) {
                    iastNode = iastNode.getChildren()[0];
                }
                if (iastNode instanceof IASTTypeId && intermediates != null) {
                    if (listType.equals(String.class)) {
                        ((List<String>) intermediates).add(((IASTTypeId) iastNode).getRawSignature());
                    } else if (listType.equals(IASTTypeId.class)) {
                        ((List<IASTTypeId>) intermediates).add((IASTTypeId) iastNode);
                    }
                }
                if (iastNode instanceof IASTIdExpression) {
                    return ASTHelper.getTypeFromBinding(((IASTIdExpression) iastNode).getName().resolveBinding(), simplify);

                }
                if (iastNode instanceof IASTCastExpression) {
                    return getTypeFromExpressionElement(iastNode, simplify, intermediates, listType);
                }
            }
        }
        return null;
    }

    public static IASTFunctionDeclarator getFunctionDeclaratorFromName(IASTName name) {
        return getFunctionDeclaratorFromName(name, null);
    }

    public static IASTFunctionDeclarator getFunctionDeclaratorFromName(IASTName name, Map<ITranslationUnit, IASTTranslationUnit> astCache) {
        if (name == null) {
            return null;
        }
        IASTFunctionDeclarator funcdecl = getFunctionDeclaratorFromNameInSameTU(name);
        if (funcdecl != null) {
            return funcdecl;
        }
        return getFunctionDeclaratorFromNameViaIndex(name, astCache);
    }

    private static IASTFunctionDeclarator getFunctionDeclaratorFromNameViaIndex(IASTName name, Map<ITranslationUnit, IASTTranslationUnit> astCache) {
        IASTFunctionDeclarator funcdecl;
        //TODO(tstauber): Extract the livin' hell out of this. BRAAAHHHH
        List<IASTNode> names = ASTHelper.findNames(null, name.resolveBinding(), CProjectUtil.getCProject(CProjectUtil.getProject(name)),
                IIndex.FIND_DECLARATIONS_DEFINITIONS, astCache);
        if (names.size() > 0) {
            for (IASTNode namenode : names) {
                funcdecl = ASTHelper.getFunctionDeclaratorFromDeclarationOrDefinition(ASTHelper.getFunctionDeclaration(namenode));
                if (funcdecl != null) {
                    return funcdecl;
                }
            }
        }
        return null;
    }

    private static IASTFunctionDeclarator getFunctionDeclaratorFromNameInSameTU(IASTName name) {
        IName[] declarations = name.getTranslationUnit().getDeclarations(name.resolveBinding());
        if (declarations.length == 1 && declarations[0] instanceof IASTName) {
            IASTNode funcdecl = ((IASTName) (declarations[0])).getParent();
            if (funcdecl instanceof IASTFunctionDeclarator) {
                return (IASTFunctionDeclarator) funcdecl;
            }
        }
        return null;
    }

    public static Map<Integer, IType> getFunctionArguments(IASTFunctionDeclarator functionDeclarator) {
        return getFunctionArguments(functionDeclarator, false);
    }

    public static Map<Integer, IType> getFunctionArguments(IASTFunctionDeclarator functionDeclarator, boolean simplify) {
        Map<Integer, IType> paramsspec = new HashMap<>();
        int i = 0;
        for (IASTNode iastNode : functionDeclarator.getChildren()) {
            if (iastNode instanceof IASTParameterDeclaration) {
                IType type = ASTHelper.getTypeFromBinding(((IASTParameterDeclaration) iastNode).getDeclarator().getName().resolveBinding(), simplify);
                paramsspec.put(i++, type);
            }
        }
        return paramsspec;
    }

    public static IASTName getNameOfFunctionFromDeclaration(IASTSimpleDeclaration decl) {
        final IASTNode[] childs = decl.getChildren();
        for (IASTNode iastNode : childs) {
            if (iastNode instanceof IASTFunctionDeclarator) {
                IASTNode[] fchilds = iastNode.getChildren();
                for (IASTNode iastNode2 : fchilds) {
                    if (iastNode2 instanceof IASTName) {
                        return (IASTName) iastNode2;
                    }
                }
            }
        }
        return null;
    }

    public static IASTName getLoopVariable(final IASTForStatement forStatement) {
        IASTExpression iterationExpression = forStatement.getIterationExpression();
        if (iterationExpression == null) {
            return null;
        }
        if (iterationExpression instanceof IASTUnaryExpression) {
            iterationExpression = getChildExpression(iterationExpression);
        }
        for (IASTNode node : iterationExpression.getChildren()) {
            if (node instanceof IASTIdExpression) {
                final IASTIdExpression idExpression = (IASTIdExpression) node;
                return idExpression.getName();
            }
        }
        return null;
    }

    public static IASTExpression getChildExpression(IASTExpression expression) {
        if (expression instanceof IASTBinaryExpression) {
            IASTBinaryExpression binaryExpression = (IASTBinaryExpression) expression;
            return binaryExpression;
        }
        if (expression instanceof IASTUnaryExpression) {
            IASTUnaryExpression unaryExpression = (IASTUnaryExpression) expression;
            if (unaryExpression.getOperator() == IASTUnaryExpression.op_bracketedPrimary) {
                return getChildExpression(unaryExpression.getOperand());
            }
            return unaryExpression;
        }
        return null;
    }

    public static boolean isForLoopStatement(final IASTName reference) {
        IASTNode node = reference;
        IASTNode child = null;
        while (!(node.getParent() instanceof IASTForStatement) && !(node.getParent() instanceof IASTTranslationUnit)) {
            child = node;
            node = node.getParent();
        }
        return !(child instanceof IASTCompoundStatement);
    }

    public static boolean isInMacro(final IASTNode node) {
        IASTNodeLocation[] locations = node.getNodeLocations();
        for (IASTNodeLocation iastNodeLocation : locations) {
            if (iastNodeLocation instanceof IASTMacroExpansionLocation) {
                return true;
            }
        }
        return false;
    }

    public static IScope getNextParentScope(IASTNode node) {
        IScope scope = null;
        while (scope == null && !(node instanceof IASTTranslationUnit)) {
            try {
                Method method = node.getClass().getMethod("getScope");
                if (method == null) {
                    node = node.getParent();
                    continue;
                }
                Object returnval = method.invoke(node);
                if (returnval instanceof IScope) {
                    scope = (IScope) returnval;
                }
            } catch (Exception e) {}
            node = node.getParent();
        }
        return scope;
    }

    public static IASTNode getNextOuterLoop(IASTNode node) {
        node = node.getParent();
        while (!(node instanceof IASTWhileStatement || node instanceof IASTForStatement || node instanceof IASTDoStatement ||
                 node instanceof ICPPASTRangeBasedForStatement)) {
            if (node instanceof IASTTranslationUnit) {
                return null;
            }
            node = node.getParent();
        }
        return node;
    }

    public static boolean isDirecltyAfterwards(IASTNode a, IASTNode b) {
        if (a == null || b == null || getNextNode(a) == null) {
            return false;
        }
        return getNextNode(a).equals(b);
    }

    public static IASTNode getNextNode(IASTNode node) {
        if (node == null || node.getParent() == null) {
            return null;
        }
        IASTNode parent = node.getParent();
        IASTNode[] siblings = parent.getChildren();
        boolean foundMe = false;
        for (IASTNode sibling : siblings) {
            if (foundMe) {
                return sibling;
            }
            if (sibling.equals(node)) {
                foundMe = true;
            }
        }
        return null;
    }

    public static boolean namesEqual(IASTName a, IASTName b) { // TODO: is this optimizable?
        IIndex indexA = a.getTranslationUnit().getIndex();
        IIndex indexB = b.getTranslationUnit().getIndex();

        try {
            indexA.acquireReadLock();
            indexB.acquireReadLock();
            IBinding bindingA = a.resolveBinding();
            IBinding bindingB = b.resolveBinding();
            if (bindingA.equals(bindingB)) {
                return true;
            }
            IIndex index = a.getTranslationUnit().getIndex();
            IIndexBinding adaptedA = index.adaptBinding(bindingA);
            IIndexBinding adaptedB = index.adaptBinding(bindingB);
            if (adaptedA == null || adaptedB == null) {
                return false;
            }
            return adaptedA.equals(adaptedB);
        } catch (Exception e) {
            CCGlator.getDefault().logException(e);
        } finally {
            indexB.releaseReadLock();
            indexA.releaseReadLock();
        }

        return false;
    }

    public static boolean isNameMemberOfClass(IASTName name, ICPPASTCompositeTypeSpecifier theClass) {
        IASTDeclaration[] members = theClass.getMembers();
        for (IASTDeclaration iastDeclaration : members) {
            List<IASTDeclarator> decls = getDeclarators(iastDeclaration);
            for (IASTDeclarator decl : decls) {
                if (ASTHelper.namesEqual(name, decl.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<IASTDeclarator> getDeclarators(IASTDeclaration declaration) {
        IASTNode[] childs = declaration.getChildren();
        List<IASTDeclarator> list = new ArrayList<>();
        for (IASTNode iastNode : childs) {
            if (iastNode instanceof IASTDeclarator) {
                list.add((IASTDeclarator) iastNode);
            }
        }
        return list;
    }

    public static IASTDeclarator findDeclaratorToName(IASTName name, ASTRewriteStore astRewriteStore) {
        List<IASTDeclaration> decls = ASTHelper.findNodeTypes(name.getTranslationUnit(), IASTDeclaration.class);
        for (IASTDeclaration iastDeclaration : decls) {
            List<IASTDeclarator> declarators = ASTHelper.getDeclarators(iastDeclaration);
            for (IASTDeclarator iastDeclarator : declarators) {
                if (ASTHelper.namesEqual(iastDeclarator.getName(), name)) {
                    return iastDeclarator;
                }
            }
        }

        //TODO(tstauber): Extract the livin' hell out of this. BRAAAHHHH
        List<IASTNode> names = ASTHelper.findNames(astRewriteStore, name.resolveBinding(), CProjectUtil.getCProject(CProjectUtil.getProject(name)),
                IIndex.FIND_DECLARATIONS_DEFINITIONS, null);
        if (names.size() == 1) {
            IASTNode node = names.get(0);
            while (!(node instanceof IASTTranslationUnit) && !(node instanceof IASTDeclarator)) {
                node = node.getParent();
            }
            if (node instanceof IASTDeclarator) {
                return (IASTDeclarator) node;
            }
        }
        return null;
    }

    public static IASTDeclaration getDeclaration(IASTDeclarator declarator) {
        IASTNode node = declarator;
        while (!(node instanceof IASTTranslationUnit) && !(node instanceof IASTDeclaration)) {
            node = node.getParent();
        }
        if (node instanceof IASTDeclaration) {
            return (IASTDeclaration) node;
        }
        return null;
    }

    public static boolean isTypeConst(IType type) {
        if (type instanceof IPointerType) {
            return isTypeConst(((IPointerType) type).getType());
        }
        if (type instanceof CPPFunctionType) {
            return ((CPPFunctionType) type).isConst();
        }
        if (type instanceof IQualifierType) {
            return ((IQualifierType) type).isConst();
        }
        return false;
    }
}
