/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.helpers;

import static org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil.ALLCVQ;
import static org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil.PTR;
import static org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil.REF;
import static org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil.TDEF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.ASTTypeUtil;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IArrayType;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IQualifierType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBasicType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMember;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNamespaceScope;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.core.index.IIndexFile;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.index.IIndexInclude;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.index.IndexFilter;
import org.eclipse.cdt.core.index.IndexLocationFactory;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.core.dom.parser.ITypeContainer;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTBaseDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTLiteralExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamedTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTPointer;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPBasicType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPClassSpecialization;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPClassTemplate;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPClassType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPNamespace;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPScope;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPVariable;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;
import org.eclipse.cdt.internal.core.pdom.dom.cpp.IPDOMCPPClassType;
import org.eclipse.cdt.internal.ui.refactoring.NodeContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;

import com.cevelop.tdd.Activator;
import com.cevelop.tdd.helpers.visitors.PossibleReturnTypeFindVisitor;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringContext;


public class TypeHelper {

    public static IType getTypeOf(IASTInitializerClause clause) {
        if (clause instanceof IASTInitializerList) {
            IASTInitializerClause[] clauses = ((IASTInitializerList) clause).getClauses();
            if (clauses.length > 0) {
                return getTypeOf(clauses[0]);
            }
            // Cannot fetch information about literal expressions inside
            // initializer
            // lists because CModelBuilder2 sets parseFlags to
            // ITranslationUnit.AST_SKIP_TRIVIAL_EXPRESSIONS_IN_AGGREGATE_INITIALIZERS
            // -> Assume int as default
            return new CPPBasicType(Kind.eInt, 0);
        }
        if (clause instanceof IASTIdExpression) {
            IASTName xname = ((IASTIdExpression) clause).getName();
            IBinding binding = xname.resolveBinding();
            if (binding instanceof IVariable) {
                return ((IVariable) binding).getType();
            }
            if (binding instanceof IFunction) {
                return ((IFunction) binding).getType().getReturnType();
            }
            if (binding instanceof ITypedef) {
                return (ITypedef) binding;
            }
        }
        if (clause instanceof IASTExpression) {
            return ((IASTExpression) clause).getExpressionType();
        }
        return new CPPBasicType(Kind.eInt, 0);
    }

    public static ICPPASTDeclSpecifier getDeclarationSpecifierOfType(IType type) {
        if (type instanceof ICPPBinding) {
            return handleType((ICPPBinding) type);
        } else if (type instanceof IBasicType) {
            return handleType((IBasicType) type);
        } else if (type instanceof IQualifierType) {
            return handleType((IQualifierType) type);
        } else { // undefined variables
            return defaultType();
        }
    }

    private static ICPPASTDeclSpecifier handleType(ICPPBinding type) {
        String typename = ASTTypeUtil.getQualifiedName(type);
        CPPASTNamedTypeSpecifier declspec = new CPPASTNamedTypeSpecifier(new CPPASTName(typename.toCharArray()));
        return declspec;
    }

    private static ICPPASTDeclSpecifier handleType(IBasicType type) {
        CPPASTSimpleDeclSpecifier simpleDeclSpec = new CPPASTSimpleDeclSpecifier();
        simpleDeclSpec.setType(type.getKind());
        return simpleDeclSpec;
    }

    private static ICPPASTDeclSpecifier handleType(IQualifierType qualifiedType) {
        ICPPASTDeclSpecifier specifier = getDeclarationSpecifierOfType(qualifiedType.getType());
        specifier.setConst(qualifiedType.isConst());
        specifier.setVolatile(qualifiedType.isVolatile());
        return specifier;
    }

    private static ICPPASTDeclSpecifier defaultType() {
        CPPASTBaseDeclSpecifier fallBackIntDeclSpec = TypeHelper.getDefaultType();
        fallBackIntDeclSpec.setConst(true);
        return fallBackIntDeclSpec;
    }

    public static boolean hasConstPart(IType type) {
        if (type instanceof ITypeContainer) {
            if (type instanceof IQualifierType) {
                if (((IQualifierType) type).isConst()) {
                    return true;
                }
            }
            return hasConstPart(((ITypeContainer) type).getType());
        }
        return false;
    }

    public static boolean hasVolatilePart(IType type) {
        if (type instanceof ITypeContainer) {
            if (type instanceof IQualifierType) {
                if (((IQualifierType) type).isVolatile()) {
                    return true;
                }
            }
            return hasVolatilePart(((ITypeContainer) type).getType());
        }
        return false;
    }

    public static boolean haveSameType(IASTInitializerClause argument, ICPPParameter parameter) {
        IType paramtype = windDownToRealType(parameter.getType(), true);
        if (argument instanceof IASTExpression) {
            IType argtype = windDownToRealType(((IASTExpression) argument).getExpressionType(), false);
            if (paramtype instanceof ICPPBasicType && argtype instanceof ICPPBasicType) {
                return true;
            }
            if (isString(argument) && isString(paramtype)) {
                return true;
            }
            return parameter.getType().isSameType(argtype);
        }
        return false;
    }

    public static boolean hasSameType(IASTDeclSpecifier one, IASTDeclSpecifier other) {
        if (one instanceof CPPASTNamedTypeSpecifier && other instanceof CPPASTNamedTypeSpecifier) {
            CPPASTNamedTypeSpecifier nameargspec = (CPPASTNamedTypeSpecifier) one;
            CPPASTNamedTypeSpecifier nameparspec = (CPPASTNamedTypeSpecifier) other;
            if (new String(nameargspec.getName().getSimpleID()).equals(new String(nameparspec.getName().getSimpleID()))) {
                return true;
            } else {
                return false;
            }
        } else if (one instanceof CPPASTSimpleDeclSpecifier) {
            if (other instanceof CPPASTSimpleDeclSpecifier) {
                CPPASTSimpleDeclSpecifier simpeargspec = (CPPASTSimpleDeclSpecifier) one;
                CPPASTSimpleDeclSpecifier simpeparspec = (CPPASTSimpleDeclSpecifier) other;
                if (simpeargspec.getType() == simpeparspec.getType()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static IType windDownToRealType(IType type, boolean stopAtTypeDef) {
        return windDownToRealType(type, stopAtTypeDef, false);
    }

    public static IType windDownToRealType(IType type, boolean stopAtTypeDef, boolean stopAtConst) {
        if (type instanceof ITypeContainer) {
            if (stopAtTypeDef && type instanceof ITypedef) {
                return type;
            }
            if (stopAtConst && type instanceof IQualifierType) {
                IQualifierType qualifierType = (IQualifierType) type;
                if (qualifierType.isConst()) {
                    return type;
                }
            }
            type = ((ITypeContainer) type).getType();
            return windDownToRealType(type, stopAtTypeDef, stopAtConst);
        }
        return type;
    }

    public static List<IASTPointerOperator> windDownAndCollectPointerOperators(IType type) {
        return windDownAndCollectPointerOperators(type, new ArrayList<IASTPointerOperator>());
    }

    private static List<IASTPointerOperator> windDownAndCollectPointerOperators(IType type, ArrayList<IASTPointerOperator> pointerOperatorCollector) {
        if (type instanceof ITypeContainer) {
            if (type instanceof ITypedef) {
                return pointerOperatorCollector;
            }
            if (type instanceof IPointerType) {
                IPointerType pointerType = (IPointerType) type;
                CPPASTPointer newPointerOperator = new CPPASTPointer();
                newPointerOperator.setConst(pointerType.isConst());
                newPointerOperator.setVolatile(pointerType.isVolatile());
                newPointerOperator.setRestrict(pointerType.isRestrict());
                pointerOperatorCollector.add(newPointerOperator);
            }
            if (type instanceof IArrayType) {
                pointerOperatorCollector.add(new CPPASTPointer());
            }
            return windDownAndCollectPointerOperators(((ITypeContainer) type).getType(), pointerOperatorCollector);
        }
        Collections.reverse(pointerOperatorCollector);
        return pointerOperatorCollector;
    }

    public static boolean isString(IType type) {
        return type instanceof IBinding && ((IBinding) type).getName().equals("string");
    }

    public static boolean isString(IASTInitializerClause argument) {
        if (argument instanceof IASTLiteralExpression) {
            return isString((CPPASTLiteralExpression) argument);
        }
        return false;
    }

    public static boolean isString(CPPASTLiteralExpression litexpr) {
        return litexpr.getKind() == IASTLiteralExpression.lk_string_literal;
    }

    public static boolean isThisPointer(IASTLiteralExpression litexpr) {
        return litexpr.getKind() == ICPPASTLiteralExpression.lk_this;
    }

    public static ICPPASTCompositeTypeSpecifier getTypeDefinitionOfMember(IASTTranslationUnit unit, IASTName selectedNode,
            CRefactoringContext context) {
        ICPPASTQualifiedName qName = getQualifiedNamePart(selectedNode);
        if (qName != null) {
            return handleQualifiedName(qName);
        }

        ICPPASTFieldReference fieldref = TddHelper.getAncestorOfType(selectedNode, ICPPASTFieldReference.class);
        if (fieldref != null && fieldref.getFieldOwner().getExpressionType() instanceof IType) {
            IASTExpression owner = fieldref.getFieldOwner();
            IType expressionType = owner.getExpressionType();
            IType unwrappedType = SemanticUtil.getNestedType(expressionType, ALLCVQ | TDEF | REF | PTR);
            if (unwrappedType instanceof CPPClassType) {
                CPPClassType cType = (CPPClassType) unwrappedType;
                return cType.getCompositeTypeSpecifier();
            }
            if (unwrappedType instanceof IPDOMCPPClassType) {
                return findTypeDefinitionInIndex(unit, context, (IPDOMCPPClassType) unwrappedType);
            }
            if (unwrappedType instanceof CPPClassSpecialization) {
                ICPPASTCompositeTypeSpecifier specializedSpecifier = ((CPPClassSpecialization) unwrappedType).getCompositeTypeSpecifier();
                if (specializedSpecifier != null) {
                    return specializedSpecifier;
                }
                ICPPClassType specializedBinding = ((CPPClassSpecialization) unwrappedType).getSpecializedBinding();
                if (specializedBinding instanceof CPPClassTemplate) {
                    return ((CPPClassTemplate) specializedBinding).getCompositeTypeSpecifier();
                }
            }
        } else {
            IASTSimpleDeclaration declaration = TddHelper.getAncestorOfType(selectedNode, IASTSimpleDeclaration.class);
            if (declaration != null) {
                final IASTDeclSpecifier type = declaration.getDeclSpecifier();
                if (type instanceof ICPPASTCompositeTypeSpecifier) {
                    return (ICPPASTCompositeTypeSpecifier) type;
                }
            }
        }
        return null;
    }

    public static ICPPASTCompositeTypeSpecifier getTypeDefinitionOfVariable(IASTTranslationUnit unit, IASTName selectedNode,
            CRefactoringContext context) {
        ICPPASTQualifiedName qName = getQualifiedNamePart(selectedNode);
        if (qName != null) {
            selectedNode = qName;
        }
        IBinding b = selectedNode.resolveBinding();
        if (b instanceof CPPVariable) {
            CPPVariable var = (CPPVariable) b;
            IType type = var.getType();
            return getDefinitionOfType(unit, context, type);
        }
        return null;
    }

    private static ICPPASTCompositeTypeSpecifier findTypeDefinitionInIndex(IASTTranslationUnit unit, CRefactoringContext context,
            ICPPClassType cType) {

        try {
            Set<IIndexFile> includedFiles = getIncludedFiles(unit, cType);

            IIndexName[] definitions = unit.getIndex().findDefinitions(cType);
            for (IIndexName def : definitions) {
                final IIndexFile file = def.getFile();
                if (includedFiles.contains(file)) {

                    IPath defPath = new Path(def.getFileLocation().getFileName());
                    ITranslationUnit tu = (ITranslationUnit) CCorePlugin.getDefault().getCoreModel().create(defPath);
                    if (tu != null) {
                        IASTTranslationUnit tuContainingDefinition = context.getAST(tu, new NullProgressMonitor());
                        final IIndexBinding adaptedTypeBinding = tuContainingDefinition.getIndex().adaptBinding(cType);
                        IASTName[] typeDefinitions = tuContainingDefinition.getDefinitionsInAST(adaptedTypeBinding);
                        if (typeDefinitions.length > 0) {
                            IBinding typeBinding = typeDefinitions[0].getBinding();
                            if (typeBinding instanceof CPPClassType) {
                                return ((CPPClassType) typeBinding).getCompositeTypeSpecifier();
                            }
                        }
                    }
                }
            }
        } catch (CoreException e) {}
        return null;
    }

    @SuppressWarnings("deprecation")
    private static Set<IIndexFile> getIncludedFiles(IASTTranslationUnit unit, ICPPClassType cType) throws CoreException {
        IIndexFileLocation ifl = IndexLocationFactory.getIFLExpensive(unit.getFilePath());
        final int cppLinkageID = cType.getLinkage().getLinkageID();
        IIndexFile astFile = unit.getIndex().getFile(cppLinkageID, ifl);

        Set<IIndexFile> includedFiles = new HashSet<>();

        IIndexInclude[] includes = unit.getIndex().findIncludes(astFile);
        for (IIndexInclude include : includes) {
            IIndexFile fileIncludedByInclude = unit.getIndex().getFile(cppLinkageID, include.getIncludesLocation());
            includedFiles.add(fileIncludedByInclude);
        }
        return includedFiles;
    }

    private static ICPPASTCompositeTypeSpecifier handleQualifiedName(ICPPASTQualifiedName qName) {
        ICPPASTNameSpecifier[] nameParts = qName.getAllSegments();
        if (nameParts.length >= 2) {
            ICPPASTNameSpecifier containingScope = nameParts[nameParts.length - 2];
            IBinding scopeBinding = containingScope.resolveBinding();
            if (scopeBinding instanceof IType) {
                IType bareType = windDownToRealType((IType) scopeBinding, false);
                if (bareType instanceof CPPClassType) {
                    return ((CPPClassType) bareType).getCompositeTypeSpecifier();
                }
            }
        }
        return null;
    }

    private static ICPPASTQualifiedName getQualifiedNamePart(IASTName selectedNode) {
        IASTNode parent = selectedNode.getParent();
        if (parent instanceof ICPPASTQualifiedName) {
            return (ICPPASTQualifiedName) parent;
        }
        return null;
    }

    public static IASTNode getTypeDefinitonOfName(IASTTranslationUnit localunit, String typename, CRefactoringContext context) {
        try {
            return TypeHelper.checkIndexBindingForType(typename, localunit, context.getIndex(), context);
        } catch (CoreException e) {
            Activator.log("Exception while finding", e);
        }
        return null;
    }

    private static IASTNode checkIndexBindingForType(String typename, IASTTranslationUnit unit, IIndex index, CRefactoringContext context)
            throws CoreException {
        IIndexBinding[] allBindings = index.findBindings(typename.toCharArray(), false, new IndexFilter() {}, new NullProgressMonitor());
        for (IIndexBinding binding : allBindings) {
            IIndexName[] names = index.findNames(binding, IIndex.FIND_ALL_OCCURRENCES);
            for (IIndexName name : names) {
                IASTTranslationUnit currentTu = unit;
                if (!name.getFileLocation().getFileName().equals(currentTu.getFileLocation().getFileName())) {
                    IPath path = new Path(name.getFileLocation().getFileName());
                    // TODO: Review: assignment to parameter, and this shouts for an else case
                    ITranslationUnit tu = (ITranslationUnit) CCorePlugin.getDefault().getCoreModel().create(path);
                    if (tu != null) {
                        currentTu = context.getAST(tu, new NullProgressMonitor());
                    }
                }
                IASTNode typeSpec = searchTypeInUnit(typename, currentTu);
                if (typeSpec != null) {
                    return typeSpec;
                }
            }
        }
        return null;
    }

    private static IASTNode searchTypeInUnit(final String typeToLookFor, IASTTranslationUnit tu) {
        final NodeContainer container = new NodeContainer();
        tu.accept(new ASTVisitor() {

            {
                shouldVisitDeclarations = true;
                shouldVisitNamespaces = true;
            }

            @Override
            public int visit(IASTDeclaration declaration) {
                if (declaration instanceof CPPASTSimpleDeclaration) {
                    CPPASTSimpleDeclaration spec = ((CPPASTSimpleDeclaration) declaration);
                    if (spec.getDeclSpecifier() instanceof CPPASTCompositeTypeSpecifier) {
                        CPPASTCompositeTypeSpecifier foundType = (CPPASTCompositeTypeSpecifier) spec.getDeclSpecifier();
                        String foundTypeName = foundType.getName().toString();
                        if (typeToLookFor.equals(foundTypeName)) {
                            container.add(foundType);
                            return PROCESS_ABORT;
                        }
                    }
                }
                return PROCESS_CONTINUE;
            }

            @Override
            public int visit(ICPPASTNamespaceDefinition nsdef) {
                String nsName = new String(nsdef.getName().getSimpleID());
                if (nsName.equals(typeToLookFor)) {
                    container.add(nsdef);
                    return PROCESS_ABORT;
                }
                return PROCESS_CONTINUE;
            }
        });
        if (container.size() > 0) {
            return container.getNodesToWrite().get(0);
        }
        return null;
    }

    public static CPPASTBaseDeclSpecifier getDefaultType() {
        CPPASTBaseDeclSpecifier spec = new CPPASTSimpleDeclSpecifier();
        ((IASTSimpleDeclSpecifier) spec).setType(IASTSimpleDeclSpecifier.t_int);
        return spec;
    }

    public static CPPASTBaseDeclSpecifier getDefaultReturnType() {
        CPPASTBaseDeclSpecifier spec = new CPPASTSimpleDeclSpecifier();
        ((IASTSimpleDeclSpecifier) spec).setType(IASTSimpleDeclSpecifier.t_void);
        return spec;
    }

    public static CPPASTBaseDeclSpecifier findTypeInAst(IASTNode startNode, ISelection selection) {
        final NodeContainer c = new NodeContainer();
        PossibleReturnTypeFindVisitor finder = new PossibleReturnTypeFindVisitor(selection, c);
        startNode.accept(finder);

        if (finder.hasFound()) {
            IASTNode node = finder.getType();
            if (node != null && node instanceof CPPASTSimpleDeclSpecifier) {
                return (CPPASTSimpleDeclSpecifier) node.copy(CopyStyle.withoutLocations);
            } else if (node instanceof CPPASTNamedTypeSpecifier) {
                return (CPPASTNamedTypeSpecifier) node.copy(CopyStyle.withoutLocations);
            }
        }
        return null;
    }

    public static IASTNode getMemberOwner(IASTTranslationUnit localunit, IASTName selectedNode, CRefactoringContext context) {
        IBinding binding = selectedNode.resolveBinding();

        if (binding != null) {
            IBinding owner = binding.getOwner();

            if (owner instanceof CPPNamespace) {
                ICPPNamespaceScope scope = ((CPPNamespace) owner).getNamespaceScope();
                if (scope instanceof CPPScope) {
                    return ((CPPScope) scope).getPhysicalNode();
                }
            }
        }
        return getTypeDefinitionOfMember(localunit, selectedNode, context);
    }

    public static ICPPASTCompositeTypeSpecifier getTypeOfMember(IASTTranslationUnit unit, IASTName selectedNode, CRefactoringContext context) {
        IBinding b = selectedNode.resolveBinding();
        if (b instanceof ICPPMember) {
            ICPPMember var = (ICPPMember) b;
            IType type = var.getClassOwner();
            return getDefinitionOfType(unit, context, type);
        }
        return null;
    }

    private static ICPPASTCompositeTypeSpecifier getDefinitionOfType(IASTTranslationUnit unit, CRefactoringContext context, IType type) {
        type = TypeHelper.windDownToRealType(type, false);
        if (type instanceof ICPPClassSpecialization) {
            type = ((ICPPClassSpecialization) type).getSpecializedBinding();
        }
        if (type instanceof CPPClassTemplate) {
            return ((CPPClassTemplate) type).getCompositeTypeSpecifier();
        }
        if (type instanceof CPPClassType) {
            return ((CPPClassType) type).getCompositeTypeSpecifier();
        }
        if (type instanceof ICPPClassType) {
            return findTypeDefinitionInIndex(unit, context, (ICPPClassType) type);
        }
        return null;
    }
}
